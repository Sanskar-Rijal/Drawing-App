package com.example.paintinandroid

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var drawingview:Drawingview? = null
    private  var mImageButtonCurrentPaint :ImageButton? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingview =findViewById(R.id.showdrawing)
        drawingview?.brushsize(10.toFloat())
        //in order to use mIMagebuttoncurrentpaint we have to setup linear layout first
        val linearLayoutPaintColors =findViewById<LinearLayout>(R.id.linear_paint_color)

        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton //we want to use item at positon 1 but we want it to be treated as image button
        //items in linear layout are in image view so no problem

        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.color_pallet_pressedl)
        )

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