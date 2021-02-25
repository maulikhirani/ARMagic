package maulik.armagic

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.google.ar.sceneform.rendering.Renderable

data class ArModel(
    val name: String,
    @DrawableRes val imageResource: Int,
    @RawRes val resourceId: Int,
    val renderable: Renderable,
    val scaleOptions: ScaleOptions
)

data class ScaleOptions(
    val freeScaling: Boolean = true,
    val minScale: Float = 0.1f,
    val maxScale: Float = 1.0f
)
