package dev.olog.presentation.dialogs.play.later

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launchWhenResumed
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments

@AndroidEntryPoint
class PlayLaterDialog : BaseDialog() {

    companion object {
        const val TAG = "PlayLaterDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): PlayLaterDialog {
            return PlayLaterDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val title by argument<String>(ARGUMENTS_ITEM_TITLE)
    private val listSize by argument<Int>(ARGUMENTS_LIST_SIZE)

    private val viewModel by viewModels<PlayLaterDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_play_later)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    private fun successMessage(context: Context): String {
        return if (mediaId.isLeaf){
            context.getString(R.string.song_x_added_to_play_later, title)
        } else context.resources.getQuantityString(R.plurals.xx_songs_added_to_play_later, listSize, listSize)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            var message: String
            try {
                val mediaController = MediaControllerCompat.getMediaController(requireActivity())
                viewModel.execute(mediaController, mediaId)
                message = successMessage(requireContext())
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(requireContext())
            }
            toast(message)
            dismiss()
        }
    }

    private fun createMessage() : String {
        if (mediaId.isAll || mediaId.isLeaf){
            return getString(R.string.add_song_x_to_play_later, title)
        }
        return context!!.resources.getQuantityString(R.plurals.add_xx_songs_to_play_later, listSize, listSize)
    }

}