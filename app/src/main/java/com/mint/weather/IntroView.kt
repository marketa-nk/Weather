package com.mint.weather

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt


class IntroView @JvmOverloads
constructor(
    ctx: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(ctx, attributeSet, defStyleAttr) {

    @ColorInt
    private val color : Int = Color.parseColor("#4961FD")

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = color
        style = Paint.Style.FILL
        strokeWidth = 10f
    }

    init {

        val v1 = ValueAnimator.ofFloat(0f, 1f)
            .apply {
                addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Float
                    val width = width
                    val height = height
                    if (height > 0 && width > 0) {
                        rectPaint.shader = RadialGradient(
                            width.toFloat() / 2,
                            height.toFloat() / 2,
                            getRadius(),
                            intArrayOf(Color.TRANSPARENT, color),
                            floatArrayOf(0.0f, animatedValue),
                            Shader.TileMode.CLAMP)
                    }
                    invalidate()
                }

            }
        val v2 = ValueAnimator.ofFloat(0f, 1f)
            .apply {
                addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Float
                    val width = width
                    val height = height
                    if (height > 0 && width > 0) {
                        rectPaint.shader = RadialGradient(
                            width.toFloat() / 2,
                            height.toFloat() / 2,
                            getRadius(),
                            intArrayOf(Color.TRANSPARENT, color),
                            floatArrayOf(animatedValue, 1f),
                            Shader.TileMode.CLAMP)
                    }
                    invalidate()
                }

            }
        AnimatorSet().apply {
            duration = 3000
            startDelay = 1000
            playSequentially(v1, v2)
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), rectPaint)
    }

    private fun getRadius(): Float {
        return if (height > width) {
            height.toFloat()
        } else {
            width.toFloat()
        }
    }
}