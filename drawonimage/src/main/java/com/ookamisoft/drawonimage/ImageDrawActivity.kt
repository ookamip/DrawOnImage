package com.ookamisoft.drawonimage

import android.app.Activity
import android.content.Context
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

        intent.extras?.getString(ACTIVITY_TITLE_KEY, "").apply {
            if (!isNullOrEmpty()) {
                title = this
            }
        }

        val contentUri = intent.data
        val useEraser = intent.extras?.getBoolean(USE_ERASER_KEY, false) ?: true

        val imageDrawFragment = fragmentCreator.createFragment(contentUri, true) { resultBitmap ->
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

    class Creator {
        @JvmOverloads
        fun createDrawOnImageActivityIntent(context: Context, title: String? = null,
                                            useErasers: Boolean = true): Intent {
            val intent = Intent(context, ImageDrawActivity::class.java)
            intent.putExtra(ACTIVITY_TITLE_KEY, title)
            intent.putExtra(USE_ERASER_KEY, useErasers)
            return intent
        }
    }

    companion object {
        const val ACTIVITY_TITLE_KEY = "activity_title"
        const val USE_ERASER_KEY = "use_eraser"
    }
}