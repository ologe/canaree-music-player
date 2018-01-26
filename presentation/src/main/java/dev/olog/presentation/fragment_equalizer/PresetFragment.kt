package dev.olog.presentation.fragment_equalizer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.olog.presentation.R
import kotlinx.android.synthetic.main.fragment_preset.view.*

class PresetFragment : Fragment() {

    companion object {
        fun newInstance(presetName: String): PresetFragment {
            val bundle = Bundle()
            bundle.putString("preset", presetName)
            val fragment = PresetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preset, container, false)
        val preset = arguments!!.getString("preset")
        view.presetName.text = preset
        return view
    }
}
