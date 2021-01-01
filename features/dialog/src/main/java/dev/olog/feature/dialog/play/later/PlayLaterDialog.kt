package dev.olog.feature.dialog.play.later

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.text.parseAsHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseDialog
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import javax.inject.Inject

@AndroidEntryPoint
class PlayLaterDialog : BaseDialog() {

    companion object {
        const val TAG = "PlayLaterDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): PlayLaterDialog {
            return PlayLaterDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)
    private val title by argument<String>(ARGUMENTS_ITEM_TITLE)
    private val listSize by argument<Int>(ARGUMENTS_LIST_SIZE)

    @Inject lateinit var presenter: PlayLaterDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_play_later)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    private fun successMessage(): String {
        return if (mediaId.isLeaf){
            getString(R.string.song_x_added_to_play_later, title)
        } else {
            resources.getQuantityString(R.plurals.xx_songs_added_to_play_later, listSize, listSize)
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
                presenter.execute(mediaController, mediaId)
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
        if (mediaId.isSongs || mediaId.isLeaf){
            return getString(R.string.add_song_x_to_play_later, title)
        }
        return resources.getQuantityString(R.plurals.add_xx_songs_to_play_later, listSize, listSize)
    }

}