package com.example.magicphoto

import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ImageEditor : AppCompatActivity() {
    private lateinit var imageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)

        imageView = findViewById<ImageView>(R.id.imageView)
        val image = intent.extras?.get("image") as Bitmap

        imageView.setImageBitmap(image)
    }
}