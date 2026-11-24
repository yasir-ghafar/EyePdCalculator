package com.techlads.eyepdcalculator.ui.home

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.techlads.eyepdcalculator.R

class FaceGuideView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val density = resources.displayMetrics.density
    private val oval = RectF()
    private var pulse = 0.55f
    private var sweep = 0f

    private val ovalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f * density
        color = ContextCompat.getColor(context, R.color.mint)
    }

    private val bracketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4f * density
        color = ContextCompat.getColor(context, R.color.white)
    }

    private val pulseAnimator = ValueAnimator.ofFloat(0.4f, 1f).apply {
        duration = 1400
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            pulse = it.animatedValue as Float
            invalidate()
        }
    }

    private val sweepAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 4200
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener { sweep = it.animatedValue as Float }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pulseAnimator.start()
        sweepAnimator.start()
    }

    override fun onDetachedFromWindow() {
        pulseAnimator.cancel()
        sweepAnimator.cancel()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height * 0.46f
        val rx = width * 0.31f
        val ry = height * 0.22f
        oval.set(cx - rx, cy - ry, cx + rx, cy + ry)

        ovalPaint.alpha = (70 + 150 * pulse).toInt()
        canvas.drawOval(oval, ovalPaint)

        val arcPaint = ovalPaint
        val previousAlpha = arcPaint.alpha
        arcPaint.alpha = 230
        canvas.drawArc(oval, sweep, 54f, false, arcPaint)
        arcPaint.alpha = previousAlpha

        val arm = 22f * density
        val inset = 10f * density
        bracketPaint.alpha = (160 + 80 * pulse).toInt()
        drawBracket(canvas, oval.left - inset, oval.top - inset, arm, arm, 1f, 1f)
        drawBracket(canvas, oval.right + inset, oval.top - inset, arm, arm, -1f, 1f)
        drawBracket(canvas, oval.left - inset, oval.bottom + inset, arm, arm, 1f, -1f)
        drawBracket(canvas, oval.right + inset, oval.bottom + inset, arm, arm, -1f, -1f)
    }

    private fun drawBracket(
        canvas: Canvas,
        x: Float,
        y: Float,
        armX: Float,
        armY: Float,
        dirX: Float,
        dirY: Float
    ) {
        canvas.drawLine(x, y, x + armX * dirX, y, bracketPaint)
        canvas.drawLine(x, y, x, y + armY * dirY, bracketPaint)
    }
}
