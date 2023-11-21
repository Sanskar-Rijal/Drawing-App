package com.example.paintinandroid

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private var drawingview:Drawingview? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingview =findViewById(R.id.showdrawing)
        drawingview?.brushsize(10.toFloat())
        val brushsize_btn: ImageButton = findViewById(R.id.click_brush)
        brushsize_btn.setOnClickListener {
            showbrushsizechooserdialog()
        }
    }
    private fun showbrushsizechooserdialog()
    {
        //creating a dialogue i.e popup we need to create an object
        val brushDialog =Dialog(this)
        brushDialog.setContentView(R.layout.brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallbtn:ImageButton = brushDialog.findViewById(R.id.small_brush)
        smallbtn.setOnClickListener {
            drawingview?.brushsize(10.toFloat())
            brushDialog.dismiss()//i want brush dialouge to be dismissed
        }
        val mediumbtn:ImageButton =brushDialog.findViewById(R.id.medium_brush)
        mediumbtn.setOnClickListener {
            drawingview?.brushsize(20.toFloat())
            brushDialog.dismiss()
        }
        val largebtn:ImageButton=brushDialog.findViewById(R.id.large_brush)
        largebtn.setOnClickListener {
            drawingview?.brushsize(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
}