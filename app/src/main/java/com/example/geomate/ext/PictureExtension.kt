package com.example.geomate.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture

fun Picture.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawPicture(this)
    return bitmap
}
