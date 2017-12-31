package dev.olog.presentation.dialog_set_ringtone

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseDialogFragment
import dev.olog.presentation.utils.extension.asHtml
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.presentation.utils.extension.withArguments
import dev.olog.shared.MediaId
import javax.inject.Inject

class SetRingtoneDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "SetRingtoneDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, itemTitle: String): SetRingtoneDialog {
            return SetRingtoneDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle)
        }
    }

    @Inject lateinit var presenter: SetRingtoneDialogPresenter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.popup_set_as_ringtone)
                .setMessage(createMessage().asHtml())
                .setNegativeButton(R.string.popup_negative_cancel, null)
                .setPositiveButton(R.string.popup_positive_ok, { _, _ -> presenter.execute() })

        return builder.makeDialog()
    }

    private fun createMessage() : String{
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        return context!!.getString(R.string.song_x_will_be_set_as_ringtone, itemTitle)
    }


}