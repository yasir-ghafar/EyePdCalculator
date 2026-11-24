package com.techlads.eyepdcalculator.ui.calculate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.techlads.eyepdcalculator.R

class MarkerLinkView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val density = resources.displayMetrics.density
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var hasPoints = false

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 3f * density
        color = ContextCompat.getColor(context, R.color.mint)
    }

    private val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f * density
        color = ContextCompat.getColor(context, R.color.mint)
        alpha = 50
    }

    fun setEndpoints(x1: Float, y1: Float, x2: Float, y2: Float, color: Int) {
        startX = x1
        startY = y1
        endX = x2
        endY = y2
        hasPoints = true
        linePaint.color = color
        haloPaint.color = color
        haloPaint.alpha = 56
        invalidate()
    }

    fun clear() {
        hasPoints = false
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean = false

    override fun onDraw(canvas: Canvas) {
        if (!hasPoints) return
        canvas.drawLine(startX, startY, endX, endY, haloPaint)
        canvas.drawLine(startX, startY, endX, endY, linePaint)
        canvas.drawCircle(startX, startY, 5f * density, linePaint)
        canvas.drawCircle(endX, endY, 5f * density, linePaint)
    }
}
