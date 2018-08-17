package com.hariofspades.vector3playground


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass for [AR]
 *
 */
class ARFragment : Fragment() {

    lateinit var arView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        arView =  inflater.inflate(R.layout.fragment_ar, container, false)
        return arView
    }


}
