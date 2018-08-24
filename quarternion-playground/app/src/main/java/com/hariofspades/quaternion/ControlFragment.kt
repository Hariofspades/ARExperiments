package com.hariofspades.quaternion


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_control.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class ControlFragment : Fragment() {

    lateinit var rootView: View
    lateinit var onQuaternionChangedListener: OnQuaternionChangedListener

    var value1: Float = 10.0f
    var value2: Float = 10.0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_control, container, false)
        setupControls()
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            onQuaternionChangedListener = activity as OnQuaternionChangedListener

        } catch (exception: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement onVector3ChangedListener")
        }
    }

    private fun setupControls() {

        rootView.apply {

            left_button.setOnClickListener {
                value1 += 50
                onQuaternionChangedListener.onLeft(value1)
            }

            right_button.setOnClickListener {
                value2 += 50
                onQuaternionChangedListener.onLeft(value2)
            }
        }
    }


    interface OnQuaternionChangedListener {
        fun onLeft(value: Float)
        fun onRight(value: Float)
    }

}
