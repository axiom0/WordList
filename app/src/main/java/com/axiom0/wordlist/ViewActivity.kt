package com.axiom0.wordlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_view.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.collections.ArrayList

class ViewActivity : Activity() {

    private var wList : ArrayList<ArrayList<String>> = arrayListOf(arrayListOf<String>(), arrayListOf<String>())

    private var isEditable = false
    private var title = ""
    private var path = ""
    private var isNew = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        title = intent.getStringExtra("filename") as String
        isNew = intent.getBooleanExtra("isNew", true)
        path = "$filesDir/$title.csv"
        Log.d("PATH", path)

        readCSV()
        setListToView()

        lv_left.setOnItemClickListener { _, view, position, _ ->
            val itemTextView : TextView = view.findViewById(android.R.id.text1)

            val editText = EditText(this)
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.setText(itemTextView.text)
            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_title).setMessage("")
                .setView(editText)
            dialog.setPositiveButton(R.string.ok) { _, _ ->
                val t :String = editText.text.toString()
                wList[0][position] = t
                setListToView()
            }
                .setNegativeButton(R.string.cancel, null)
                .show()

        }
        lv_right.setOnItemClickListener { _, view, position, _ ->
            val itemTextView : TextView = view.findViewById(android.R.id.text1)

            val editText = EditText(this)
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.setText(itemTextView.text)
            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_title).setMessage("")
                .setView(editText)
            dialog.setPositiveButton(R.string.ok) { _, _ ->
                val t :String = editText.text.toString()
                wList[1][position] = t
                setListToView()
            }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        lv_left.isClickable = true
        lv_right.isClickable = true

        btnAdd.setOnClickListener {
            if(isEditable){
                addTextToList()
                setListToView()
            }
            input_left.text.clear()
            input_right.text.clear()
        }


        val inputLLYScale = inputLL.scaleY
        inputLL.scaleY = 0f

        btnEdit.setOnClickListener {
            isEditable = true

            btnEdit.isClickable = false
            btnCheck.isClickable = true
            btnEdit.visibility = View.INVISIBLE
            btnCheck.visibility = View.VISIBLE
            inputLL.scaleY = inputLLYScale

            lv_left.isClickable = false
            lv_right.isClickable = false
        }

        btnCheck.setOnClickListener {
            isEditable = false

            btnEdit.isClickable = true
            btnCheck.isClickable = false
            btnEdit.visibility = View.VISIBLE
            btnCheck.visibility = View.INVISIBLE
            inputLL.scaleY = 0f

            lv_left.isClickable = true
            lv_right.isClickable = true
            saveCSV()
        }

    }

    override fun onPause() {
        super.onPause()
        saveCSV()
    }

    private fun addTextToList() {
        val l = input_left.text
        val r = input_right.text
        wList[0].add(l.toString())
        wList[1].add(r.toString())
    }

    private fun setListToView() {
        val adapterL = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wList[0])
        val adapterR = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wList[1])
        lv_left.adapter = adapterL
        lv_right.adapter = adapterR
    }

    private fun setClickEvent(){

    }


    private fun readCSV(){
        val readFile = File(path)
        if(isNew){
            readFile.createNewFile()
        }else{
            readFile.forEachLine {
                Log.d("TAG", it)
                val t = it.split(",")
                wList[0].add(t[0])
                wList[1].add(t[1])
            }
        }
    }

    private fun saveCSV(){
        if(wList[0].size != 0){
            val writeFile = File(path)
            val bw = BufferedWriter(FileWriter(writeFile))
            for(i in 0 until wList[0].size){
                val s = wList[0][i] + "," + wList[1][i]+"\n"
                bw.write(s)
            }
            bw.close()
        }
    }

    private fun deleteInput(){

    }

    private fun isFileExist(): Boolean {
        return File(path).exists()
    }
}


