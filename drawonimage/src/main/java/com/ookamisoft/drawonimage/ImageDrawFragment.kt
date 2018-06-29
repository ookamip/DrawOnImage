package com.ookamisoft.drawonimage

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_image_draw.*

class ImageDrawFragment : Fragment() {

    private var onBitmapSaveListener: ((savedFileUri: Bitmap) -> Unit)? = null
    private var imageSource: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_image_draw, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawOnImageView.setImageURI(imageSource)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_image_draw, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.imageActionUndo -> {
                drawOnImageView.undoMove()
            }
            R.id.imageActionEraser -> {
                if (item.isChecked) {
                    drawOnImageView.turnEraserOff()
                    item.isChecked = false
                    item.setIcon(R.drawable.ic_menu_action_eraser)
                } else {
                    drawOnImageView.turnEraserOn()
                    item.isChecked = true
                    item.setIcon(R.drawable.ic_menu_action_brush)
                }
            }
            R.id.imageActionSave -> {
                onBitmapSaveListener?.invoke(drawOnImageView.getBitmap())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    class FragmentCreator {
        fun createFragment(imageSource: Uri, onBitmapSaveListener: ((savedFileUri: Bitmap) -> Unit)? = null): ImageDrawFragment {
            val fragment = ImageDrawFragment()
            fragment.onBitmapSaveListener = onBitmapSaveListener
            fragment.imageSource = imageSource
            return fragment
        }
    }
}