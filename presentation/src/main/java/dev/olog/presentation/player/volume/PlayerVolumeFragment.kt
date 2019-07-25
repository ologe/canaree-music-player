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
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.withArguments
import kotlinx.android.synthetic.main.player_volume.*
import javax.inject.Inject

class PlayerVolumeFragment : DaggerFragment(), DrawsOnTop, SeekBar.OnSeekBarChangeListener {

    companion object {
        val TAG = PlayerVolumeFragment::class.java.name
        private val ARGUMENT_LAYOUT_ID = "$TAG.argument.layoutid"

        @JvmStatic
        fun newInstance(layoutId: Int): PlayerVolumeFragment {
            return PlayerVolumeFragment().withArguments(
                ARGUMENT_LAYOUT_ID to layoutId
            )
        }
    }

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = arguments?.getInt(ARGUMENT_LAYOUT_ID) ?: R.layout.player_volume_no_background
        return inflater.inflate(layoutId, container, false)
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
        if (fromUser) {
            musicPrefs.setVolume(progress)
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
    }
}