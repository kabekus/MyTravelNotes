package com.kabekus.mytravelnotes

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.kabekus.mytravelnotes.databinding.ActivityDetailBinding
import android.Manifest
import java.io.ByteArrayOutputStream

class DetailActivity : AppCompatActivity() {
    var selectedBitmap : Bitmap? = null
    private lateinit var binding: ActivityDetailBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()
    }
    fun saveButtonClicked(view : View){
        val noteTitle = binding.noteTitleTxt.text.toString()
        val note = binding.noteTxt.text.toString()
        if (selectedBitmap != null){
            val smallBitmap = createSmallBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArrayImg = outputStream.toByteArray()

            try {
                val database = this.openOrCreateDatabase("TravelNotes", MODE_PRIVATE,null)
                database.execSQL("CREATE TABLE IF NOT EXISTS travelNotes " +
                        "(id INTEGER PRIMARY KEY, note_title VARCHAR, notes VARCHAR, image BLOB)")
                val sqlString = "INSERT INTO travelNotes (note_title,notes,image)VALUES(?,?,?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1,noteTitle)
                statement.bindString(2,note)
                statement.bindBlob(3,byteArrayImg)
                statement.execute()

                Toast.makeText(this@DetailActivity,"Save Successful",Toast.LENGTH_LONG).show()

            }catch (e:Exception){
                e.printStackTrace()
            }



            val intent = Intent(this@DetailActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Açık ne kadar aktivite varsa kapat ve sadece main aktiviteye dön
            startActivity(intent)
        }
    }

    private fun createSmallBitmap(image : Bitmap, maximumSize : Int):Bitmap{
        var width = image.width
        var height = image.height
        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1){
            //Landscape
            width = maximumSize
            val scaleHeight = width/bitmapRatio
            height = scaleHeight.toInt()
        }else{
            //Portrait
            height = maximumSize
            val scaleWidth = height*bitmapRatio
            width = scaleWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }
    fun selectImage(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ){

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission",View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }
        }else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission",View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

            if (result.resultCode == RESULT_OK){
                 val intentFromResult = result.data
                if (intentFromResult != null){
                    val imageData = intentFromResult.data
                    //binding.imageView.setImageURI(imageData)
                    if (imageData != null){
                        try {
                            if (Build.VERSION.SDK_INT >= 28) { // Min SDK 28 gerektiriyor onun kontrolü
                                val source = ImageDecoder.createSource(this@DetailActivity.contentResolver, imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }

                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }else{
                Toast.makeText(this@DetailActivity,"Permission Needed !",Toast.LENGTH_LONG).show()
            }

        }
    }
}