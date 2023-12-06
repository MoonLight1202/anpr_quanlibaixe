package com.example.mongodbrealmcourse.viewmodel.utils

import android.view.View
import android.view.animation.Interpolator
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.example.mongodbrealmcourse.viewmodel.callback.VoidCallback
import kotlin.math.sin

class AnimationHelper {
    companion object {
        private const val duration: Long = 100
        private const val scaleDefault = 0.94f
        fun scaleAnimation(view: View?, animationListener: VoidCallback?, scale: Float) {
            if (view == null) {
                animationListener?.execute()
                return
            }
            ViewCompat.animate(view)
                .setDuration(duration)
                .scaleX(if (scale != 0f) scale else scaleDefault)
                .scaleY(if (scale != 0f) scale else scaleDefault)
                .setInterpolator(CycleInterpolator())
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {}
                    override fun onAnimationEnd(view: View) {
                        // some thing
                        animationListener?.execute()
                    }

                    override fun onAnimationCancel(view: View) {}
                })
                .withLayer()
                .start()
        }
    }
    private class CycleInterpolator : Interpolator {
        private val mCycles = 0.5f
        override fun getInterpolation(input: Float): Float {
            return sin(2.0f * mCycles * Math.PI * input).toFloat()
        }
    }
}