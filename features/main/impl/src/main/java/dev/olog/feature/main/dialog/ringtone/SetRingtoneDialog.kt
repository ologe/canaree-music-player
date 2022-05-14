package dev.olog.feature.main.dialog.ringtone

import android.content.Context
import android.provider.MediaStore
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.platform.fragment.BaseDialog
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments

@AndroidEntryPoint
class SetRingtoneDialog : BaseDialog() {

    companion object {
        const val TAG = "SetRingtoneDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_TITLE = "$TAG.arguments.title"
        const val ARGUMENTS_ARTIST = "$TAG.arguments.artist"

        @JvmStatic
        fun newInstance(mediaId: MediaId, title: String, artist: String): SetRingtoneDialog {
            return SetRingtoneDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_TITLE to title,
                    ARGUMENTS_ARTIST to artist
            )
        }
    }

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val title by argument<String>(ARGUMENTS_TITLE)
    private val artist by argument<String>(ARGUMENTS_ARTIST)
    private val viewModel by viewModels<SetRingtoneDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(localization.R.string.popup_set_as_ringtone)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(localization.R.string.popup_positive_ok, null)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            var message: String
            try {
                viewModel.execute(requireActivity(), mediaId)
                message = successMessage(requireContext())
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(requireContext())
            }
            toast(message)
            dismiss()

        }
    }

    private fun successMessage(context: Context): String {
        val title = generateItemDescription()
        return context.getString(localization.R.string.song_x_set_as_ringtone, title)
    }

    private fun failMessage(context: Context): String {
        return context.getString(localization.R.string.popup_error_message)
    }

    private fun createMessage() : String{
        val title = generateItemDescription()
        return context!!.getString(localization.R.string.song_x_will_be_set_as_ringtone, title)
    }

    private fun generateItemDescription(): String{
        var title = this.title
        val artist = this.artist
        if (artist != MediaStore.UNKNOWN_STRING){
            title += " $artist"
        }
        return title
    }


}