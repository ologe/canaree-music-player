package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEachIndexed
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.TextViewDialog
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.presentation.widgets.equalizer.bar.BoxedVertical
import dev.olog.presentation.widgets.equalizer.croller.Croller
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import kotlinx.android.synthetic.main.fragment_equalizer.*
import kotlinx.android.synthetic.main.fragment_equalizer_band.view.*
import kotlinx.coroutines.*

@AndroidEntryPoint
internal class EqualizerFragment : BaseBottomSheetFragment(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "EqualizerFragment"
        const val DEFAULT_BAR_ALPHA = .75f

        @JvmStatic
        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    private val presenter by activityViewModels<EqualizerFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        powerSwitch.isChecked = presenter.isEqualizerEnabled()

        bassKnob.apply {
            max = 1000
            progress = presenter.getBassStrength()
        }
        virtualizerKnob.apply {
            max = 1000
            progress = presenter.getVirtualizerStrength()
        }

        buildBands()

        presenter.observePreset()
            .subscribe(viewLifecycleOwner) { preset ->
                delete.toggleVisibility(preset.isCustom, true)

                presetSpinner.text = preset.name

                preset.bands.forEachIndexed { index, band ->
                    val layout = bands.getChildAt(index)
                    layout.seekbar.apply {
                        step = presenter.getBandStep()
                        max = presenter.getBandLimit()
                        min = -presenter.getBandLimit()
                        animateBar(this, band.gain)
                    }
                    layout.seekbar.alpha = DEFAULT_BAR_ALPHA
                    layout.frequency.text = band.displayableFrequency
                }
            }
    }

    private fun animateBar(bar: BoxedVertical, gain: Float) = launch {
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
        for (band in 0 until presenter.getBandCount()) {
            val layout = layoutInflater.inflate(R.layout.fragment_equalizer_band, null, false)
            layout.seekbar.apply {
                step = presenter.getBandStep()
                max = presenter.getBandLimit()
                min = -presenter.getBandLimit()
            }
            bands.addView(layout)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.updateCurrentPresetIfCustom()
    }

    override fun onResume() {
        super.onResume()
        bassKnob.setOnProgressChangedListener(onBassKnobChangeListener)
        virtualizerKnob.setOnProgressChangedListener(onVirtualizerKnobChangeListener)

        setupBandListeners { band -> BandListener(band) }

        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) dev.olog.shared.android.R.string.common_switch_on else dev.olog.shared.android.R.string.common_switch_off
            powerSwitch.text = getString(text)
            presenter.setEqualizerEnabled(isChecked)
        }
        presetSpinner.setOnClickListener { changePreset() }
        delete.setOnClickListener { presenter.deleteCurrentPreset() }
        save.setOnClickListener {
            // create new preset
            TextViewDialog(ctx, "Save preset", null)
                .addTextView(customizeWrapper = { hint = "Preset name" })
                .show(positiveAction = TextViewDialog.Action("OK") {
                    val title = it[0].text.toString()
                    !title.isBlank() && presenter.addPreset(title)
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
        launch {
            val presets = withContext(Dispatchers.IO) {
                presenter.getPresets()
            }
            val popup = PopupMenu(ctx, presetSpinner)
            popup.inflate(R.menu.empty)
            for (preset in presets) {
                popup.menu.add(Menu.NONE, preset.id.toInt(), Menu.NONE, preset.name)
            }
            popup.setOnMenuItemClickListener { menu ->
                val preset = presets.first { it.id.toInt() == menu.itemId }
                presetSpinner.text = preset.name
                presenter.setCurrentPreset(preset)
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
            presenter.setBandLevel(band, value)
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
        presenter.setBassStrength(progress)
    }

    private val onVirtualizerKnobChangeListener = Croller.onProgressChangedListener { progress ->
        presenter.setVirtualizerStrength(progress)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}