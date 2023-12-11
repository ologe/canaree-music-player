package dev.olog.presentation.player.volume

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.withArguments
import javax.inject.Inject

@AndroidEntryPoint
class PlayerVolumeFragment : Fragment(), DrawsOnTop, SeekBar.OnSeekBarChangeListener {

    companion object {
        @JvmStatic
        val TAG = PlayerVolumeFragment::class.java.name
        @JvmStatic
        private val ARGUMENT_LAYOUT_ID = "$TAG.argument.layoutid"
        private val ARGUMENT_Y_POSITION = "$TAG.argument.y_position"

        @JvmStatic
        fun newInstance(layoutId: Int, yPosition: Float = -1f): PlayerVolumeFragment {
            return PlayerVolumeFragment().withArguments(
                ARGUMENT_LAYOUT_ID to layoutId,
                ARGUMENT_Y_POSITION to yPosition
            )
        }
    }

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway

    private lateinit var volumeSlider: SeekBar
    private lateinit var card: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = arguments?.getInt(ARGUMENT_LAYOUT_ID) ?: R.layout.player_volume_no_background
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        volumeSlider = view.findViewById(R.id.volumeSlider)
        card = view.findViewById(R.id.card)

        volumeSlider.max = 100
        volumeSlider.progress = musicPrefs.getVolume()

        val yPosition = arguments?.getFloat(ARGUMENT_Y_POSITION, -1f) ?: -1f
        if (yPosition > -1){
            card.translationY = yPosition
        }
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