package com.example.geomate.ext

import android.graphics.Picture
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

fun Modifier.toPicture(picture: Picture): Modifier {
    return this.drawWithCache {
        val width = this.size.width.toInt()
        val height = this.size.height.toInt()
        onDrawWithContent {
            val canvas = Canvas(picture.beginRecording(width, height))
            draw(this, this.layoutDirection, canvas, this.size) {
                this@onDrawWithContent.drawContent()
            }
            picture.endRecording()
            drawIntoCanvas { it.nativeCanvas.drawPicture(picture) }
        }
    }
}