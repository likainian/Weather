package com.wallet.weather.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.wallet.weather.R
import com.wallet.weather.utils.WeatherUtils.dp
import com.wallet.weather.item.HourlyWeatherAdapter
import com.wallet.weather.utils.WeatherUtils

/**
 * 天气折线图
 */
class HourlyTemperatureView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 12 * resources.displayMetrics.scaledDensity
        strokeWidth = context.dp(2).toFloat()
    }
    private val accentColor = context.getColor(R.color.weather_accent)
    private val temperatureColor = context.getColor(R.color.white)
    private val timeColor = context.getColor(R.color.weather_text_secondary)
    private var point: HourlyWeatherAdapter.Point? = null

    fun bind(point: HourlyWeatherAdapter.Point) {
        this.point = point
        contentDescription = "${point.hour.time} ${WeatherUtils.degrees(point.hour.temperature)}"
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val item = point ?: return
        val center = width / 2f
        fun y(height: Double) = context.dp(88) - (height * context.dp(44)).toFloat()

        item.height?.let { height ->
            val centerY = y(height)
            paint.color = accentColor
            item.previous?.let { canvas.drawLine(0f, y((it + height) / 2), center, centerY, paint) }
            item.next?.let { canvas.drawLine(center, centerY, width.toFloat(), y((it + height) / 2), paint) }
            canvas.drawCircle(center, centerY, context.dp(3).toFloat(), paint)
            paint.color = temperatureColor
            canvas.drawText(WeatherUtils.degrees(item.hour.temperature), center, centerY - context.dp(10), paint)
        }
        paint.color = timeColor
        canvas.drawText(item.hour.time, center, height - context.dp(12).toFloat(), paint)
    }
}
