package com.example.geomate.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        this.layoutParams.width,
        this.layoutParams.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    this.layout(this.left, this.top, this.right, this.bottom)
    this.draw(canvas)
    return bitmap
}

fun Drawable.toBitmap(): Bitmap {
    this.setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)

    val bitmap = Bitmap.createBitmap(
        this.intrinsicWidth,
        this.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    this.draw(Canvas(bitmap))
    return bitmap
}

fun Drawable.toBitmapWithText(
    context: Context,
    text: String,
    fontSize: Int = 24,
    x: Float = 0f,
    y: Float = 0f,
    @ColorInt color: Int = Color.rgb(0, 0, 0),
): Bitmap {
    this.setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)

    val bitmap = Bitmap.createBitmap(
        this.intrinsicWidth,
        this.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    val bounds = Rect()
    val scale = context.resources.displayMetrics.density

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        setColor(color)
        textSize = (fontSize * scale).roundToInt().toFloat()
        getTextBounds(text, 0, text.length, bounds)
    }

    val xCenter = (bitmap.width - bounds.width()) / 2f
    val yCenter = (bitmap.height + bounds.height()) / 2f

    canvas.drawText("hello", xCenter + x + 20, yCenter + y + 20, paint)
    this.draw(canvas)
    return bitmap
}