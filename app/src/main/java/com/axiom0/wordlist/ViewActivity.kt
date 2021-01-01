package com.axiom0.wordlist

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_view.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.collections.ArrayList
import kotlinx.coroutines.*
import okhttp3.internal.wait
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ViewActivity() : Activity(){

    private var wList : ArrayList<ArrayList<String>> = arrayListOf(arrayListOf<String>(), arrayListOf<String>())
    private val LEFT = 0
    private val RIGHT = 1

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
            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_word).setMessage("")
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
            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_word).setMessage("")
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






        btnEdit.setOnLongClickListener {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL


            val sideSpinner = Spinner(this)
            ArrayAdapter.createFromResource(this, R.array.translate_source_side, android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                sideSpinner.adapter = adapter
            }

            val sourceSpinner =  Spinner(this)
            ArrayAdapter.createFromResource(this, R.array.translate_language, android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                sourceSpinner.adapter = adapter
            }


            val targetSpinner =  Spinner(this)
            ArrayAdapter.createFromResource(this, R.array.translate_language, android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                targetSpinner.adapter = adapter
            }

            linearLayout.addView(sideSpinner)
            linearLayout.addView(sourceSpinner)
            linearLayout.addView(targetSpinner)

            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_word).setMessage("")
                .setView(linearLayout)
            dialog.setPositiveButton(R.string.ok) { _, _ ->
                val sourceSide = translateSideConvert(sideSpinner.selectedItem.toString())
                val sourceLanguage = translateLanguageConvert(sourceSpinner.selectedItem.toString())
                val targetLanguage = translateLanguageConvert(targetSpinner.selectedItem.toString())

                Log.d("TAG",sourceLanguage)
                Log.d("TAG",targetLanguage)

                runBlocking {
                    translate(sourceSide, sourceLanguage, targetLanguage)

                }
                setListToView()
                saveCSV()

                Log.d("TAG", "RESULT!!!!!!!!!!!!!!")
                for(i in wList[1]){
                    Log.d("TAG",i)
                }
                Log.d("TAG", "RESULT!!!!!!!!!!!!!!")
            }
                .setNegativeButton(R.string.cancel, null)
                .show()

            return@setOnLongClickListener true
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


    //sourceLanguage and targetLanguage is autoTranslateHelper.ENGLISH or autoTranslateHelper.JAPANESE
    private suspend fun translate(sourceSide: Int, sourceLanguage: String, targetLanguage: String){
        var autoTranslateHelper = AutoTranslateHelper()

        val targetSide = changeSide(sourceSide)
        for(i in 0 until wList[targetSide].size){
            Log.d("TAG",i.toString())
                if (wList[targetSide][i]==""){
                    Log.d("TAG","i void")
                    wList[targetSide][i]= request(autoTranslateHelper, sourceSide, sourceLanguage, targetLanguage, i).await()
                    Log.d("TAG",wList[targetSide][i])
                }
        }
    }




    private suspend fun request(autoTranslateHelper: AutoTranslateHelper, sourceSide: Int,
                                sourceLanguage: String, targetLanguage: String, i : Int): Deferred<String> {
        val targetSide = changeSide(sourceSide)
        return GlobalScope.async {
            return@async autoTranslateHelper.callback(wList[sourceSide][i], sourceLanguage, targetLanguage)
        }
    }

    private fun changeSide(side: Int): Int{
        return if(side== LEFT) RIGHT else LEFT
    }

    private fun translateSideConvert(sourceSideString: String): Int{
        return if(sourceSideString=="Left") LEFT else RIGHT
    }

    private fun translateLanguageConvert(languageString: String): String{
        var language = AutoTranslateHelper.ENGLISH
        when(languageString){
            "English" -> {
                language = AutoTranslateHelper.ENGLISH
            }
            "Japanese" -> {
                language = AutoTranslateHelper.JAPANESE
            }
            "Chinese" ->{
                language = AutoTranslateHelper.CHINESE
            }
        }
        return  language
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


