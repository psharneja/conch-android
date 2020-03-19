package com.buildit.conch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.content.Context
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataViewActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_view)
        val button = findViewById<Button>(R.id.moveToQRView)
        getData()
        button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


    fun getData() {
        val jsonFileString = getJsonDataFromAsset(applicationContext, "colornames.json")

        val gson = Gson()
        val listColors = object : TypeToken<List<Colors>>() {}.type
        var colors: List<Colors> = gson.fromJson(jsonFileString, listColors)
        colors.forEachIndexed { idx, color -> Log.i("data", "> Item $idx:\n$color") }

    }
}

data class Colors(val name: String, val hex: String) {
}






