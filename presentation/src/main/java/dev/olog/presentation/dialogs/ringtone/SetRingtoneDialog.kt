package dev.olog.presentation.dialogs.ringtone

import android.content.Context
import android.provider.MediaStore
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.platform.extension.act
import dev.olog.platform.extension.toast
import dev.olog.platform.extension.withArguments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetRingtoneDialog : BaseDialog() {

    companion object {
        const val TAG = "SetRingtoneDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_TITLE = "$TAG.arguments.title"
        const val ARGUMENTS_ARTIST = "$TAG.arguments.artist"

        fun newInstance(mediaId: MediaId, title: String, artist: String): SetRingtoneDialog {
            return SetRingtoneDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_TITLE to title,
                    ARGUMENTS_ARTIST to artist
            )
        }
    }

    private val viewModel by viewModels<SetRingtoneDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_set_as_ringtone)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        // TODO refactor
        GlobalScope.launch(Dispatchers.Main) {
            var message: String
            try {
                val mediaId = MediaId.fromString(arguments!!.getString(ARGUMENTS_MEDIA_ID)!!)
                viewModel.execute(act, mediaId)
                message = successMessage(act)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(act)
            }
            act.toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        val title = generateItemDescription()
        return context.getString(R.string.song_x_set_as_ringtone, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String{
        val title = generateItemDescription()
        return context!!.getString(R.string.song_x_will_be_set_as_ringtone, title)
    }

    private fun generateItemDescription(): String{
        var title = arguments!!.getString(ARGUMENTS_TITLE)!!
        val artist = arguments!!.getString(ARGUMENTS_ARTIST)
        if (artist != MediaStore.UNKNOWN_STRING){
            title += " $artist"
        }
        return title
    }


}