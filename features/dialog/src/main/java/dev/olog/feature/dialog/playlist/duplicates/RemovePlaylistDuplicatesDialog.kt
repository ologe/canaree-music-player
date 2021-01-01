package dev.olog.feature.dialog.playlist.duplicates

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
class RemovePlaylistDuplicatesDialog: BaseDialog() {

    private val viewModel by viewModels<RemovePlaylistDuplicatesDialogViewModel>()

    private val itemTitle by argument<String>(Params.TITLE)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.remove_duplicates_title)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_remove, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction() {
        launch {
            var message: String
            try {
                viewModel.execute()
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
        return getString(R.string.remove_duplicates_success, itemTitle)
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return getString(R.string.remove_duplicates_message, itemTitle)
    }

}