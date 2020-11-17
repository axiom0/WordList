package com.axiom0.wordlist

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class MainActivity : Activity() {
    private val tag = "AXIOM"
    private var leftList: ArrayList<String> = arrayListOf()
    private var rightList: ArrayList<String> = arrayListOf()
    private var isEditable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val filename: String = intent.getStringExtra("filename") as String
        CsvHelper(resources.assets).openCSV(filename)
        setListToView()

//        var data = readCsv(filename)
//        setDataVisible(data)
//        setClickEvent()

        btnAdd.setOnClickListener {
            addTextToList()
            setListToView()
        }

        edit_btn.setOnClickListener {
            isEditable = !isEditable
//            if(isEditable){
//                edit_btn.setImageResource(R.drawable.)
//            }else{
//                edit_btn.setImageResource(R.drawable.)
//            }
        }

//        val file = resources.assets.open(filename)

    }

    fun addTextToList() {
        val l = input_left.text
        val r = input_right.text
        leftList.add(l.toString())
        rightList.add(r.toString())
    }

    fun setListToView() {
        val adapterL = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, leftList)
        val adapterR = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rightList)
        lv_left.adapter = adapterL
        lv_right.adapter = adapterR
    }

    fun openCSV(file: InputStream){

    }
}


