package com.practice.project1

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), AdapterClickEvents {

    private lateinit var addText: EditText
    private lateinit var addTextDetail: EditText
    private lateinit var addButton: Button
    private lateinit var toDoList: RecyclerView
    private var counter: Int = 0

    private lateinit var list: ArrayList<CustomItem>

    private var saveDataFile = SaveDataFile()
    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addText = findViewById(R.id.addText)
        addTextDetail = findViewById(R.id.addTextDetail)
        addButton = findViewById(R.id.addButton)
        toDoList = findViewById(R.id.toDoList)

        toDoList.layoutManager = LinearLayoutManager(this)

        list = saveDataFile.read(this)

        customAdapter = CustomAdapter(list, this, this)
        toDoList.adapter = customAdapter
        customAdapter.setList(list)

        addButton.setOnClickListener {
            addItem()
        }

        addText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                Handler(Looper.getMainLooper()).postDelayed({
                    addTextDetail.requestFocus()
                }, 200)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        addTextDetail.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                addItem()
                hideKeyboard(addTextDetail)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }

    private fun addItem() {
        val newItem: String = addText.text.toString()
        val newItemDetail: String = addTextDetail.text.toString()

        if (newItem.isNotEmpty() && newItemDetail.isNotEmpty()) {
            var id: Int = 0
            if(list.isNotEmpty()) {
                id = list[list.size-1].id+1
            }
            val newItemObj = CustomItem(id, newItem, newItemDetail)
            list.add(newItemObj)
            addText.setText("")
            addTextDetail.setText("")
            saveDataFile.write(list, applicationContext)
            customAdapter.setList(list)
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun deleteClickCallback(position: Int) {
        showAlertDialogDelete(list[position], position)
    }

    override fun editClickCallback(position: Int) {
        showAlertDialogEdit(list[position], position)
    }

    private fun showAlertDialogDelete(item: CustomItem, position: Int) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Delete")
        alert.setMessage("Do you want to delete \"${item.data}\"\n${item.detail} ?")
        alert.setCancelable(false)
        alert.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, i ->
                dialog.cancel()
            })
        alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, i ->
            list.removeAt(position)
            saveDataFile.write(list, applicationContext)
            customAdapter.setList(list)
            customAdapter.notifyItemRangeChanged(position, list.size - position)
        })
        alert.create().show()
    }

    private fun showAlertDialogEdit(item: CustomItem, position: Int) {

        val inputText = EditText(this)
        inputText.setText(item.data)
        val inputTextDetail = EditText(this)
        inputTextDetail.setText(item.detail)

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(inputText)
        linearLayout.addView(inputTextDetail)

        val alert = AlertDialog.Builder(this)
        alert.setTitle("Edit")
        alert.setView(linearLayout)
        alert.setCancelable(false)
        alert.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, i ->
                dialog.cancel()
            })
        alert.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, i ->
            val newInputText = inputText.text.toString()
            val newInputTextDetail = inputTextDetail.text.toString()
            if (newInputText.isNotEmpty() && newInputTextDetail.isNotEmpty()) {
                list[position].data = newInputText
                list[position].detail = newInputTextDetail
                saveDataFile.write(list, applicationContext)
                customAdapter.setList(list)
                customAdapter.notifyItemChanged(position)
            }
        })
        alert.create().show()
    }
}

interface AdapterClickEvents {
    fun deleteClickCallback(position: Int)
    fun editClickCallback(position: Int)
}