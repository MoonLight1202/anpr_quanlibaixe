package com.example.mongodbrealmcourse.view.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mongodbrealmcourse.R

class DashedLineView constructor(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    private var mPaint: Paint? = null
    private var orientation = 0

    companion object {
        const val ORIENTATION_HORIZONTAL = 0
        const val ORIENTATION_VERTICAL = 1
    }

    init {
        val dashGap: Int
        val dashLength: Int
        val dashThickness: Int
        val color: Int
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DividerView, 0, 0)
        try {
            dashGap = a.getDimensionPixelSize(R.styleable.DividerView_dashGap, 4)
            dashLength = a.getDimensionPixelSize(R.styleable.DividerView_dashLength, 10)
            dashThickness = a.getDimensionPixelSize(R.styleable.DividerView_dashThickness, 5)
            color = a.getColor(R.styleable.DividerView_color, ContextCompat.getColor(context, R.color.colorPrimary));
//            color = ContextCompat.getColor(context, R.color.colorText_Day_2)
            orientation = a.getInt(R.styleable.DividerView_orientation, ORIENTATION_VERTICAL)
        } finally {
            a.recycle()
        }
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = color
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = dashThickness.toFloat()
        mPaint!!.pathEffect =
            DashPathEffect(floatArrayOf(dashLength.toFloat(), dashGap.toFloat()), 0f)
    }

    override fun onDraw(canvas: Canvas) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            val center = height * .5f
            canvas.drawLine(0f, center, width.toFloat(), center, mPaint!!)
        } else {
            val center = width * .5f
            canvas.drawLine(center, 0f, center, height.toFloat(), mPaint!!)
        }
    }


}