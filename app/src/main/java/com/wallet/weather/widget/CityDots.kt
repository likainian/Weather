package com.wallet.weather.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wallet.weather.R
import com.wallet.weather.utils.WeatherUtils.dp
import kotlin.math.min

/**
 * 城市切换圆点。
 */
class CityDots @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectedColor = context.getColor(R.color.weather_accent)
    private val inactiveColor = context.getColor(R.color.weather_dot_inactive)
    private var count = 0
    private var selected = 0

    fun setPosition(count: Int, selected: Int) {
        this.count = count
        this.selected = selected
        contentDescription = if (count == 0) null else "第 ${selected + 1} 个城市，共 $count 个"
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (count == 0) return
        val step = min(context.dp(14).toFloat(), width.toFloat() / count)
        val start = width / 2f - step * (count - 1) / 2
        repeat(count) { index ->
            paint.color = if (index == selected) selectedColor else inactiveColor
            val radius = min(step / 3, context.dp(if (index == selected) 4 else 3).toFloat())
            canvas.drawCircle(start + index * step, height / 2f, radius, paint)
        }
    }
}
