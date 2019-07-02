package dev.olog.presentation.player.volume

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import dagger.android.support.DaggerFragment
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.shared.extensions.act
import kotlinx.android.synthetic.main.player_volume.*
import javax.inject.Inject

class PlayerVolumeFragment : DaggerFragment(), DrawsOnTop, SeekBar.OnSeekBarChangeListener {

    companion object {
        val TAG = PlayerVolumeFragment::class.java.name
    }

    @Inject lateinit var musicPrefs: MusicPreferencesGateway

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_volume, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        volumeSlider.max = 100
        volumeSlider.progress = musicPrefs.getVolume()
    }

    override fun onResume() {
        super.onResume()
        view?.setOnClickListener { act.onBackPressed() }
        volumeSlider.setOnSeekBarChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        view?.setOnClickListener(null)
        volumeSlider.setOnSeekBarChangeListener(null)
    }

    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser){
            musicPrefs.setVolume(progress)
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
    }
}