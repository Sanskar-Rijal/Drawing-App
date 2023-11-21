package com.example.paintinandroid

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
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
    fun paintclicked (view: View)
    {
        if (view!== mImageButtonCurrentPaint) //if it is clicked different position than current then we need to change color
        {
            val imageButton =view as ImageButton //we are taking the view from function in which we are clicking on
            //it may be text view,image view etc but in our case we are making sure only image button has paintclicked method assigned to it
            //so view always is going to be image button so converting it to image button

            val getcolor =imageButton.tag.toString()//we created tag for ever single image button just to know the # value of color

            drawingview?.setcolor(getcolor)
            //if its selected then we must change ui to color_pressed
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.color_pallet_pressedl)
            )//image button is the current button we pressed so changing it's color to pressed


            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.color_pallet_normal) //mimagebuttoncurrentpaint is the previous image view which was pressed making it normal

            )
            mImageButtonCurrentPaint = view //At beginning mimagebuttoncurrentpaint was at black now when  clicked on red then the value of
            //mimagebuttoncurrentpaint is still black but it should be red,again when yellow is pressed mimagebuttoncurrentpaint should be red because it
            //represents just the value before so we used mImageButtonCurrentPaint = view so that it works properly
            
        }
    }
}