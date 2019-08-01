package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEachIndexed
import androidx.lifecycle.ViewModelProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.TextViewDialog
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.presentation.widgets.equalizer.bar.BoxedVertical
import dev.olog.presentation.widgets.equalizer.croller.Croller
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.extensions.viewModelProvider
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_equalizer.*
import kotlinx.android.synthetic.main.fragment_equalizer_band.view.*
import kotlinx.coroutines.*
import javax.inject.Inject

internal class EqualizerFragment : BaseBottomSheetFragment(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "EqualizerFragment"

        @JvmStatic
        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val presenter by lazyFast { viewModelProvider<EqualizerFragmentPresenter>(factory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        powerSwitch.isChecked = presenter.isEqualizerEnabled()

        bassKnob.apply {
            max = 100
            progress = presenter.getBassStrength()
        }
        virtualizerKnob.apply {
            max = 100
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
                        value = band.gain
                    }
                    layout.frequency.text = band.displayableFrequency
                }
            }
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

    override fun onResume() {
        super.onResume()
        bassKnob.setOnProgressChangedListener(onBassKnobChangeListener)
        virtualizerKnob.setOnProgressChangedListener(onVirtualizerKnobChangeListener)

        setupBandListeners { BandListener(it) }

        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) R.string.common_switch_on else R.string.common_switch_off
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
                    presenter.saveCurrentPreset(it[0].text.toString())
                    true
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
        override fun onStartTrackingTouch(seekbar: BoxedVertical) {

        }

        override fun onPointsChanged(seekbar: BoxedVertical, value: Float) {
            presenter.setBandLevel(band, value)
        }

        override fun onStopTrackingTouch(p0: BoxedVertical?) {
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