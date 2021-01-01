package com.axiom0.wordlist

import android.util.JsonReader
import android.util.Log
import okhttp3.*
import okhttp3.Request;
import org.json.JSONObject
import java.io.IOException


class AutoTranslateHelper {
    private val urlBase ="https://script.google.com/macros/s/AKfycbyl20b2l9u1Aw1SJR8x9evTk1hFTomtKjt-hcalfEEYPG5P-q8/exec"


    companion object{
        const val ENGLISH = "en"
        const val JAPANESE = "ja"
        const val CHINESE = "zh-CN"
    }


    fun request(text: String, source: String, target: String): String{

        var r = "?text=$text&source=$source&target=$target"
        val url = urlBase + r

        val httpClient = HttpClient()
        httpClient.getWithUrlString(url)

        return httpClient.text
    }

    fun callback (text: String, source: String, target: String): String{
        var r = "?text=$text&source=$source&target=$target"
        val url = urlBase + r

        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .build()
        var t = ""
        client.newCall(request).execute().use {

            val str = it.body!!.string()
            Log.d("TAG", "Status code: " + it.code)
            Log.d("TAG", "Body: $str")

            val body = JSONObject(str)
            if(body["code"]==200){
                t = body["text"].toString()
            }
        }
        return t
    }

}

class HttpClient : Callback {
    var text = ""
    fun getWithUrlString(url: String) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.d("TAG", "onFailure")
    }


    override fun onResponse(call: Call, response: Response) {
        val str = response.body!!.string()
        Log.d("TAG", "Status code: " + response.code)
        Log.d("TAG", "Body: $str")

        val body = JSONObject(str)
        if(body["code"]==200){
            text = body["text"].toString()
        }
    }
}