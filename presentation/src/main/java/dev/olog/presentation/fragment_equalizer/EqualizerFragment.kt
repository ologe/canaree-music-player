package dev.olog.presentation.fragment_equalizer

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.os.Bundle
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.widgets.vertical_seek_bar.VerticalSeekBar
import kotlinx.android.synthetic.main.fragment_equalizer.view.*

class EqualizerFragment : BaseFragment() {

    private val audioSessionId = 1

    private val equalizer by lazy { Equalizer(0, audioSessionId) }
    private val virtualizer by lazy { Virtualizer(0, audioSessionId) }
    private val bassBoost by lazy { BassBoost(0, audioSessionId) }
    private val presetRever by lazy { PresetReverb(0, audioSessionId) }

    private val lowerEqLevel by lazy { equalizer.bandLevelRange[0] }
    private val upperEqLevel by lazy { equalizer.bandLevelRange[1] }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {

        view.bar1.max = upperEqLevel - lowerEqLevel
        view.bar1.progress = equalizer.getBandLevel(0).toInt()

        view.bar2.max = upperEqLevel - lowerEqLevel
        view.bar2.progress = equalizer.getBandLevel(1).toInt()

        view.bar3.max = upperEqLevel - lowerEqLevel
        view.bar3.progress = equalizer.getBandLevel(2).toInt()

        view.bar4.max = upperEqLevel - lowerEqLevel
        view.bar4.progress = equalizer.getBandLevel(3).toInt()

        view.bar5.max = upperEqLevel - lowerEqLevel
        view.bar5.progress = equalizer.getBandLevel(4).toInt()
    }

    override fun onResume() {
        super.onResume()
        view!!.bar1.setOnSeekBarChangeListener(object : SeekbarWrapper{
            override fun onProgressChanged(seekBar: VerticalSeekBar?, progress: Int, fromUser: Boolean) {
                equalizer.setBandLevel(0, ((progress + lowerEqLevel).toShort()))
                println(progress)
            }
        })
        view!!.bar2.setOnSeekBarChangeListener(object : SeekbarWrapper{
            override fun onProgressChanged(seekBar: VerticalSeekBar?, progress: Int, fromUser: Boolean) {
                equalizer.setBandLevel(1, ((progress + lowerEqLevel).toShort()))
                println(progress)
            }
        })
        view!!.bar3.setOnSeekBarChangeListener(object : SeekbarWrapper{
            override fun onProgressChanged(seekBar: VerticalSeekBar?, progress: Int, fromUser: Boolean) {
                equalizer.setBandLevel(2, ((progress + lowerEqLevel).toShort()))
                println(progress)
            }
        })
        view!!.bar4.setOnSeekBarChangeListener(object : SeekbarWrapper{
            override fun onProgressChanged(seekBar: VerticalSeekBar?, progress: Int, fromUser: Boolean) {
                equalizer.setBandLevel(3, ((progress + lowerEqLevel).toShort()))
                println(progress)
            }
        })
        view!!.bar5.setOnSeekBarChangeListener(object : SeekbarWrapper{
            override fun onProgressChanged(seekBar: VerticalSeekBar?, progress: Int, fromUser: Boolean) {
                equalizer.setBandLevel(4, ((progress + lowerEqLevel).toShort()))
                println(progress)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        view!!.bar1.setOnSeekBarChangeListener(null)
        view!!.bar2.setOnSeekBarChangeListener(null)
        view!!.bar3.setOnSeekBarChangeListener(null)
        view!!.bar4.setOnSeekBarChangeListener(null)
        view!!.bar5.setOnSeekBarChangeListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_equalizer

    interface SeekbarWrapper: VerticalSeekBar.OnSeekBarChangeListener{
        override fun onStartTrackingTouch(seekBar: VerticalSeekBar?) {}
        override fun onStopTrackingTouch(seekBar: VerticalSeekBar?) {}
    }

}