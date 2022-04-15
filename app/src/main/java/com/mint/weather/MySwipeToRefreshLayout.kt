package com.mint.weather

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.abs

class MySwipeToRefreshLayout(context: Context, attrs: AttributeSet?) : SwipeRefreshLayout(context, attrs) {

    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var mPrevX = 0f

    @SuppressLint("Recycle")
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mPrevX = MotionEvent.obtain(event).x
            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = abs(eventX - mPrevX)
                if (xDiff > mTouchSlop) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}