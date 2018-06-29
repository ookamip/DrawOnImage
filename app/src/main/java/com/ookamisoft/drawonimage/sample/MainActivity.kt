package com.ookamisoft.drawonimage.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.ookamisoft.drawonimage.ImageDrawActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        revertButton.setOnClickListener {
            drawOnImageView.undoMove()
        }

        saveButton.setOnClickListener {
            val imageState = drawOnImageView.getBitmap()
            savedImageView.setImageBitmap(imageState)
        }

        eraserButton.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                drawOnImageView.turnEraserOn()
            } else {
                drawOnImageView.turnEraserOff()
            }
        }

        startActivityButton.setOnClickListener {
            val intent = Intent(this, ImageDrawActivity::class.java)
            intent.data = Uri.parse("android.resource://$packageName/drawable/test")
            startActivityForResult(intent, DRAW_ON_IMAGE_REQUEST_CODE)
        }

        startActivityButtonWithFile.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data)
        }
        when (requestCode) {
            DRAW_ON_IMAGE_REQUEST_CODE -> savedImageView.setImageURI(data?.data)
            CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE -> {
                val intent = Intent(this, ImageDrawActivity::class.java)
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                val tempFileURI = File(externalCacheDir, "myImage")
                val out = FileOutputStream(tempFileURI)
                out.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    bitmap.recycle()
                }

                intent.data = Uri.fromFile(tempFileURI)
                startActivityForResult(intent, DRAW_ON_IMAGE_REQUEST_CODE)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val DRAW_ON_IMAGE_REQUEST_CODE = 1
        private const val CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE = 2
    }
}
