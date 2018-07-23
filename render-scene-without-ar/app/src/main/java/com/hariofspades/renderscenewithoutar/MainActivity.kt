package com.hariofspades.renderscenewithoutar

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import kotlinx.android.synthetic.main.activity_main.*

/**
 * created by Hari Vignesh Jayapalan
 *
 * MainActivity, where you can place your 3D models and display it for the phones which does not
 * support ARCore. Refer the blog for further usecase and implementation assistance
 *
 */
class MainActivity : AppCompatActivity() {

    lateinit var scene: Scene

    lateinit var cupCakeNode: Node

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scene = sceneView.scene // get current scene
        renderObject(Uri.parse("cupcake.sfb")) // Render the object
    }

    /**
     * load the 3D model in the space
     * @param parse URI of the model, imported using Sceneform plugin
     */
    private fun renderObject(parse: Uri) {
        ModelRenderable.builder()
                .setSource(this, parse)
                .build()
                .thenAccept {
                    addNodeToScene(it)
                }
                .exceptionally {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(it.message)
                            .setTitle("error!")
                    val dialog = builder.create()
                    dialog.show()
                    return@exceptionally null
                }

    }

    /**
     * Adds a node to the current scene
     * @param model - rendered model
     */
    private fun addNodeToScene(model: ModelRenderable?) {

        model?.let {
            cupCakeNode = Node().apply {
                setParent(scene)
                localPosition = Vector3(0f, 0f, -1f)
                localScale = Vector3(3f, 3f, 3f)
                name = "Cupcake"
                renderable = it
            }

            scene.addChild(cupCakeNode)
        }
    }

    override fun onPause() {
        super.onPause()
        sceneView.pause()
    }

    override fun onResume() {
        super.onResume()
        sceneView.resume()
    }

}
