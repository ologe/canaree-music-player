package dev.olog.msc.presentation.dialog.rename

import android.content.Context
import dev.olog.core.MediaId
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseEditTextDialog
import dev.olog.shared.android.extensions.withArguments
import javax.inject.Inject

class RenameDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, itemTitle: String): RenameDialog {
            return RenameDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: RenameDialogPresenter
    @Inject lateinit var mediaId: MediaId
    @Inject lateinit var title: String

    override fun title(): Int = R.string.popup_rename

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.popup_negative_cancel
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.popup_positive_rename
    }

    override fun errorMessageForBlankForm(): Int {
        return when {
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> R.string.popup_playlist_name_not_valid
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

    override fun errorMessageForInvalidForm(currentValue: String): Int {
        return when {
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> R.string.popup_playlist_name_already_exist
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

    override fun positiveAction(currentValue: String) {
        return presenter.execute(currentValue)
    }

    override fun successMessage(context: Context, currentValue: String): CharSequence {
        return when {
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> context.getString(R.string.playlist_x_renamed_to_y, title, currentValue)
            else -> throw IllegalStateException("not a playlist, $mediaId")
        }
    }

    override fun negativeMessage(context: Context, currentValue: String): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun isStringValid(string: String): Boolean = presenter.checkData(string)

    override fun initialTextFieldValue(): String {
        return arguments!!.getString(ARGUMENTS_ITEM_TITLE)!!
    }
}