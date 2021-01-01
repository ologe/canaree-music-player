package dev.olog.feature.dialog.playlist.clear

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
class ClearPlaylistDialog : BaseDialog() {

    private val title by argument<String>(Params.TITLE)

    private val viewModel by viewModels<ClearPlaylistDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_clear_playlist)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_delete, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
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
        return getString(R.string.playlist_x_cleared, title)
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return getString(R.string.remove_songs_from_playlist_y, title)
    }

}