package com.example.paintinandroid

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.icu.text.CaseMap.Title
import android.media.Image
import android.media.MediaScannerConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var drawingview:Drawingview? = null
    private  var mImageButtonCurrentPaint :ImageButton? =null
    var customprogressDialog:Dialog?=null //creating a progress dialog
    /**
     * creating show progress dialog
     */
    private fun showprogressDialog()
    {
        customprogressDialog= Dialog(this@MainActivity)
        /**
         * set the screen content from a layout resource
         * The resource will be inflated ,adding all top-level views to the screen
         */
        customprogressDialog?.setContentView(R.layout.progressbar)
        customprogressDialog?.show()//now to start the dialogue we use .show()
    }
    /**
     * function to cancel progress dialogue
     */
    private fun cancelprogressdialogue()
    {
        if(customprogressDialog !=null)
        {
            customprogressDialog?.dismiss()
            customprogressDialog=null
        }
    }
    /**
     * launcher for opening gallery
     */
    val opengalleryLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
            result ->
        if(result.resultCode == RESULT_OK && result.data!=null)
        {
            val imagebackground:ImageView=findViewById(R.id.imgview_background)
            //we get uri so .setimgURI is choosed
            imagebackground.setImageURI(result.data?.data)//we are using the location of the data
        }
    }

    /**
     * sharing image
     */
    private fun shareImage(result:String)
    {
        MediaScannerConnection.scanFile(this,arrayOf(result),null)
        {
            path,uri ->
            val shareIntent =Intent()
            shareIntent.action = Intent.ACTION_SEND //using action in intent to perform operations
            shareIntent.putExtra(Intent.EXTRA_STREAM,uri)//passing where the image is on the device
            shareIntent.type="image/png"
            startActivity(Intent.createChooser(shareIntent,"Share"))
        }
    }
    private var req_for_permission:ActivityResultLauncher<Array<String>>
            =registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    {
            permission->
        permission.entries.forEach{
            val permissionName=it.key //it value is of type boolean
            val isgranted=it.value//so it will be either true of false
            if (isgranted)
            {
                Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show()
                /**
                 * now opening gallery using intent
                 */
                val pick_intent =Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //we have to find a way to start this intent and select the image so we need to create a launcher
                opengalleryLauncher.launch(pick_intent)
            }
            else
            {
                if(permissionName==Manifest.permission.READ_EXTERNAL_STORAGE)
                {
                    Toast.makeText(this,"oops you denied the permission",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //to store data
    private fun getBitmapFromView(view: View):Bitmap
    {
        val returnedBitmap =Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val backgrounddraw=view.background
        if(backgrounddraw !=null) //if there is back ground drawing it into canvas
        {
            backgrounddraw.draw(canvas)
        }
        else
        {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }
    private suspend fun saveBitmapfile(mBitmap: Bitmap?):String //making courriteis
    {
        var result=" "
        withContext(Dispatchers.IO)//to use withcontex we need dependencies beta01...
        {
            //first we check if bit map is null ornot
            if(mBitmap!=null)
            {
                try {
                    val bytes =ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    //now we create a file out of it
                    val f= File(externalCacheDir?.absoluteFile.toString()
                            + File.separator + "paintinandroid_" + System.currentTimeMillis()/10000 + ".png")
                    val fo=FileOutputStream(f)
                    fo.write(bytes.toByteArray()) //it will take our output and make array outof if
                    fo.close()
                    result =f.absolutePath
                    runOnUiThread {
                        cancelprogressdialogue()
                        if(result.isNotEmpty())
                        {
                            Toast.makeText(this@MainActivity,"File saved successfully $result",
                                Toast.LENGTH_SHORT).show()
                            shareImage(result)
                        }
                        else
                        {
                            Toast.makeText(this@MainActivity,
                                "something went wrong",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e:Exception)
                {
                    result=" "
                    e.printStackTrace()
                }
            }
        }
        return result
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingview =findViewById(R.id.showdrawing)
        drawingview?.brushsize(10.toFloat())
        //in order to use mIMagebuttoncurrentpaint we have to setup linear layout first
        val linearLayoutPaintColors =findViewById<LinearLayout>(R.id.linear_paint_color)
        //getting rid of grey effect on xml
        /**
         * creating save button
         */
        val save :ImageButton=findViewById(R.id.click_save)
        save.setOnClickListener {
            if(isReadStorageAllowed())
            {
                showprogressDialog()
                lifecycleScope.launch {
                    val fldrawingview:FrameLayout = findViewById(R.id.frm_lyt_container)
                    val myBitmap: Bitmap= getBitmapFromView(fldrawingview)
                    saveBitmapfile(myBitmap)
                }
            }
        }

        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton //we want to use item at positon 1 but we want it to be treated as image button
        //items in linear layout are in image view so no problem

        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.color_pallet_pressedl)
        )

        val brushsize_btn: ImageButton = findViewById(R.id.click_brush)
        brushsize_btn.setOnClickListener {
            showbrushsizechooserdialog()
        }
        /**
         * setting up gallery button so that it can access storage
         */
        val img_btn_gallery:ImageButton=findViewById(R.id.click_gallery)
        img_btn_gallery.setOnClickListener {
            requestStoragePermission()
        }
        /**
         * setting up undo button
         */
        val undo_btn:ImageButton =findViewById(R.id.click_undo)
        undo_btn.setOnClickListener {
            drawingview?.onClickUndo()
        }
        val redo_btn:ImageButton=findViewById(R.id.click_redo)
        redo_btn.setOnClickListener {
            drawingview?.onClickRedo()
        }
    }
    private fun requestStoragePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            /**
             * This method checks if the user has previously denied
             * the permission request. If it returns true, it means t
             * he user previously denied the permission at least once.
             */
            showRationalDialog("drawing app","it must access internal storage")
        }
        else
        {
            /**it means user has never been asked for permission so we should ask them
             * so if it returns false value then it will ask for permission
             */
            req_for_permission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE))

        }
    }
    //for newer devices making permission
    private  fun isReadStorageAllowed():Boolean
    {
        val result=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result==PackageManager.PERMISSION_DENIED
    }

    /**
     * using builder for asking permessions
     */
    private fun showRationalDialog(title:String,message:String)
    {
        val builder=AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("cancel")
        {
                dialog,_->
            Toast.makeText(this,"you clicked cancel",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.show()
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
            //represents just the value before so we used mImageButtonCurrentPaint = view so that it works properlyo

        }
    }
}