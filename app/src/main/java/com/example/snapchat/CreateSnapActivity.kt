package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var createSnapIV : ImageView? = null
    var messageET : EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapIV = findViewById(R.id.imageView)
        messageET = findViewById(R.id.editText3)

    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)
    }

    fun chooseimageClicked(view : View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                createSnapIV?.setImageBitmap(bitmap)
            }
            catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode ==1 ) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }


    fun nextClicked(view : View) {
        // Get the data from an ImageView as bytes
        createSnapIV?.isDrawingCacheEnabled = true
        createSnapIV?.buildDrawingCache()
        val bitmap = (createSnapIV?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val ref = FirebaseStorage.getInstance().getReference().child("images").child(imageName)
        var uploadTask = ref.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(applicationContext,"UploadFailed",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.i("URL",downloadUri.toString())
                    val intent = Intent(this,ChooseUsserActivity::class.java)
                    intent.putExtra("imageURL",downloadUri.toString())
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",messageET?.text.toString())
                    startActivity(intent)
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(applicationContext,"UploadFailed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
