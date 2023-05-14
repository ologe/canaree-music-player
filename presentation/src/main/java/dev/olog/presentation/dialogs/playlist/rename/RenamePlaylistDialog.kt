package dev.olog.presentation.dialogs.playlist.rename

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.platform.extension.getArgument
import dev.olog.platform.extension.toast
import dev.olog.platform.extension.withArguments
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.EditTextDialogButton
import dev.olog.presentation.dialogs.createEditTextDialog
import dev.olog.presentation.dialogs.playlist.AlreadyExistingPlaylistValidator
import dev.olog.presentation.validation.ComposedValidator
import dev.olog.presentation.validation.NonEmptyValidator
import dev.olog.shared.lazyFast

@AndroidEntryPoint
class RenamePlaylistDialog : DialogFragment() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, itemTitle: String): RenamePlaylistDialog {
            return RenamePlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val viewModel by viewModels<RenamePlaylistDialogViewModel>()

    private val mediaId: MediaId by lazyFast {
        MediaId.fromString(getArgument(ARGUMENTS_MEDIA_ID))
    }
    private val itemTitle by lazyFast { getArgument<String>(ARGUMENTS_ITEM_TITLE) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createEditTextDialog(
            title = getString(R.string.popup_rename),
            positiveButton = EditTextDialogButton(R.string.popup_positive_rename) { text ->
                renamePlaylist(text)
                dismiss()
            },
            negativeButton = EditTextDialogButton(R.string.popup_negative_cancel) {
                dismiss()
            },
            validator = ComposedValidator(
                NonEmptyValidator(getString(R.string.popup_playlist_name_not_valid)),
                AlreadyExistingPlaylistValidator(
                    existingPlaylists = viewModel.getPlaylistTitles(),
                    message = getString(R.string.popup_playlist_name_already_exist)
                )
            ),
            setupEditText = {
                val text = requireArguments().getString(ARGUMENTS_ITEM_TITLE).orEmpty()
                setText(text)
                setSelection(text.length)
            },
        )
    }

    private suspend fun renamePlaylist(string: String) {
        val message = try {
            viewModel.execute(mediaId, string)
            successMessage(string)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            getString(R.string.popup_error_message)
        }
        requireActivity().toast(message)
    }

    private fun successMessage(currentValue: String): String {
        return getString(R.string.playlist_x_renamed_to_y, itemTitle, currentValue)
    }
}

