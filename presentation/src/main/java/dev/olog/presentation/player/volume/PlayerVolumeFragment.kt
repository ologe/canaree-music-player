package dev.olog.presentation.player.volume

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.databinding.PlayerVolumeBinding
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.viewBinding
import javax.inject.Inject

@AndroidEntryPoint
class PlayerVolumeFragment : Fragment(R.layout.player_volume), DrawsOnTop, SeekBar.OnSeekBarChangeListener {

    companion object {
        val TAG = PlayerVolumeFragment::class.java.name
        private val ARGUMENT_Y_POSITION = "$TAG.argument.y_position"

        @JvmStatic
        fun newInstance(yPosition: Float = -1f): PlayerVolumeFragment {
            return PlayerVolumeFragment().withArguments(
                ARGUMENT_Y_POSITION to yPosition
            )
        }
    }

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway

    private val binding by viewBinding(PlayerVolumeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.volumeSlider.max = 100
        binding.volumeSlider.progress = musicPrefs.getVolume()

        val yPosition = arguments?.getFloat(ARGUMENT_Y_POSITION, -1f) ?: -1f
        if (yPosition > -1){
            binding.card.translationY = yPosition
        }
    }

    override fun onResume() {
        super.onResume()
        view?.setOnClickListener { act.onBackPressed() }
        binding.volumeSlider.setOnSeekBarChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        view?.setOnClickListener(null)
        binding.volumeSlider.setOnSeekBarChangeListener(null)
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