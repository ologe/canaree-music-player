package dev.olog.feature.dialog.ringtone

import android.provider.MediaStore
import androidx.core.text.parseAsHtml
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseDialog
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.toast

@AndroidEntryPoint
class SetRingtoneDialog : BaseDialog() {

    private val viewModel by viewModels<SetRingtoneDialogViewModel>()

    private val itemTitle by argument<String>(Params.TITLE)
    private val itemArtist by argument<String>(Params.ARTIST)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_set_as_ringtone)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction() {
        launch {
            var message: String
            try {
                viewModel.execute(requireActivity())
                message = successMessage()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage()
            }
            requireActivity().toast(message)
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
        if (itemArtist != MediaStore.UNKNOWN_STRING){
            return "$itemTitle $itemArtist"

        }
        return itemTitle
    }


}