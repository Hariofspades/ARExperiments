package com.hariofspades.quaternion

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity(), ControlFragment.OnQuaternionChangedListener {

    private lateinit var arFragment: ArFragment

    private var anchorNode: AnchorNode? = null
    private lateinit var cubeNode: Node

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSceneformFragment()
    }

    private fun setupSceneformFragment() {

        arFragment = supportFragmentManager
                .findFragmentById(R.id.sceneform_fragment)
                as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            makeCube(hitResult, Color.RED)
        }

    }

    /**
     * Constructs cube of radius 1f and at position 0.0f, 0.15f, 0.0f on the plane
     * Here Vector3 takes up the size - 0.2f, 0.2f, 0.2f
     * @param hitResult - If the hit result is a plane
     * @param res - Color
     */
    private fun makeCube(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
                com.google.ar.sceneform.rendering.Color(color))
                .thenAccept { material ->
                    addNodeToScene(arFragment, hitResult.createAnchor(),
                            ShapeFactory.makeCube(
                                    Vector3(0.2f, 0.2f, 0.2f),
                                    Vector3(0.0f, 0.15f, 0.0f),
                                    material)
                    )

                }
    }

    /**
     * Adds node to the scene and the object.
     * @param fragment - sceneform fragment
     * @param anchor - created anchor at the tapped position
     * @param modelObject - rendered object
     */
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, modelObject: ModelRenderable) {

        anchorNode = AnchorNode(anchor).apply {
            setParent(fragment.arSceneView.scene)
        }

        anchorNode?.addChild(createCubeNode(modelObject))

    }

    override fun onLeft(value: Float) {
        cubeNode.let {
            Log.d("left", value.toString())
            it.localRotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), value)
        }
    }

    override fun onRight(value: Float) {
        cubeNode.let {
            Log.d("right", value.toString())
            it.localRotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), -value)
        }
    }

    fun createCubeNode(modelObject: ModelRenderable): Node {
        cubeNode =  Node().apply {
            renderable = modelObject
            localPosition = Vector3(0.0f, 0.15f, 0.0f)
        }

        return cubeNode
    }
}
