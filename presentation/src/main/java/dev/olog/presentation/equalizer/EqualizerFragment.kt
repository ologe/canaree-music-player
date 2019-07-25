package dev.olog.presentation.equalizer

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import dev.olog.injection.equalizer.IEqualizer
import dev.olog.presentation.R
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import dev.olog.presentation.widgets.equalizer.croller.Croller
import dev.olog.shared.android.extensions.lazyFast
import kotlinx.android.synthetic.main.fragment_equalizer.*
import javax.inject.Inject

internal class EqualizerFragment : BaseBottomSheetFragment(), IEqualizer.Listener {

    companion object {
        const val TAG = "EqualizerFragment"

        @JvmStatic
        fun newInstance(): EqualizerFragment {
            return EqualizerFragment()
        }
    }

    @Inject
    lateinit var presenter: EqualizerFragmentPresenter
    private val adapter by lazyFast {
        PresetPagerAdapter(this, presenter.getPresets().toMutableList())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val presets = presenter.getPresets()

        if (presets.isNotEmpty()) {
            pager.adapter = adapter
            pager.currentItem = presenter.getCurrentPreset()
//            pageIndicator.setViewPager(pager) TODO
        }

        powerSwitch.isChecked = presenter.isEqualizerEnabled()

        bassKnob.apply {
            max = 100
            progress = presenter.getBassStrength()
        }
        virtualizerKnob.apply {
            max = 100
            progress = presenter.getVirtualizerStrength()
        }

        band1.initializeBandHeight(presenter.getBandLevel(0))
        band2.initializeBandHeight(presenter.getBandLevel(1))
        band3.initializeBandHeight(presenter.getBandLevel(2))
        band4.initializeBandHeight(presenter.getBandLevel(3))
        band5.initializeBandHeight(presenter.getBandLevel(4))
    }

    override fun onResume() {
        super.onResume()
        bassKnob.setOnProgressChangedListener(onBassKnobChangeListener)
        virtualizerKnob.setOnProgressChangedListener(onVirtualizerKnobChangeListener)
        pager.registerOnPageChangeCallback(onPageChangeListener)
        presenter.addEqualizerListener(this)

        band1.setLevel = onBandLevelChange
        band2.setLevel = onBandLevelChange
        band3.setLevel = onBandLevelChange
        band4.setLevel = onBandLevelChange
        band5.setLevel = onBandLevelChange

        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            val text = if (isChecked) R.string.common_switch_on else R.string.common_switch_off
            powerSwitch.text = getString(text)
            presenter.setEqualizerEnabled(isChecked)
        }
    }

    override fun onPause() {
        super.onPause()
        bassKnob.setOnProgressChangedListener(null)
        virtualizerKnob.setOnProgressChangedListener(null)
        pager.unregisterOnPageChangeCallback(onPageChangeListener)
        presenter.removeEqualizerListener(this)

        band1.setLevel = null
        band2.setLevel = null
        band3.setLevel = null
        band4.setLevel = null
        band5.setLevel = null

        powerSwitch.setOnCheckedChangeListener(null)
    }

    private val onBassKnobChangeListener = Croller.onProgressChangedListener { progress ->
        presenter.setBassStrength(progress)
    }

    private val onVirtualizerKnobChangeListener = Croller.onProgressChangedListener { progress ->
        presenter.setVirtualizerStrength(progress)
    }

    private val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            presenter.setPreset(position)
        }
    }



    private val onBandLevelChange = { band: Int, level: Float ->
        presenter.setBandLevel(band, level)
    }

    override fun onPresetChange(band: Int, level: Float) {
        band1.onPresetChange(band, level)
        band2.onPresetChange(band, level)
        band3.onPresetChange(band, level)
        band4.onPresetChange(band, level)
        band5.onPresetChange(band, level)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}