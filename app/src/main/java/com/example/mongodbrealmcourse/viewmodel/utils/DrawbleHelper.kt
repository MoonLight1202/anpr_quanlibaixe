package com.example.mongodbrealmcourse.viewmodel.utils

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat

class DrawableHelper {

    companion object {
        private const val RECTANGLE = "RECTANGLE"
        private const val OVAL = "OVAL"

        private fun init(
            shape: String = RECTANGLE, backgroundColor: Int?,
            borderColor: Int?, borderWidth: Int?, radius: Float?
        ): GradientDrawable {
            val drawable = GradientDrawable()

            when (shape) {
                RECTANGLE -> drawable.shape = GradientDrawable.RECTANGLE
                OVAL -> drawable.shape = GradientDrawable.OVAL
                else -> drawable.shape = GradientDrawable.RECTANGLE
            }
            backgroundColor?.let { drawable.setColor(it) }

            if (borderColor != null && borderWidth != null)
                drawable.setStroke(borderWidth, borderColor)

            radius?.let { drawable.cornerRadius = it }
            return drawable
        }

        private fun init(
            shape: String = RECTANGLE, backgroundColor: Int?,
            borderColor: Int?, borderWidth: Int?, radius: FloatArray?
        ): GradientDrawable {
            val drawable = GradientDrawable()

            when (shape) {
                RECTANGLE -> drawable.shape = GradientDrawable.RECTANGLE
                OVAL -> drawable.shape = GradientDrawable.OVAL
                else -> drawable.shape = GradientDrawable.RECTANGLE
            }
            backgroundColor?.let { drawable.setColor(it) }

            if (borderColor != null && borderWidth != null)
                drawable.setStroke(borderWidth, borderColor)

            radius?.let { drawable.cornerRadii = it }
            return drawable
        }
        private fun initColor(
            shape: String = RECTANGLE, backgroundColor: Int?,
            borderColor: Int?, borderWidth: Int?, radius: Float?
        ): GradientDrawable {
            val drawable = GradientDrawable()

            when (shape) {
                RECTANGLE -> drawable.shape = GradientDrawable.RECTANGLE
                OVAL -> drawable.shape = GradientDrawable.OVAL
                else -> drawable.shape = GradientDrawable.RECTANGLE
            }
            backgroundColor?.let { drawable.setColor(it) }

            if (borderColor != null && borderWidth != null)
                drawable.setStroke(borderWidth, borderColor)

            radius?.let { drawable.cornerRadius = it }
            return drawable
        }

        fun rectangle(context: Context, backgroundColor: Int, radius: Float): GradientDrawable {
            return init(
                RECTANGLE, ContextCompat.getColor(context, backgroundColor),
                null, null,
                GlobalHelper().convertDpToPixel(radius, context)
            )
        }

        fun rectangle(
            context: Context, borderColor: Int, borderWidth: Float, radius: Float
        ): GradientDrawable {
            return init(
                RECTANGLE, null,
                ContextCompat.getColor(context, borderColor),
                GlobalHelper().convertDpToPixel(borderWidth, context).toInt(),
                GlobalHelper().convertDpToPixel(radius, context)
            )
        }

        fun rectangle(
            context: Context, backgroundColor: Int,
            borderColor: Int, borderWidth: Float, radius: Float
        ): GradientDrawable {
            return init(
                RECTANGLE, ContextCompat.getColor(context, backgroundColor),
                ContextCompat.getColor(context, borderColor),
                GlobalHelper().convertDpToPixel(borderWidth, context).toInt(),
                GlobalHelper().convertDpToPixel(radius, context)
            )
        }

        fun rectangle(context: Context, backgroundColor: Int, radius: FloatArray?)
                : GradientDrawable {
            return init(
                RECTANGLE, ContextCompat.getColor(context, backgroundColor),
                null, null, radius
            )
        }

        fun rectangle(
            context: Context, borderColor: Int, borderWidth: Float, radius: FloatArray?
        ): GradientDrawable {
            return init(
                RECTANGLE, null,
                ContextCompat.getColor(context, borderColor),
                GlobalHelper().convertDpToPixel(borderWidth, context).toInt(),
                radius
            )
        }

        fun rectangle(
            context: Context, backgroundColor: Int,
            borderColor: Int, borderWidth: Float, radius: FloatArray?
        ): GradientDrawable {
            return init(
                RECTANGLE, ContextCompat.getColor(context, backgroundColor),
                ContextCompat.getColor(context, borderColor),
                GlobalHelper().convertDpToPixel(borderWidth, context).toInt(),
                radius
            )
        }

        fun oval(context: Context, backgroundColor: Int): GradientDrawable {
            return init(OVAL, ContextCompat.getColor(context, backgroundColor), null, null, 0f)
        }
        fun ovalStroke(context:Context,borderColor: Int,borderWidth:Float): GradientDrawable {
            return initColor(OVAL, null, ContextCompat.getColor(context, borderColor), GlobalHelper().convertDpToPixel(borderWidth, context).toInt(), 0f)
        }
    }
}