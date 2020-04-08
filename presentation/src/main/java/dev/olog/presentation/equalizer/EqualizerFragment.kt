package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.dialog.TextViewDialog
import dev.olog.feature.presentation.base.fragment.BaseBottomSheetFragment
import dev.olog.feature.presentation.base.extensions.onClick
import kotlinx.android.synthetic.main.fragment_equalizer.*
import kotlinx.android.synthetic.main.fragment_equalizer_band.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class EqualizerFragment : BaseBottomSheetFragment() {

    companion object {
        const val TAG = "EqualizerFragment"

        @JvmStatic
        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val presenter by viewModels<EqualizerFragmentViewModel> {
        factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        powerSwitch.isChecked = presenter.isEqualizerEnabled()

        bassKnob.apply {
            max = 1000
            setProgress(presenter.getBassStrength())
        }
        virtualizerKnob.apply {
            max = 1000
            setProgress(presenter.getVirtualizerStrength())
        }

        buildBands()

        presenter.currentPreset
            .onEach { preset ->
                delete.isVisible = preset.isCustom

                presetSpinner.text = preset.name

                preset.bands.forEachIndexed { index, band ->
                    val layout = bands.getChildAt(index)
                    layout.bar.animateProgress(
                        band.gain,
                        -presenter.getBandLimit(),
                        presenter.getBandLimit()
                    )
                    layout.frequency.text = band.displayableFrequency
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun buildBands() {
        for (band in 0 until presenter.getBandCount()) {
            val layout = layoutInflater.inflate(R.layout.fragment_equalizer_band, null, false)
            bands.addView(layout)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.updateCurrentPresetIfCustom()
    }

    override fun onResume() {
        super.onResume()
        bassKnob.onProgressChanged = { presenter.setBassStrength(it) }
        virtualizerKnob.onProgressChanged = { presenter.setVirtualizerStrength(it) }

        setupBandListeners()

        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) R.string.common_switch_on else R.string.common_switch_off
            powerSwitch.text = getString(text)
            presenter.setEqualizerEnabled(isChecked)
        }
        presetSpinner.onClick { changePreset() }
        delete.setOnClickListener { presenter.deleteCurrentPreset() }
        save.setOnClickListener {
            // create new preset
            // TODO localization
            TextViewDialog(
                requireActivity(),
                "Save preset",
                null
            )
                .addTextView(customizeWrapper = { hint = "Preset name" })
                .show(positiveAction = TextViewDialog.Action("OK") {
                    val title = it[0].text.toString()
                    !title.isBlank() && presenter.addPreset(title)
                }, neutralAction = TextViewDialog.Action("Cancel") { true })
        }
    }

    override fun onPause() {
        super.onPause()
        bassKnob.onProgressChanged = null
        virtualizerKnob.onProgressChanged = null

        bands.forEach { view ->
            view.bar.onProgressChanged = null
        }

        powerSwitch.setOnCheckedChangeListener(null)
        presetSpinner.setOnClickListener(null)
        delete.setOnClickListener(null)
        save.setOnClickListener(null)
    }

    private suspend fun changePreset() {
        val presets = withContext(Dispatchers.IO) {
            presenter.getPresets()
        }
        val popup = PopupMenu(requireContext(), presetSpinner)
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

    private fun setupBandListeners() {
        bands.forEachIndexed { index, view ->
            view.bar.onProgressChanged = { presenter.setBandLevel(index, it) }
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}