package dev.olog.msc.presentation.equalizer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import dev.olog.msc.R
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_preset.*
import kotlinx.android.synthetic.main.fragment_preset.view.*

class PresetFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance(presetName: String, presets: List<String>): PresetFragment {

            return PresetFragment().withArguments(
                    "preset" to presetName,
                    "presetList" to presets
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preset, container, false)
        val preset = arguments!!.getString("preset")
        view.presetName.text = preset
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presets = arguments!!.getStringArrayList("presetList")
        view.presetSpinner.adapter = PresetSpinnerAdapter(act, presets)
    }

    override fun onResume() {
        super.onResume()
        presetSpinner.onItemSelectedListener = this
    }

    override fun onPause() {
        super.onPause()
        presetSpinner.onItemSelectedListener = null
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println("on item selected $position")
        // todo not working
//        act.findViewById<ViewPager>(R.id.pager).currentItem = position
    }

}
