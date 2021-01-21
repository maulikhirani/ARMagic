package maulik.armagic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.Toast

const val MIN_OPENGL_VERSION = 3.0

@SuppressLint("ObsoleteSdkInt")
fun Activity.checkIfOpenGl3SupportedOrFinish(): Boolean {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
        Log.e(
            "MagicAR",
            "Sceneform requires Android N or later"
        )
        Toast.makeText(
            this,
            "Sceneform requires Android N or later",
            Toast.LENGTH_LONG
        ).show()
        finish()
        return false
    }
    val openGlVersionString =
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
    if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
        Log.e(
            "MagicAR",
            "Sceneform requires OpenGL ES 3.0 later"
        )
        Toast.makeText(
            this,
            "Sceneform requires OpenGL ES 3.0 or later",
            Toast.LENGTH_LONG
        ).show()
        finish()
        return false
    }
    return true
}