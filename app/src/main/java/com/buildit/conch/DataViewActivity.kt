package com.buildit.conch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_data_view.*

class DataViewActivity : AppCompatActivity() {


    val numberList: MutableList<Colors> = ArrayList()
    var page = 1
    var isLoading = false
    val limit = 10
    var colorCodes: List<Colors> = ArrayList()

    lateinit var adapter: NumberAdapter
    lateinit var layoutManager: LinearLayoutManager

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



        recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if(dy > 0) {
                val visibleItemCount  = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                val total = adapter.itemCount
                if(!isLoading) {
                    if((visibleItemCount + pastVisibleItem) >= total) {
                        page++
                        getPage()
                    }
                }
//                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }


    fun getPage() {
        isLoading = true
        progressBar.visibility = View.VISIBLE
        val start = (page-1) * limit
        val end = (page) * limit

        for(i in start..end) {
            numberList.add(colorCodes[i])
        }
        Handler().postDelayed({
            if(::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
            } else {
                adapter = NumberAdapter(this)
                recyclerView.adapter = adapter
            }
            isLoading = false
            progressBar.visibility = View.GONE
        }, 0)
    }

    class NumberAdapter(val activity: DataViewActivity): RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {
        class NumberViewHolder(v: View): RecyclerView.ViewHolder(v) {
            val tvNumber = v.findViewById<TextView>(R.id.rv_number)
            val tvColor = v.findViewById<ImageView>(R.id.my_image_view)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            return NumberViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_number,parent,false))
        }

        override fun getItemCount(): Int {
            return activity.numberList.size
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.tvNumber.text = activity.numberList[position].name
            holder.tvNumber.setTextColor(Color.parseColor(activity.numberList[position].hex))

            val bitmap: Bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            // oval positions
            val left = 100
            val top = 100
            val right = 800
            val bottom = 800
            // draw oval shape to canvas
            val shapeDrawable = ShapeDrawable(OvalShape())
            shapeDrawable.setBounds( left, top, right, bottom)

            shapeDrawable.paint.color = Color.parseColor(activity.numberList[position].hex)
            shapeDrawable.draw(canvas)

            // now bitmap holds the updated pixels

            // set bitmap as background to ImageView
            holder.tvColor.setImageBitmap(bitmap)
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
        colorCodes = gson.fromJson(jsonFileString, listColors)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        getPage()
//        colors.forEachIndexed { idx, color -> Log.i("data", "> Item $idx:\n$color") }

    }
}

data class Colors(val name: String, val hex: String) {
}






