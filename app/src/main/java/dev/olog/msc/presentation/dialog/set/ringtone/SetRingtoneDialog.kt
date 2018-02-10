package dev.olog.msc.presentation.dialog.set.ringtone

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

class SetRingtoneDialog : BaseDialogFragment() {

    companion object {
        const val TAG = "SetRingtoneDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}.arguments.item_title"

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