package com.example.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_snap.*
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    var snapimageView: ImageView? = null
    var msgtextView: TextView? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        snapimageView = findViewById(R.id.snapimageView)
        msgtextView = findViewById(R.id.messagetextView)

        msgtextView?.text = intent.getStringExtra("message")

        val task = ImageDownloadeer()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snapimageView?.setImageBitmap(myImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ImageDownloadeer : AsyncTask<String?, Void?, Bitmap>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                return BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        mAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference().child("users").child(
                it
            ).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()
        }

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
    }
}
