package com.example.mongodbrealmcourse.view.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

class CustomImageView : androidx.appcompat.widget.AppCompatImageView {

    private var rectPaint: Paint = Paint()
    private var rect: RectF? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        rectPaint.apply {
            color = Color.RED // Màu sắc của hộp
            style = Paint.Style.STROKE
            strokeWidth = 5f // Độ dày của đường vẽ hộp
        }
    }

    fun drawRect(left: Float, top: Float, right: Float, bottom: Float) {
        rect = RectF(left, top, right, bottom)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rect?.let {
            canvas.drawRect(it, rectPaint)
        }
    }
}
