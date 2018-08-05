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
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
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

            val option = intent.extras

            when (option.getInt("order")) {

                1 -> makeSphere(hitResult, Color.BLUE)

                2 ->  makeCylinder(hitResult, Color.GREEN)

                3 -> makeCube(hitResult, Color.RED)

                4 -> makeTextureSphere(hitResult, R.drawable.sun)
            }

        }
    }

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

    private fun makeSphere(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
                com.google.ar.sceneform.rendering.Color(color))
                .thenAccept { material ->
                    addNodeToScene(fragment, hitResult.createAnchor(),
                            ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), material))

                }
    }

    private fun makeCylinder(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
                com.google.ar.sceneform.rendering.Color(color))
                .thenAccept { material ->
                    addNodeToScene(fragment, hitResult.createAnchor(),
                            ShapeFactory.makeCylinder(0.1f, 0.3f, Vector3(0.0f, 0.15f, 0.0f), material))

                }
    }

    private fun makeCube(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
                com.google.ar.sceneform.rendering.Color(color))
                .thenAccept { material ->
                    addNodeToScene(fragment, hitResult.createAnchor(),
                            ShapeFactory.makeCube(Vector3(0.2f, 0.2f, 0.2f), Vector3(0.0f, 0.15f, 0.0f), material))

                }
    }

    private fun addNodeToScene(fragment: ArFragment, createAnchor: Anchor, modelObject: ModelRenderable) {

        val anchorNode = AnchorNode(createAnchor)

        TransformableNode(fragment.transformationSystem).apply {
            renderable = modelObject
            setParent(anchorNode)
            select()
        }

        fragment.arSceneView.scene.addChild(anchorNode)
    }
}