package dev.olog.presentation.player.volume

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.feature.base.DrawsOnTop
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.withArguments
import kotlinx.android.synthetic.main.player_volume.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayerVolumeFragment : Fragment(), DrawsOnTop, SeekBar.OnSeekBarChangeListener {

    companion object {
        val TAG = PlayerVolumeFragment::class.java.name
        private val ARGUMENT_LAYOUT_ID = "$TAG.argument.layoutid"
        private val ARGUMENT_Y_POSITION = "$TAG.argument.y_position"

        fun newInstance(@LayoutRes layoutId: Int, yPosition: Float = -1f): PlayerVolumeFragment {
            return PlayerVolumeFragment().withArguments(
                ARGUMENT_LAYOUT_ID to layoutId,
                ARGUMENT_Y_POSITION to yPosition
            )
        }
    }

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway

    private val layoutId by argument<Int>(ARGUMENT_LAYOUT_ID)
    private val yPosition by argument<Float>(ARGUMENT_Y_POSITION)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        volumeSlider.max = 100
        volumeSlider.progress = musicPrefs.volume

        if (yPosition > -1){
            card.translationY = yPosition
        }
    }

    override fun onResume() {
        super.onResume()
        requireView().setOnClickListener {
            requireActivity().onBackPressed()
        }
        volumeSlider.setOnSeekBarChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        requireView().setOnClickListener(null)
        volumeSlider.setOnSeekBarChangeListener(null)
    }

    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            musicPrefs.volume = progress
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
    }
}