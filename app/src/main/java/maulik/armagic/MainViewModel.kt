package maulik.armagic

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val arModelSet = MutableLiveData<MutableSet<ArModel>>()
    val arModelList = Transformations.map(arModelSet) {
        it.toList()
    }

    fun createModels() {
        createModel("Sofa", R.drawable.sofa, R.raw.sofa, ScaleOptions(true))
        createModel("Chair", R.drawable.chair, R.raw.chair, ScaleOptions(false, 0.1f, 0.3f))
        createViewRenderable()
    }

    private fun createViewRenderable() {
        ViewRenderable.builder().setView(getApplication(), R.layout.texture_view)
            .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
            .build()
            .thenAccept { modelRenderable ->
                val arModel = ArModel(
                    "Texture",
                    R.drawable.wood,
                    R.raw.sofa,
                    modelRenderable,
                    ScaleOptions(true)
                )
                if (arModelSet.value != null) {
                    arModelSet.value?.add(arModel)
                    arModelSet.value = arModelSet.value
                } else {
                    arModelSet.value = mutableSetOf(arModel)
                }
            }.exceptionally {
                return@exceptionally null
            }
    }

    private fun createModel(
        name: String,
        @DrawableRes imageResource: Int,
        @RawRes modelResource: Int,
        scaleOptions: ScaleOptions
    ) {

        ModelRenderable.builder()
            .setSource(getApplication(), modelResource)
            .setIsFilamentGltf(true)
            .build()
            .thenAccept { modelRenderable ->
                val arModel =
                    ArModel(name, imageResource, modelResource, modelRenderable, scaleOptions)
                if (arModelSet.value != null) {
                    arModelSet.value?.add(arModel)
                    arModelSet.value = arModelSet.value
                } else {
                    arModelSet.value = mutableSetOf(arModel)
                }
            }
            .exceptionally {
                return@exceptionally null
            }
    }

}