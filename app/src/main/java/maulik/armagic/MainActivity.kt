package maulik.armagic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private val renderableMap = mutableMapOf<Int, Renderable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIfOpenGl3SupportedOrFinish()) return
        setContentView(R.layout.activity_main)
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        createModels()
    }

    private fun createModels() {
        val weakActivity: WeakReference<MainActivity> = WeakReference(this)


    }
}