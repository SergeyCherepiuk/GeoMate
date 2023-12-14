package com.example.geomate.image

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap

@Composable
fun BitmapComposable(
    onBitmapped: (bitmap: Bitmap) -> Unit = { _ -> },
    backgroundColor: Color = Color.Transparent,
    dpSize : DpSize,
    composable: @Composable () -> Unit
) {
    Column(modifier = Modifier.size(0.dp, 0.dp)){
        Box(modifier = Modifier.size(dpSize)) {
            AndroidView(factory = {
                ComposeView(it).apply {
                    setContent {
                        Box(modifier = Modifier.background(backgroundColor).fillMaxSize()) {
                            composable()
                        }
                    }
                }
            }, modifier = Modifier.fillMaxSize(), update = {
                it.run {
                    doOnLayout {
                        onBitmapped(drawToBitmap())
                    }
                }
            })
        }
    }

}

@Composable
fun BitmapComposable(
    onBitmapped: (bitmap: Bitmap) -> Unit = { _ -> },
    backgroundColor: Color = Color.Transparent,
    intSize : IntSize, // Pixel size for output bitmap
    composable: @Composable () -> Unit
) {
    val renderComposableSize = LocalDensity.current.run { intSize.toSize().toDpSize() }
    BitmapComposable(onBitmapped,backgroundColor,renderComposableSize,composable)
}