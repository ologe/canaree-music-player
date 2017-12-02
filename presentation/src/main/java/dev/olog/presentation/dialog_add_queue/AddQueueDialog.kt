package dev.olog.presentation.dialog_add_queue

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared.MediaIdHelper
import org.jetbrains.anko.toast
import javax.inject.Inject

class AddQueueDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "AddQueueDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: String, listSize: Int, itemTitle: String): AddQueueDialog {
            return AddQueueDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var mediaId: String
    @Inject @JvmField var listSize: Int = 0
    @Inject lateinit var presenter: AddQueueDialogPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.popup_add_to_queue)
                .setMessage(Html.fromHtml(createMessage()))
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    presenter.execute(activity!!)
                            .doOnSuccess { createSuccessMessage(it) }
                            .doOnError { createErrorMessage() }
                            .subscribe({}, Throwable::printStackTrace)
                    this.dismiss()
                })

        return builder.makeDialog()
    }

    private fun createSuccessMessage(string: String){
        val message = if (TextUtils.isDigitsOnly(string)){
            val size = string.toInt()
            context!!.resources.getQuantityString(R.plurals.added_xx_songs_to_queue, size, size)
        } else {
            context!!.getString(R.string.added_song_x_to_queue, string)
        }
        context!!.toast(message)
    }

    private fun createErrorMessage(){
        context!!.toast(context!!.getString(R.string.popup_error_message))
    }

    private fun createMessage() : String {
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        if (MediaIdHelper.extractCategory(mediaId) == MediaIdHelper.MEDIA_ID_BY_ALL){
            return getString(R.string.add_song_x_to_queue, itemTitle)
        }
        return context!!.resources.getQuantityString(R.plurals.add_xx_songs_to_queue, listSize, listSize)
    }

}