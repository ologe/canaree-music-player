package dev.olog.feature.player.volume

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.player.R
import dev.olog.platform.DrawsOnTop
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.shared.extension.withArguments
import kotlinx.android.synthetic.main.player_volume.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayerVolumeFragment : Fragment(), DrawsOnTop, SeekBar.OnSeekBarChangeListener {

    companion object {
        val TAG = FragmentTagFactory.create(PlayerVolumeFragment::class)
        private val ARGUMENT_LAYOUT_ID = "$TAG.argument.layoutid"
        private val ARGUMENT_Y_POSITION = "$TAG.argument.y_position"

        fun newInstance(layoutId: Int, yPosition: Float = -1f): PlayerVolumeFragment {
            return PlayerVolumeFragment().withArguments(
                ARGUMENT_LAYOUT_ID to layoutId,
                ARGUMENT_Y_POSITION to yPosition
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

        val yPosition = arguments?.getFloat(ARGUMENT_Y_POSITION, -1f) ?: -1f
        if (yPosition > -1){
            card.translationY = yPosition
        }
    }

    override fun onResume() {
        super.onResume()
        view?.setOnClickListener { requireActivity().onBackPressed() }
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