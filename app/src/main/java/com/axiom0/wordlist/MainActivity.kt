package com.axiom0.wordlist

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity() {
    private var leftList:ArrayList<String> = arrayListOf()
    private var rightList:ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var data = readCsv(filename)
//        setDataVisible(data)
//        setClickEvent()

        for (i in 1..100){
            leftList.add("LEFT$i")
            rightList.add("RIGHT$i")
        }

        val adapterL  = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, leftList)
        val adapterR  = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, rightList)
        lv_left.adapter = adapterL
        lv_right.adapter = adapterR

    }

    fun addText(text : String, layout : ViewGroup){

    }
}