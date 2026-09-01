package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEachIndexed
import androidx.fragment.app.activityViewModels
import dev.olog.presentation.R
import dev.olog.presentation.base.TextViewDialog
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.presentation.databinding.FragmentEqualizerBandBinding
import dev.olog.presentation.databinding.FragmentEqualizerBinding
import dev.olog.presentation.widgets.equalizer.bar.BoxedVertical
import dev.olog.presentation.widgets.equalizer.croller.Croller
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class EqualizerFragment : BaseBottomSheetFragment() {

    companion object {
        const val TAG = "EqualizerFragment"
        const val DEFAULT_BAR_ALPHA = .75f

        @JvmStatic
        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    private val presenter by activityViewModels<EqualizerFragmentViewModel>()

    private var _binding: FragmentEqualizerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEqualizerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.powerSwitch.isChecked = presenter.isEqualizerEnabled()

        binding.bassKnob.apply {
            max = 1000
            progress = presenter.getBassStrength()
        }
        binding.virtualizerKnob.apply {
            max = 1000
            progress = presenter.getVirtualizerStrength()
        }

        buildBands()

        presenter.observePreset()
            .subscribe(viewLifecycleOwner) { preset ->
                binding.delete.toggleVisibility(preset.isCustom, true)

                binding.presetSpinner.text = preset.name

                preset.bands.forEachIndexed { index, band ->
                    val layout = binding.bands.getChildAt(index)
                    val bandBinding = FragmentEqualizerBandBinding.bind(layout)
                    bandBinding.seekbar.apply {
                        step = presenter.getBandStep()
                        max = presenter.getBandLimit()
                        min = -presenter.getBandLimit()
                        animateBar(this, band.gain)
                    }
                    bandBinding.seekbar.alpha = DEFAULT_BAR_ALPHA
                    bandBinding.frequency.text = band.displayableFrequency
                }
            }
    }

    private fun animateBar(bar: BoxedVertical, gain: Float) = viewLifecycleOwner.lifecycleScope.launch {
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
            val bandBinding = FragmentEqualizerBandBinding.inflate(layoutInflater, binding.bands, false)
            bandBinding.seekbar.apply {
                step = presenter.getBandStep()
                max = presenter.getBandLimit()
                min = -presenter.getBandLimit()
            }
            binding.bands.addView(bandBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.updateCurrentPresetIfCustom()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.bassKnob.setOnProgressChangedListener(onBassKnobChangeListener)
        binding.virtualizerKnob.setOnProgressChangedListener(onVirtualizerKnobChangeListener)

        setupBandListeners { band -> BandListener(band) }

        binding.powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) R.string.common_switch_on else R.string.common_switch_off
            binding.powerSwitch.text = getString(text)
            presenter.setEqualizerEnabled(isChecked)
        }
        binding.presetSpinner.setOnClickListener { changePreset() }
        binding.delete.setOnClickListener { presenter.deleteCurrentPreset() }
        binding.save.setOnClickListener {
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
        binding.bassKnob.setOnProgressChangedListener(null)
        binding.virtualizerKnob.setOnProgressChangedListener(null)

        setupBandListeners(null)

        binding.powerSwitch.setOnCheckedChangeListener(null)
        binding.presetSpinner.setOnClickListener(null)
        binding.delete.setOnClickListener(null)
        binding.save.setOnClickListener(null)
    }

    private fun changePreset() {
        viewLifecycleOwner.lifecycleScope.launch {
            val presets = withContext(Dispatchers.IO) {
                presenter.getPresets()
            }
            val popup = PopupMenu(ctx, binding.presetSpinner)
            popup.inflate(R.menu.empty)
            for (preset in presets) {
                popup.menu.add(Menu.NONE, preset.id.toInt(), Menu.NONE, preset.name)
            }
            popup.setOnMenuItemClickListener { menu ->
                val preset = presets.first { it.id.toInt() == menu.itemId }
                binding.presetSpinner.text = preset.name
                presenter.setCurrentPreset(preset)
                true
            }
            popup.show()
        }
    }

    private fun setupBandListeners(listener: ((Int) -> BandListener)?) {
        binding.bands.forEachIndexed { index, view ->
            FragmentEqualizerBandBinding.bind(view).seekbar.setOnBoxedPointsChangeListener(listener?.invoke(index))
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

}