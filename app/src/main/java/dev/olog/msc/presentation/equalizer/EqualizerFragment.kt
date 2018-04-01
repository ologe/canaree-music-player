package dev.olog.msc.presentation.equalizer

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.view.View
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import dev.olog.msc.R
import dev.olog.msc.interfaces.equalizer.IEqualizer
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.equalizer.widget.RadialKnob
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_equalizer.*
import kotlinx.android.synthetic.main.fragment_equalizer.view.*
import javax.inject.Inject

class EqualizerFragment : BaseFragment(), IEqualizer.Listener {

    companion object {
        const val TAG = "EqualizerFragment"
    }

    @Inject lateinit var presenter: EqualizerFragmentPresenter
    private lateinit var adapter : PresetPagerAdapter
    private var snackBar: Snackbar? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter.isEqualizerAvailable()
                .asLiveData()
                .subscribe(this, { isEqAvailable ->
                    if (snackBar != null){
                        if (isEqAvailable){
                            snackBar?.dismiss()
                        } // else, already shown
                    } else {
                        // error snackBar now shown
                        if (!isEqAvailable){
                            snackBar = Snackbar.make(root, R.string.equalizer_error, Snackbar.LENGTH_INDEFINITE)
                            snackBar!!.show()
                        }
                    }
                })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        adapter = PresetPagerAdapter(act.supportFragmentManager, presenter.getPresets())

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

        RxCompoundButton.checkedChanges(view.powerSwitch)
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this) { isChecked ->
                    val text = if(isChecked) R.string.common_switch_on else R.string.common_switch_off
                    view.powerSwitch.text = getString(text)
                    presenter.setEqualizerEnabled(isChecked)
                }
    }

    override fun onResume() {
        super.onResume()
        replayGainSwitch.setOnCheckedChangeListener { _, isChecked -> presenter.setReplayGainEnabled(isChecked) }
        bassKnob.setOnKnobChangeListener(onBassKnobChangeListener)
        virtualizerKnob.setOnKnobChangeListener(onVirtualizerKnobChangeListener)
        pager.addOnPageChangeListener(onPageChangeListener)
        presenter.addEqualizerListener(this)

        band1.setLevel = onBandLevelChange
        band2.setLevel = onBandLevelChange
        band3.setLevel = onBandLevelChange
        band4.setLevel = onBandLevelChange
        band5.setLevel = onBandLevelChange

        back.setOnClickListener { act.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        replayGainSwitch.setOnCheckedChangeListener(null)
        bassKnob.setOnKnobChangeListener(null)
        virtualizerKnob.setOnKnobChangeListener(null)
        pager.removeOnPageChangeListener(onPageChangeListener)
        presenter.removeEqualizerListener(this)

        band1.setLevel = null
        band2.setLevel = null
        band3.setLevel = null
        band4.setLevel = null
        band5.setLevel = null

        back.setOnClickListener(null)
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
            presenter.setPreset(position % adapter.count)
        }
    }

    private val onBandLevelChange = { band: Int, level : Float -> presenter.setBandLevel(band, level) }

    override fun onPresetChange(band: Int, level: Float) {
        band1.onPresetChange(band, level)
        band2.onPresetChange(band, level)
        band3.onPresetChange(band, level)
        band4.onPresetChange(band, level)
        band5.onPresetChange(band, level)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer
}