package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.olog.presentation.R
import dev.olog.shared.extensions.withArguments
import kotlinx.android.synthetic.main.fragment_preset.view.*

class PresetFragment : Fragment() {

    companion object {
        private const val ARGUMENT_PRESET = "preset"

        @JvmStatic
        fun newInstance(presetName: String): PresetFragment {

            return PresetFragment().withArguments(
                ARGUMENT_PRESET to presetName
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preset, container, false)
        val preset = arguments!!.getString(ARGUMENT_PRESET)
        view.presetName.text = preset
        return view
    }

}
