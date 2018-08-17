package com.hariofspades.vector3playground


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_control.view.*


/**
 * A simple [Fragment] subclass for controls
 *
 */
class ControlFragment : Fragment() {

    lateinit var rootView: View
    lateinit var vector3ChangedListener: OnVector3ChangedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_control, container, false)
        setupSeekBar()
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            vector3ChangedListener = activity as OnVector3ChangedListener

        } catch (exception: ClassCastException) {
            throw ClassCastException("${activity.toString()} must implement onVector3ChangedListener")
        }
    }

    private fun setupSeekBar() {

        rootView.apply {

            seek_x.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    vector3ChangedListener.onSeekXChanged(getConvertedValue(p1))
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            seek_y.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    vector3ChangedListener.onSeekYChanged(getConvertedValue(p1))
                    //Toast.makeText(activity, "Value Y: ${getConvertedValue(p1)}", Toast.LENGTH_LONG).show()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            seek_z.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    vector3ChangedListener.onSeekZChanged(getConvertedValue(p1))
                    //Toast.makeText(activity, "Value Z: ${getConvertedValue(p1)}", Toast.LENGTH_LONG).show()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }

    fun getConvertedValue(interval: Int): Float {
        return 0.1f * interval
    }

    interface OnVector3ChangedListener {
        fun onSeekXChanged(value: Float)
        fun onSeekYChanged(value: Float)
        fun onSeekZChanged(value: Float)
    }

}
