package com.hariofspades.dynamicshaperendering

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class MainActivity : AppCompatActivity() {

    lateinit var fragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = supportFragmentManager
                .findFragmentById(R.id.sceneform_fragment)
                as ArFragment

        fragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            MaterialFactory.makeOpaqueWithColor(this,
                    com.google.ar.sceneform.rendering.Color(Color.BLUE))
                    .thenAccept { material ->
                        addNodeToScene(fragment, hitResult.createAnchor(),
                                ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), material))

                    }
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
