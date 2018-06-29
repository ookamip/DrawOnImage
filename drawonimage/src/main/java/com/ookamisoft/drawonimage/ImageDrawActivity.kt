package com.ookamisoft.drawonimage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class ImageDrawActivity : AppCompatActivity() {

    private val fragmentCreator = ImageDrawFragment.FragmentCreator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_draw)

        val contentUri = intent.data
        val imageDrawFragment = fragmentCreator.createFragment(contentUri) { resultBitmap ->
            val path = contentUri.path
            try {
                val out = FileOutputStream(File(path))
                out.use {
                    resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                resultBitmap.recycle()
            } catch (exc: Exception) {
                exc.printStackTrace()
                setResult(Activity.RESULT_CANCELED)
                finish()
                return@createFragment
            }
            setResult(Activity.RESULT_OK, Intent().apply { data = contentUri })
            finish()
        }

        supportFragmentManager.beginTransaction().replace(R.id.imageDrawFragmentHolder, imageDrawFragment).commit()
    }
}