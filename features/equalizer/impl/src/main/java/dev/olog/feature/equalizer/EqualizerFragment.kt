package dev.olog.feature.equalizer

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.equalizer.widget.BoxedVertical
import dev.olog.feature.equalizer.widget.croller.Croller
import dev.olog.platform.fragment.BaseBottomSheetFragment
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.subscribe
import dev.olog.ui.dialog.TextViewDialog
import kotlinx.android.synthetic.main.fragment_equalizer.*
import kotlinx.android.synthetic.main.fragment_equalizer_band.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@AndroidEntryPoint
internal class EqualizerFragment : BaseBottomSheetFragment() {

    companion object {
        val TAG = FragmentTagFactory.create(EqualizerFragment::class)
        const val DEFAULT_BAR_ALPHA = .75f

        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    private val viewModel by activityViewModels<EqualizerFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        powerSwitch.isChecked = viewModel.isEqualizerEnabled()

        bassKnob.apply {
            max = 1000
            progress = viewModel.getBassStrength()
        }
        virtualizerKnob.apply {
            max = 1000
            progress = viewModel.getVirtualizerStrength()
        }

        buildBands()

        viewModel.observePreset()
            .subscribe(viewLifecycleOwner) { preset ->
                delete.isVisible = preset.isCustom

                presetSpinner.text = preset.name

                preset.bands.forEachIndexed { index, band ->
                    val layout = bands.getChildAt(index)
                    layout.seekbar.apply {
                        step = viewModel.getBandStep()
                        max = viewModel.getBandLimit()
                        min = -viewModel.getBandLimit()
                        animateBar(this, band.gain)
                    }
                    layout.seekbar.alpha = DEFAULT_BAR_ALPHA
                    layout.frequency.text = band.displayableFrequency
                }
            }
    }

    private fun animateBar(bar: BoxedVertical, gain: Float) = launchWhenResumed {
        var duration = 150f
        val timeDelta = 16f
        val progressDelta = (gain - bar.value) * (timeDelta / duration)
        while (duration > 0){
            delay(timeDelta.toLong())
            duration -= timeDelta
            bar.value += progressDelta
        }
        bar.value = gain // set exact value
    }

    private fun buildBands() {
        for (band in 0 until viewModel.getBandCount()) {
            val layout = layoutInflater.inflate(R.layout.fragment_equalizer_band, null, false)
            layout.seekbar.apply {
                step = viewModel.getBandStep()
                max = viewModel.getBandLimit()
                min = -viewModel.getBandLimit()
            }
            bands.addView(layout)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateCurrentPresetIfCustom()
    }

    override fun onResume() {
        super.onResume()
        bassKnob.setOnProgressChangedListener(onBassKnobChangeListener)
        virtualizerKnob.setOnProgressChangedListener(onVirtualizerKnobChangeListener)

        setupBandListeners { band -> BandListener(band) }

        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) localization.R.string.common_switch_on else localization.R.string.common_switch_off
            powerSwitch.text = getString(text)
            viewModel.setEqualizerEnabled(isChecked)
        }
        presetSpinner.setOnClickListener { changePreset() }
        delete.setOnClickListener { viewModel.deleteCurrentPreset() }
        save.setOnClickListener {
            // create new preset
            TextViewDialog(requireContext(), "Save preset", null)
                .addTextView(customizeWrapper = { hint = "Preset name" })
                .show(positiveAction = TextViewDialog.Action("OK") {
                    val title = it[0].text.toString()
                    !title.isBlank() && viewModel.addPreset(title)
                }, neutralAction = TextViewDialog.Action("Cancel") { true })
        }
    }

    override fun onPause() {
        super.onPause()
        bassKnob.setOnProgressChangedListener(null)
        virtualizerKnob.setOnProgressChangedListener(null)

        setupBandListeners(null)

        powerSwitch.setOnCheckedChangeListener(null)
        presetSpinner.setOnClickListener(null)
        delete.setOnClickListener(null)
        save.setOnClickListener(null)
    }

    private fun changePreset() {
        launchWhenResumed {
            val presets = withContext(Dispatchers.IO) {
                viewModel.getPresets()
            }
            val popup = PopupMenu(requireContext(), presetSpinner)
            popup.inflate(dev.olog.ui.R.menu.empty)
            for (preset in presets) {
                popup.menu.add(Menu.NONE, preset.id.toInt(), Menu.NONE, preset.name)
            }
            popup.setOnMenuItemClickListener { menu ->
                val preset = presets.first { it.id.toInt() == menu.itemId }
                presetSpinner.text = preset.name
                viewModel.setCurrentPreset(preset)
                true
            }
            popup.show()
        }
    }

    private fun setupBandListeners(listener: ((Int) -> BandListener)?) {
        bands.forEachIndexed { index, view ->
            view.seekbar.setOnBoxedPointsChangeListener(listener?.invoke(index))
        }
    }

    inner class BandListener(private val band: Int) : BoxedVertical.OnValuesChangeListener {

        override fun onPointsChanged(seekbar: BoxedVertical, value: Float) {
            viewModel.setBandLevel(band, value)
        }
        override fun onStartTrackingTouch(seekbar: BoxedVertical) {
            seekbar.animate()
                .setDuration(200)
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.05f)
        }

        override fun onStopTrackingTouch(seekbar: BoxedVertical) {
            seekbar.animate()
                .setDuration(200)
                .alpha(DEFAULT_BAR_ALPHA)
                .scaleX(1f)
                .scaleY(1f)
        }
    }

    private val onBassKnobChangeListener = Croller.onProgressChangedListener { progress ->
        viewModel.setBassStrength(progress)
    }

    private val onVirtualizerKnobChangeListener = Croller.onProgressChangedListener { progress ->
        viewModel.setVirtualizerStrength(progress)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}