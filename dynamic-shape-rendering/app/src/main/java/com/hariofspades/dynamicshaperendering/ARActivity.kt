package com.hariofspades.dynamicshaperendering

import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.*

/**
 * Activity contatining Sceneform fragment, renders shape on command form
 * the previous screen
 */
class ARActivity : AppCompatActivity() {

    lateinit var fragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        fragment = supportFragmentManager
                .findFragmentById(R.id.sceneform_fragment)
                as ArFragment

        fragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            when (intent.extras.getInt("order")) {

                1 -> makeSphere(hitResult, Color.BLUE)

                2 ->  makeCylinder(hitResult, Color.GREEN)

                3 -> makeCube(hitResult, Color.RED)

                4 -> makeTextureSphere(hitResult, R.drawable.sun)
            }

        }
    }

    /**
     * Constructs sphere of radius 1f and at position 0.0f, 0.15f, 0.0f and with TEXTURE
     * @param hitResult - If the hit result is a plane
     * @param res - Image res for texture, here [R.drawable.sun]
     */
    private fun makeTextureSphere(hitResult: HitResult, res: Int) {
        Texture.builder().setSource(BitmapFactory.decodeResource(resources, res))
                .build()
                .thenAccept {
                    MaterialFactory.makeOpaqueWithTexture(this, it)
                            .thenAccept { material ->
                                addNodeToScene(fragment, hitResult.createAnchor(),
                                        ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), material))

                            }
                }
    }

    /**
     * Constructs sphere of radius 1f and at position 0.0f, 0.15f, 0.0f on the plane
     * @param hitResult - If the hit result is a plane
     * @param res - Color
     */
    private fun makeSphere(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
                com.google.ar.sceneform.rendering.Color(color))
                .thenAccept { material ->
                    addNodeToScene(fragment, hitResult.createAnchor(),
                            ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), material))

                }
    }

    /**
     * Constructs cylinder of radius 1f and at position 0.0f, 0.15f, 0.0f on the plane
     * Need to mention height for the cylinder
     * @param hitResult - If the hit result is a plane
     * @param res - Color
     */
    private fun makeCylinder(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
                com.google.ar.sceneform.rendering.Color(color))
                .thenAccept { material ->
                    addNodeToScene(fragment, hitResult.createAnchor(),
                            ShapeFactory.makeCylinder(0.1f, 0.3f, Vector3(0.0f, 0.15f, 0.0f), material))

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
                    addNodeToScene(fragment, hitResult.createAnchor(),
                            ShapeFactory.makeCube(Vector3(0.2f, 0.2f, 0.2f), Vector3(0.0f, 0.15f, 0.0f), material))

                }
    }


    /**
     * Adds node to the scene and the object.
     * @param fragment - sceneform fragment
     * @param anchor - created anchor at the tapped position
     * @param modelObject - rendered object
     */
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, modelObject: ModelRenderable) {

        val anchorNode = AnchorNode(anchor)

        TransformableNode(fragment.transformationSystem).apply {
            renderable = modelObject
            setParent(anchorNode)
            select()
        }

        fragment.arSceneView.scene.addChild(anchorNode)
    }
}