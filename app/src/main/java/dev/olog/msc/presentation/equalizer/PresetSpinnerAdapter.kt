package dev.olog.msc.presentation.equalizer

import android.content.Context
import android.widget.ArrayAdapter
import dev.olog.msc.R

class PresetSpinnerAdapter(
        context: Context,
        presets: List<String>

) : ArrayAdapter<String>(context, R.layout.layout_preset_spinner, R.id.presetName, presets) {

}