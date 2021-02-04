package com.axiom0.wordlist

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_start.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class StartActivity :AppCompatActivity() {
    private val title = "title"
    var tList: List<String> = ArrayList<String>(listOf(""))

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        //---------only for development----------
//        clearPrefs()
        //---------------------------------------


        initList()



        new_btn.setOnClickListener {
            val inputTitle = EditText(this)
            inputTitle.inputType = InputType.TYPE_CLASS_TEXT
            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_title).setMessage("")
                .setView(inputTitle)
            dialog.setPositiveButton(R.string.ok) { _, _ ->
                val filename:String = inputTitle.text.toString()
                when {
                    filename == ""  -> {
                        AlertDialog.Builder(this).setMessage(R.string.cant_use_void).show()
                    }
                    filename.contains(",")  ->  {
                        AlertDialog.Builder(this).setMessage(R.string.cant_use_comma).show()
                    }
                    tList.contains(filename)  ->  {
                        AlertDialog.Builder(this).setMessage(R.string.already_exists).show()
                    }
                    else  -> {
                        (tList as ArrayList<String>).add(filename)
                        saveTitleList(tList)
                        openViewer(filename, true)
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
        }

        title_list.setOnItemClickListener { _, view, _, _ ->
            val itemTextView : TextView = view.findViewById(android.R.id.text1)
            val s = itemTextView.text.toString()

            Log.i("TAG", s)
            if(s==""){
                AlertDialog.Builder(this).setMessage(R.string.alert_void).show()
            }else{
                openViewer(s, false)
            }



        }

        title_list.setOnItemLongClickListener { _, view, _, _ ->
            val itemTextView : TextView = view.findViewById(android.R.id.text1)
            val s = itemTextView.text.toString()

            if(s==""){
                AlertDialog.Builder(this).setMessage(R.string.alert_void).show()
            }else{
                val dialog = AlertDialog.Builder(this).setTitle(R.string.check_export).setMessage(Environment.DIRECTORY_DCIM)
                dialog.setPositiveButton(R.string.ok) { _, _ ->
                    exportCSV(s)
                }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }



            return@setOnItemLongClickListener true
        }


    }

    override fun onResume() {
        super.onResume()
        initList()
    }

    private fun initList(){
        tList = getTitleList()
        if(tList[0]=="" && tList.size==1){

        }else if (tList[0]=="" && tList.size>=2){
            (tList as ArrayList<String>).removeAt(0)
            title_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tList)
        }else{
            title_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tList)
        }
    }

    private fun openViewer(filename: String, isNew: Boolean){
        Log.d("TAG", filename)
        val intent = Intent(this, ViewActivity::class.java)
        intent.putExtra("filename", filename)
        intent.putExtra("isNew", isNew)
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    private fun getTitleList(): ArrayList<String> {
        val preferences : SharedPreferences = this.getSharedPreferences(title, Context.MODE_PRIVATE)
        var titleString:String = preferences.getString(title, "")?: ""

        return ArrayList(titleString.split(","))
    }

    private fun saveTitleList(titleList: List<String>){
        val sb: StringBuilder = StringBuilder()
        for (t in titleList){
            sb.append(t).append(",")
        }
        sb.setLength(sb.length - 1)

        val preferences : SharedPreferences = this.getSharedPreferences(title, Context.MODE_PRIVATE)
        val editer = preferences.edit()
        editer.apply {
            putString(title, sb.toString())
            commit()
        }
    }

    private fun exportCSV(title : String){
        val state: String = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) {
            val myDirName = "WordList"
            val myDir = File(getExternalFilesDir(Environment.DIRECTORY_DCIM), myDirName)
            if (!myDir.exists()) {
                myDir.mkdirs()
            }

            val readFile = File("$filesDir/$title.csv")

            val saveFile = File(myDir, "$title.csv")
            try {
                readFile.copyTo(saveFile, overwrite = true)
            } catch (e: IOException) {
            }
        }
    }


    //only for development
    private fun clearPrefs(){
        this.getSharedPreferences(title, Context.MODE_PRIVATE).edit().clear().commit()
    }
}