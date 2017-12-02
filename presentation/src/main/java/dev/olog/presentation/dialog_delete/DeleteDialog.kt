package dev.olog.presentation.dialog_delete

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.MediaIdHelper.MEDIA_ID_BY_ALL
import javax.inject.Inject

class DeleteDialog: BaseDialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: String, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject @JvmField var listSize: Int = 0
    @Inject lateinit var mediaId: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_delete)
                .setMessage(Html.fromHtml(createMessage()))
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_delete, { dialog, button ->
                    // todo
                })


        return builder.makeDialog()
    }

    private fun createMessage() : String {
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        if (MediaIdHelper.extractCategory(mediaId) == MEDIA_ID_BY_ALL){
            return getString(R.string.delete_song_y, itemTitle)
        }
        return context!!.resources.getQuantityString(R.plurals.delete_xx_songs_from_y, listSize, listSize, itemTitle)
    }

}