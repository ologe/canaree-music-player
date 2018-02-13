package dev.olog.msc.presentation.dialog.set.ringtone

import android.content.Context
import android.content.DialogInterface
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseDialog
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asHtml
import dev.olog.msc.utils.k.extension.withArguments
import io.reactivex.Completable
import javax.inject.Inject

class SetRingtoneDialog : BaseDialog() {

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
    @Inject lateinit var title: String

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_set_as_ringtone)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.popup_negative_cancel
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.popup_positive_ok
    }

    override fun successMessage(context: Context): CharSequence {
        return context.getString(R.string.song_x_set_as_ringtone, title)
    }

    override fun failMessage(context: Context): CharSequence {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int): Completable {
        return presenter.execute()
    }

    private fun createMessage() : String{
        val itemTitle = arguments!!.getString(ARGUMENTS_ITEM_TITLE)
        return context!!.getString(R.string.song_x_will_be_set_as_ringtone, itemTitle)
    }


}