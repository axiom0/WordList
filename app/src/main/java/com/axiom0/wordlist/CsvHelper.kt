package com.axiom0.wordlist

import android.content.res.AssetManager
import java.io.*
import java.util.ArrayList

class CsvHelper(assets : AssetManager) {
    var leftList: ArrayList<String> = arrayListOf()
    var rightList: ArrayList<String> = arrayListOf()
    val assets = assets
    val list: Array<String> = assets.list("csv_data/")  as Array<kotlin.String>

    fun openCSV(filename: String) {
        if(!list.contains(filename)){

        }
        try {
            val file = assets.open(filename)
            val fileReader = BufferedReader(InputStreamReader(file))
            fileReader.forEachLine {
                if (it.isNotBlank()) {
                    val line = it.split(",").toTypedArray()
                    leftList.add(line[0])
                    rightList.add(line[1])
                }
            }
        } catch (e: IOException) {
            print(e)
        }
    }




}


