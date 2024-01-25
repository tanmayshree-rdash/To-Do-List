package com.practice.project1

import android.content.Context
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class SaveDataFile {

    private var filename = "customlistdata.dat"

    fun write(list: ArrayList<CustomItem>, context: Context) {

        val fos : FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(list)
        oos.close()

    }

    fun read(context: Context): ArrayList<CustomItem> {

        var listItem : ArrayList<CustomItem>

        try {
            val fis : FileInputStream = context.openFileInput(filename)
            val ois = ObjectInputStream(fis)
            listItem = ois.readObject() as ArrayList<CustomItem>
            fis.close()
        }

        catch (ex : Exception) {
            listItem = ArrayList()
        }

        return listItem

    }
}