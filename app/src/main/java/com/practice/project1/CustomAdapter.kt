package com.practice.project1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

data class CustomItem(
    var id: Int, var data: String, var detail: String
) : Serializable

class CustomAdapter(
    private var itemList: ArrayList<CustomItem>,
    context: Context,
    private val clickEvents: AdapterClickEvents
) :
    RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    private var adapterList = ArrayList<CustomItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_list_item, parent, false)
        return CustomViewHolder(view, clickEvents)
    }

    override fun getItemCount(): Int {
        return adapterList.size
    }

    override fun onBindViewHolder(
        holder: CustomViewHolder,
        position: Int,
    ) {
        holder.onBind(holder.adapterPosition, itemList[position])
    }

    fun setList(updatedItemList: ArrayList<CustomItem>) {
        val diffUtil = CustomDiffUtil(adapterList, updatedItemList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        adapterList.clear()
        adapterList.addAll(updatedItemList)
        diffResult.dispatchUpdatesTo(this)
    }

    class CustomViewHolder(
        view: View, private val clickEvents: AdapterClickEvents
    ) :
        RecyclerView.ViewHolder(view) {
        private val customText: TextView = view.findViewById(R.id.customText)
        private val customEdit: ImageView = view.findViewById(R.id.customEdit)
        private val customDelete: ImageView = view.findViewById(R.id.customDelete)
        private val customId: TextView = view.findViewById(R.id.customId)
        private val customTextDetail: TextView = view.findViewById(R.id.customTextDetail)

        fun onBind(position: Int, item: CustomItem) {
            (position+1).toString().also { customId.text = it }
            customText.text = item.data
            customTextDetail.text = item.detail
            customDelete.setOnClickListener {
                clickEvents.deleteClickCallback(position)
            }
            customEdit.setOnClickListener {
                clickEvents.editClickCallback(position)
            }
        }
    }

    class CustomDiffUtil(
        private var oldList: ArrayList<CustomItem>,
        private var newList: ArrayList<CustomItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].data == newList[newItemPosition].data && oldList[oldItemPosition].detail == newList[newItemPosition].detail
        }

    }
}