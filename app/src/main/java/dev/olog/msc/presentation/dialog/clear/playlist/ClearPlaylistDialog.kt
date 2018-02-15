package dev.olog.msc.presentation.dialog.clear.playlist

import android.content.Context
import android.content.DialogInterface
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialog
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asHtml
import dev.olog.msc.utils.k.extension.withArguments
import io.reactivex.Completable
import javax.inject.Inject

class ClearPlaylistDialog : BaseDialog() {

    companion object {
        const val TAG = "ClearPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, itemTitle: String): ClearPlaylistDialog {
            return ClearPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var title: String
    @Inject lateinit var presenter: ClearPlaylistDialogPresenter

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_clear_playlist)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.popup_negative_cancel
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.popup_positive_delete
    }

    override fun successMessage(context: Context): CharSequence {
        return context.getString(R.string.playlist_x_cleared, title)
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
        return presenter.execute()
    }

    private fun createMessage() : String {
        return context!!.getString(R.string.remove_songs_from_playlist_y, title)
    }

}