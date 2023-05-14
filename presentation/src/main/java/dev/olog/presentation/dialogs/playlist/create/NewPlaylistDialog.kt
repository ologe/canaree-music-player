package dev.olog.presentation.dialogs.playlist.create

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.platform.extension.getArgument
import dev.olog.platform.extension.toast
import dev.olog.platform.extension.withArguments
import dev.olog.presentation.dialogs.EditTextDialogButton
import dev.olog.presentation.dialogs.createEditTextDialog
import dev.olog.presentation.dialogs.playlist.AlreadyExistingPlaylistValidator
import dev.olog.presentation.validation.ComposedValidator
import dev.olog.presentation.validation.NonEmptyValidator
import kotlinx.android.parcel.Parcelize

@AndroidEntryPoint
class NewPlaylistDialog : DialogFragment() {

    sealed interface NavArgs : Parcelable {
        @Parcelize
        data class FromIds(val ids: List<Long>): NavArgs
        @Parcelize
        data class FromMediaId(val mediaId: MediaId, val title: String): NavArgs
    }

    companion object {
        const val TAG = "NewPlaylistDialog"
        private const val ARGUMENTS = "$TAG.arguments"

        fun newInstance(navArgs: NavArgs): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                ARGUMENTS to navArgs,
            )
        }

    }

    private val navArgs: NavArgs
        get() = getArgument(ARGUMENTS)

    private val viewModel by viewModels<NewPlaylistDialogViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createEditTextDialog(
            title = getString(R.string.popup_new_playlist),
            positiveButton = EditTextDialogButton(R.string.popup_positive_create) {
                createPlaylist(it)
                dismiss()
            },
            negativeButton = EditTextDialogButton(R.string.popup_negative_cancel) {
                dismiss()
            },
            validator = ComposedValidator(
                NonEmptyValidator(getString(R.string.popup_playlist_name_not_valid)),
                AlreadyExistingPlaylistValidator(
                    existingPlaylists = viewModel.getPlaylistTitles(),
                    message = getString(R.string.popup_playlist_name_already_exist),
                )
            ),
            setupEditText = {
                hint = getString(R.string.popup_new_playlist)
            }
        )
    }

    private suspend fun createPlaylist(title: String) {
        val message = try {
            val insertedItems = viewModel.execute(title, navArgs)
            successMessage(title, insertedItems!!)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            getString(R.string.popup_error_message)
        }
        requireActivity().toast(message)
    }


    private fun successMessage(
        playlistTitle: String,
        insertedItems: Int,
    ): String = when (val args = navArgs) {
        is NavArgs.FromIds -> {
            resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, insertedItems, insertedItems, playlistTitle)
        }
        is NavArgs.FromMediaId -> {
            val mediaId = args.mediaId
            when (mediaId.category) {
                MediaIdCategory.PLAYING_QUEUE -> getString(R.string.queue_saved_as_playlist, playlistTitle)
                MediaIdCategory.SONGS -> getString(R.string.added_song_x_to_playlist_y, args.title, playlistTitle)
                else -> resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, insertedItems, insertedItems, playlistTitle)
            }
        }
    }
}