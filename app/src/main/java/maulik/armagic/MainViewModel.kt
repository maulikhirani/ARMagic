package maulik.armagic

import android.app.Application
import android.view.LayoutInflater
import android.widget.ImageView
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
//        createModel("Sofa", R.drawable.sofa, R.raw.sofa, ScaleOptions(true))
//        createModel("Chair", R.drawable.chair, R.raw.chair, ScaleOptions(false, 0.1f, 0.3f))
        createViewRenderable("Wood", R.drawable.wood)
        createViewRenderable("Ocean", R.drawable.texture2)
        createViewRenderable("Garden", R.drawable.garden)
        createViewRenderable("Roses", R.drawable.roses)
    }

    private fun createViewRenderable(name: String, drawable: Int) {

        val view = LayoutInflater.from(getApplication()).inflate(R.layout.texture_view, null)
        view.findViewById<ImageView>(R.id.iv_texture).setImageResource(drawable)

        ViewRenderable.builder().setView(getApplication(), view)
            .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
            .build()
            .thenAccept { modelRenderable ->
                val arModel = ArModel(
                    name,
                    drawable,
                    drawable,
                    modelRenderable,
                    ScaleOptions(true, 0.1f, 0.5f)
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