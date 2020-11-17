package com.axiom0.wordlist

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity :AppCompatActivity() {
    private var path: String = "/"
    val folder = "csv_data/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

//        getTitleList()
//        layoutID.setTitleList()
//        setClickEvents

        val fileList: Array<String> = resources.assets.list(folder) as Array<String>

        new_btn.setOnClickListener {
            val inputTitle = EditText(this)
            val dialog = AlertDialog.Builder(this).setTitle(R.string.input_title).setMessage("")
                .setView(inputTitle)

                dialog.setPositiveButton(R.string.ok) { _, _ ->
                    val filename:String = inputTitle.text.toString() + ".csv"
                    if(fileList.contains(filename)){
                        dialog.setMessage(R.string.already_exists).show()
                    }else{
                        openViewer(filename)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun openViewer(filename: String){
        Log.d("TAG", filename)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("filename", filename)
        startActivity(intent)
    }


}