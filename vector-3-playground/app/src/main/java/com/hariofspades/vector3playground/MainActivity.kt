package com.hariofspades.vector3playground

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : FragmentActivity(), ControlFragment.OnVector3ChangedListener {

    lateinit var arFragment: ArFragment

    var anchorNode: AnchorNode? = null

    var xCoordinate: Float = 0.2f
    var yCoordinate: Float = 0.2f
    var zCoordinate: Float = 0.2f

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
                                    Vector3(xCoordinate, yCoordinate, zCoordinate),
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
            renderable = modelObject
            setParent(anchorNode)
        }

        fragment.arSceneView.scene.addChild(anchorNode)

    }

    override fun onSeekXChanged(value: Float) {
        anchorNode?.let {
            xCoordinate = value
            it.localScale = Vector3(xCoordinate, yCoordinate, zCoordinate)
        }
    }

    override fun onSeekYChanged(value: Float) {
        anchorNode?.let {
            yCoordinate = value
            it.localScale = Vector3(xCoordinate, yCoordinate, zCoordinate)
        }
    }

    override fun onSeekZChanged(value: Float) {
        anchorNode?.let {
            zCoordinate = value
            it.localScale = Vector3(xCoordinate, yCoordinate, zCoordinate)
        }
    }
}
