package dev.olog.feature.player.volume

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.DrawsOnTop
import dev.olog.feature.player.R
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.withArguments
import kotlinx.android.synthetic.main.player_volume.*

// TODO fix click on background going through
@AndroidEntryPoint
internal class PlayerVolumeFragment : Fragment(R.layout.player_volume),
    DrawsOnTop,
    SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val ARGUMENT_Y_POSITION = "y_position"

        fun newInstance(yPosition: Float): PlayerVolumeFragment {
            return PlayerVolumeFragment().withArguments(
                ARGUMENT_Y_POSITION to yPosition
            )
        }
    }

    private val viewModel by viewModels<PlayerVolumeFragmentViewModel>()

    private val yPosition by argument<Float>(ARGUMENT_Y_POSITION)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        volumeSlider.max = 100
        volumeSlider.progress = viewModel.volume

        card.translationY = yPosition
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
            viewModel.volume = progress
        }
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
    }
}