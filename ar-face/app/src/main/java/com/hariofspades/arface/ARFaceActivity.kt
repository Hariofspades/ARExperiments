package com.hariofspades.arface

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode

class ARFaceActivity : AppCompatActivity() {

    private lateinit var arFragment: FaceARFragment
    private lateinit var modelRenderable: ModelRenderable
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyCompatibility()
        setContentView(R.layout.activity_arface)
        arFragment = supportFragmentManager.findFragmentById(R.id.face_fragment) as FaceARFragment
        loadModel()
        setupScene()

    }

    private fun loadModel() {
        ModelRenderable.builder()
            .setSource(this, R.raw.sunglasses_01)
            .build()
            .thenAccept { model ->
                model.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
                modelRenderable = model
            }
    }

    private fun setupScene() {
        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        val scene = sceneView.scene

        scene.addOnUpdateListener { _ ->

            val collection: Collection<AugmentedFace>? = sceneView.session?.getAllTrackables(AugmentedFace::class.java)
            collection?.forEach { face ->
                if (!faceNodeMap.containsKey(face)) {
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.apply {
                        setParent(scene)
                        faceRegionsRenderable = modelRenderable
                    }
                    faceNodeMap[face] = faceNode
                }
            }

            val iterator = faceNodeMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val face = entry.key
                if (face.trackingState == TrackingState.STOPPED) {
                    val faceNode = entry.value
                    faceNode.setParent(null)
                    iterator.remove()
                }
            }
        }
    }

    private fun verifyCompatibility() {
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
    }

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (ArCoreApk.getInstance().checkAvailability(activity) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(activity, "Augmented Faces requires ArCore", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }

        if (getOpenGLVersion(activity) < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }

        return true
    }

    private fun getOpenGLVersion(activity: Activity): Double {
        val config = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return config.deviceConfigurationInfo.glEsVersion.toDouble()
    }

    companion object {
        const val MIN_OPENGL_VERSION: Double = 3.0
    }
}
