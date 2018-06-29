package com.ookamisoft.drawonimage

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import java.util.*

class DrawOnImageView : ImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleInt: Int) : super(context, attributeSet, defStyleInt)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleInt: Int, defStyleRes: Int) : super(context, attributeSet, defStyleInt, defStyleRes)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val previousPaths: LinkedList<DrawOnImagePath> = LinkedList()
    private var currentPath = Path()

    private var isEraserOn = false

    private var pathLayerBitmap: Bitmap? = null
    private var pathLayerCanvas: Canvas? = null

    init {
        setupPaint(paint)
        setupPaint(eraserPaint)

        paint.color = Color.RED
        eraserPaint.color = Color.RED
        eraserPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    previousPaths.add(DrawOnImagePath(currentPath, isEraserOn))
                    currentPath = Path()
                }
                MotionEvent.ACTION_DOWN -> {
                    currentPath.incReserve(10)
                }
                else -> {
                    addLineToPath(currentPath, this)
                }
            }
            invalidate()
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(w == 0|| h == 0) {
            return
        }
        val previousBitmap = pathLayerBitmap
        if (previousBitmap == null) {
            pathLayerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } else {
            pathLayerBitmap = Bitmap.createBitmap(previousBitmap)
            previousBitmap.recycle()
        }
        pathLayerCanvas = Canvas(pathLayerBitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            save()
            pathLayerCanvas?.apply {
                previousPaths.forEach {
                    val paint = if (it.isEraserPath) eraserPaint else paint
                    drawPath(it.path, paint)
                }
                drawPath(currentPath, if (isEraserOn) eraserPaint else paint)
            }
            pathLayerBitmap?.let {
                drawBitmap(it, 0f, 0f, null)
            }

            restore()
        }
    }

    fun undoMove() {
        if (previousPaths.isNotEmpty()) {
            pathLayerBitmap?.let {
                pathLayerBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                pathLayerCanvas = Canvas(pathLayerBitmap)
                it.recycle()
            }
            previousPaths.removeLast()
            invalidate()
        }
    }

    fun getBitmap(autoScale: Boolean = false): Bitmap {
        destroyDrawingCache()
        buildDrawingCache()
        return getDrawingCache(autoScale)
    }

    fun turnEraserOn() {
        isEraserOn = true
    }

    fun turnEraserOff() {
        isEraserOn = false
    }

    private fun addLineToPath(path: Path, event: MotionEvent) {
        if (path.isEmpty) {
            path.moveTo(event.x, event.y)
        } else {
            path.lineTo(event.x, event.y)
        }
    }

    private fun setupPaint(paint: Paint) {
        paint.isAntiAlias = true
        paint.strokeWidth = 10f
        paint.pathEffect = CornerPathEffect(50f)
        paint.style = Paint.Style.STROKE
    }

    private data class DrawOnImagePath(val path: Path, val isEraserPath: Boolean)
}