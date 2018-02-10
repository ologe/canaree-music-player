package dev.olog.msc.presentation.dialog.clear.playlist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialogFragment
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asHtml
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.withArguments
import javax.inject.Inject

class ClearPlaylistDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "ClearPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "${TAG}.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): ClearPlaylistDialog {
            return ClearPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject @JvmField var listSize: Int = 0
    @Inject lateinit var presenter: ClearPlaylistDialogPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_clear_playlist)
                .setMessage(createMessage().asHtml())
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_delete, { _, _ ->
                    presenter.execute()
                            .subscribe({}, Throwable::printStackTrace)
                })


        return builder.makeDialog()
    }

    private fun createMessage() : String {
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        return context!!.resources.getQuantityString(R.plurals.remove_xx_songs_from_playlist_y, listSize, listSize, itemTitle)
    }

}