package dev.olog.presentation.dialogs.ringtone

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.MediaId
import dev.olog.intents.AppConstants
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.*
import timber.log.Timber
import javax.inject.Inject

class SetRingtoneDialog : BaseDialog() {

    companion object {
        const val TAG = "SetRingtoneDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"
        const val ARGUMENTS_TITLE = "${TAG}_arguments_title"
        const val ARGUMENTS_ARTIST = "${TAG}_arguments_artist"

        @JvmStatic
        fun newInstance(mediaId: MediaId, title: String, artist: String): SetRingtoneDialog {
            return SetRingtoneDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_TITLE to title,
                    ARGUMENTS_ARTIST to artist
            )
        }
    }

    @Inject lateinit var presenter: SetRingtoneDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_set_as_ringtone)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            var message: String
            try {
                val mediaId = MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
                presenter.execute(act, mediaId)
                message = successMessage()
            } catch (ex: Exception) {
                Timber.e(ex)
                message = failMessage()
            }
            act.toast(message)
            dismiss()

        }
    }

    private fun successMessage(): String {
        val title = generateItemDescription()
        return getString(R.string.song_x_set_as_ringtone, title)
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    private fun createMessage() : String{
        val title = generateItemDescription()
        return getString(R.string.song_x_will_be_set_as_ringtone, title)
    }

    private fun generateItemDescription(): String{
        var title = getArgument<String>(ARGUMENTS_TITLE)
        val artist = getArgument<String>(ARGUMENTS_ARTIST)
        if (artist != AppConstants.UNKNOWN){
            title += " $artist"
        }
        return title
    }


}