package com.rhmn.learneng.view.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.rhmn.learneng.R

class StepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var stepCount = 5
    private var currentStep = 1
    private val inactiveDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.step_inactive)!!
    private val activeDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.step_active)!!

    fun setStepCount(count: Int) {
        stepCount = (count).coerceAtLeast(2)
        invalidate()
    }

    fun setCurrentStep(step: Int) {
        currentStep = (step).coerceIn(1, stepCount)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val totalWidth = width - paddingLeft - paddingRight
        val segmentWidth = (totalWidth - (stepCount - 1) * 10) / stepCount.toFloat() // 10dp gap between steps
        val height = height - paddingTop - paddingBottom

        for (i in 0 until stepCount) {
            val left = paddingLeft + i * (segmentWidth + 10).toInt()
            val right = left + segmentWidth.toInt()
            val drawable = if (i < currentStep) activeDrawable else inactiveDrawable
            drawable.setBounds(left, paddingTop, right, paddingTop + height)
            drawable.draw(canvas)
        }
    }
}