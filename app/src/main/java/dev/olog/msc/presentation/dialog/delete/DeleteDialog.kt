package dev.olog.msc.presentation.dialog.delete

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

class DeleteDialog: BaseDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "${TAG}.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject @JvmField var listSize: Int = 0
    @Inject lateinit var mediaId: MediaId
    @Inject lateinit var presenter: DeleteDialogPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_delete)
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

        return when {
            mediaId.isAll || mediaId.isLeaf -> getString(R.string.delete_song_y, itemTitle)
            mediaId.isPlaylist -> getString(R.string.delete_playlist_y, itemTitle)
            else -> context!!.resources.getQuantityString(R.plurals.delete_xx_songs_from_y, listSize, listSize, itemTitle)
        }
    }

}