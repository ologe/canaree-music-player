package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.presentation.R
import dev.olog.presentation.base.TextViewDialog
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentEqualizerBandBinding
import dev.olog.presentation.databinding.FragmentEqualizerBinding
import dev.olog.presentation.widgets.equalizer.bar.BoxedVertical
import dev.olog.presentation.widgets.equalizer.croller.Croller
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
internal class EqualizerFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "EqualizerFragment"
        const val DEFAULT_BAR_ALPHA = .75f

        @JvmStatic
        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    private val viewModel by viewModels<EqualizerFragmentViewModel>()

    private val binding by viewBinding(FragmentEqualizerBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equalizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.powerSwitch.isChecked = viewModel.isEqualizerEnabled()

        binding.bassKnob.apply {
            max = 1000
            progress = viewModel.getBassStrength()
        }
        binding.virtualizerKnob.apply {
            max = 1000
            progress = viewModel.getVirtualizerStrength()
        }

        buildBands()

        viewModel.observePreset()
            .subscribe(viewLifecycleOwner) { preset ->
                binding.delete.toggleVisibility(preset.isCustom, true)

                binding.presetSpinner.text = preset.name

                preset.bands.forEachIndexed { index, band ->
                    val layout = binding.bands.getChildAt(index)
                    layout.findViewById<BoxedVertical>(R.id.seekbar).apply {
                        step = viewModel.getBandStep()
                        max = viewModel.getBandLimit()
                        min = -viewModel.getBandLimit()
                        animateBar(this, band.gain)
                    }
                    layout.findViewById<View>(R.id.seekbar).alpha = DEFAULT_BAR_ALPHA
                    layout.findViewById<TextView>(R.id.frequency).text = band.displayableFrequency
                }
            }
    }

    private fun animateBar(bar: BoxedVertical, gain: Float) = viewLifecycleScope.launch {
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
            val layout = FragmentEqualizerBandBinding.inflate(layoutInflater, null, false)
            layout.seekbar.apply {
                step = viewModel.getBandStep()
                max = viewModel.getBandLimit()
                min = -viewModel.getBandLimit()
            }
            binding.bands.addView(layout.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateCurrentPresetIfCustom()
    }

    override fun onResume() {
        super.onResume()
        binding.bassKnob.setOnProgressChangedListener(onBassKnobChangeListener)
        binding.virtualizerKnob.setOnProgressChangedListener(onVirtualizerKnobChangeListener)

        setupBandListeners { band -> BandListener(band) }

        binding.powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) R.string.common_switch_on else R.string.common_switch_off
            binding.powerSwitch.text = getString(text)
            viewModel.setEqualizerEnabled(isChecked)
        }
        binding.presetSpinner.setOnClickListener { changePreset() }
        binding.delete.setOnClickListener { viewModel.deleteCurrentPreset() }
        binding.save.setOnClickListener {
            // create new preset
            TextViewDialog(ctx, "Save preset", null)
                .addTextView(customizeWrapper = { hint = "Preset name" })
                .show(positiveAction = TextViewDialog.Action("OK") {
                    val title = it[0].text.toString()
                    !title.isBlank() && viewModel.addPreset(title)
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
        viewLifecycleScope.launch {
            val presets = withContext(Dispatchers.IO) {
                viewModel.getPresets()
            }
            val popup = PopupMenu(ctx, binding.presetSpinner)
            popup.inflate(R.menu.empty)
            for (preset in presets) {
                popup.menu.add(Menu.NONE, preset.id.toInt(), Menu.NONE, preset.name)
            }
            popup.setOnMenuItemClickListener { menu ->
                val preset = presets.first { it.id.toInt() == menu.itemId }
                binding.presetSpinner.text = preset.name
                viewModel.setCurrentPreset(preset)
                true
            }
            popup.show()
        }
    }

    private fun setupBandListeners(listener: ((Int) -> BandListener)?) {
        binding.bands.forEachIndexed { index, view ->
            view.findViewById<BoxedVertical>(R.id.seekbar).setOnBoxedPointsChangeListener(listener?.invoke(index))
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

}