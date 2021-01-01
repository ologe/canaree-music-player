package dev.olog.feature.dialog.play.later

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseDialog
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.toast

@AndroidEntryPoint
class PlayLaterDialog : BaseDialog() {

    private val mediaId by argument(Params.MEDIA_ID, MediaId::fromString)
    private val title by argument<String>(Params.TITLE)
    private val listSize by argument<Int>(Params.SIZE)

    private val viewModel by viewModels<PlayLaterDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_play_later)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    private fun successMessage(): String {
        return when (mediaId) {
            is MediaId.Category -> resources.getQuantityString(R.plurals.xx_songs_added_to_play_later, listSize, listSize)
            is MediaId.Track -> getString(R.string.song_x_added_to_play_later, title)
        }
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    override fun positionButtonAction() {
        launch {
            var message: String
            try {
                val mediaController = MediaControllerCompat.getMediaController(requireActivity())
                viewModel.execute(mediaController)
                message = successMessage()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage()
            }
            requireActivity().toast(message)
            dismiss()
        }
    }

    private fun createMessage() : String {
        return when (mediaId) {
            is MediaId.Category -> resources.getQuantityString(R.plurals.add_xx_songs_to_play_later, listSize, listSize)
            // TODO add podcast localization
            is MediaId.Track -> getString(R.string.add_song_x_to_play_later, title)
        }
    }

}