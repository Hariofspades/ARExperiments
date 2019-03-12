package com.hariofspades.arface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class FaceARFragment : ArFragment() {

    override fun getSessionConfiguration(session: Session?): Config {
        return Config(session).apply { augmentedFaceMode = Config.AugmentedFaceMode.MESH3D }
    }

    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.FRONT_CAMERA)
    }

    /**
     * Override to turn off planeDiscoveryController. Plane trackables are not supported with the
     * front camera.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val layout = super.onCreateView(inflater, container, savedInstanceState) as FrameLayout
        planeDiscoveryController.apply {
            hide()
            setInstructionView(null)
        }
        return layout
    }


}
