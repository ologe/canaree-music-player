package dev.olog.feature.playlist.dialog.clear

import android.content.Context
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.platform.fragment.BaseDialog
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments

@AndroidEntryPoint
class ClearPlaylistDialog : BaseDialog() {

    companion object {
        val TAG = FragmentTagFactory.create(ClearPlaylistDialog::class)
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, itemTitle: String): ClearPlaylistDialog {
            return ClearPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val title by argument<String>(ARGUMENTS_ITEM_TITLE)

    private val viewModel by viewModels<ClearPlaylistDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(localization.R.string.popup_clear_playlist)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(localization.R.string.popup_positive_delete, null)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            var message: String
            try {
                viewModel.execute(mediaId)
                message = successMessage(requireContext())
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(requireContext())
            }
            toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        return context.getString(localization.R.string.playlist_x_cleared, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(localization.R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return context!!.getString(localization.R.string.remove_songs_from_playlist_y, title)
    }

}