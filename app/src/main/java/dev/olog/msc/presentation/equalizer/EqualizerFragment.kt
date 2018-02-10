package dev.olog.msc.presentation.equalizer

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.equalizer.widget.InfinitePagerAdapter
import dev.olog.msc.presentation.equalizer.widget.RadialKnob
import dev.olog.shared_android.interfaces.equalizer.IEqualizer
import kotlinx.android.synthetic.main.fragment_equalizer.view.*
import javax.inject.Inject

class EqualizerFragment : BaseFragment(), IEqualizer.Listener {

    companion object {
        const val TAG = "EqualizerFragment"
    }

    @Inject lateinit var presenter: EqualizerFragmentPresenter
    private lateinit var adapter : InfinitePagerAdapter

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        adapter = InfinitePagerAdapter(PresetPagerAdapter(
                activity!!.supportFragmentManager, presenter.getPresets()))

        view.pager.adapter = adapter
        view.pager.currentItem = presenter.getCurrentPreset()
        view.pageIndicator.setViewPager(view.pager)

        view.powerSwitch.isChecked = presenter.isEqualizerEnabled()
        view.replayGainSwitch.isChecked = presenter.isReplayGainEnabled()

        view.bassKnob.setMax(100)
        view.virtualizerKnob.setMax(100)
        view.bassKnob.setValue(presenter.getBassStrength())
        view.virtualizerKnob.setValue(presenter.getVirtualizerStrength())

        if (!presenter.isReplayGainEnabled()){
            view.replayGainSwitch.visibility = View.GONE
            view.replayGain.visibility = View.GONE
        }

        view.band1.initializeBandHeight(presenter.getBandLevel(0))
        view.band2.initializeBandHeight(presenter.getBandLevel(1))
        view.band3.initializeBandHeight(presenter.getBandLevel(2))
        view.band4.initializeBandHeight(presenter.getBandLevel(3))
        view.band5.initializeBandHeight(presenter.getBandLevel(4))
    }

    override fun onResume() {
        super.onResume()
        view!!.powerSwitch.setOnCheckedChangeListener { _, isChecked -> presenter.setEqualizerEnabled(isChecked) }
        view!!.replayGainSwitch.setOnCheckedChangeListener { _, isChecked -> presenter.setReplayGainEnabled(isChecked) }
        view!!.bassKnob.setOnKnobChangeListener(onBassKnobChangeListener)
        view!!.virtualizerKnob.setOnKnobChangeListener(onVirtualizerKnobChangeListener)
        view!!.pager.addOnPageChangeListener(onPageChangeListener)
        presenter.addEqualizerListener(this)

        view!!.band1.setLevel = onBandLevelChange
        view!!.band2.setLevel = onBandLevelChange
        view!!.band3.setLevel = onBandLevelChange
        view!!.band4.setLevel = onBandLevelChange
        view!!.band5.setLevel = onBandLevelChange
    }

    override fun onPause() {
        super.onPause()
        view!!.powerSwitch.setOnCheckedChangeListener(null)
        view!!.replayGainSwitch.setOnCheckedChangeListener(null)
        view!!.bassKnob.setOnKnobChangeListener(null)
        view!!.virtualizerKnob.setOnKnobChangeListener(null)
        view!!.pager.removeOnPageChangeListener(onPageChangeListener)
        presenter.removeEqualizerListener(this)

        view!!.band1.setLevel = null
        view!!.band2.setLevel = null
        view!!.band3.setLevel = null
        view!!.band4.setLevel = null
        view!!.band5.setLevel = null
    }

    private val onBassKnobChangeListener = object : RadialKnob.OnKnobChangeListener {
        override fun onValueChanged(knob: RadialKnob?, value: Int, fromUser: Boolean) {
            presenter.setBassStrength(value)
        }

        override fun onSwitchChanged(knob: RadialKnob?, on: Boolean): Boolean = false
    }

    private val onVirtualizerKnobChangeListener = object : RadialKnob.OnKnobChangeListener {
        override fun onValueChanged(knob: RadialKnob?, value: Int, fromUser: Boolean) {
            presenter.setVirtualizerStrength(value)
        }

        override fun onSwitchChanged(knob: RadialKnob?, on: Boolean): Boolean = false
    }

    private val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            presenter.setPreset(position % adapter.realCount)
        }
    }

    private val onBandLevelChange = { band: Int, level : Float -> presenter.setBandLevel(band, level) }

    override fun onPresetChange(band: Int, level: Float) {
        view!!.band1.onPresetChange(band, level)
        view!!.band2.onPresetChange(band, level)
        view!!.band3.onPresetChange(band, level)
        view!!.band4.onPresetChange(band, level)
        view!!.band5.onPresetChange(band, level)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}