package dev.olog.feature.dialog.playlist.rename

import android.content.Context
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseEditTextDialog
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.toast

@AndroidEntryPoint
class RenamePlaylistDialog : BaseEditTextDialog() {

    private val viewModel by viewModels<RenamePlaylistViewModel>()

    private val mediaId by argument(Params.MEDIA_ID, MediaId::fromString)
    private val itemTitle by argument<String>(Params.TITLE)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return super.extendBuilder(builder)
            .setTitle(R.string.popup_rename)
            .setPositiveButton(R.string.popup_positive_rename, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {
        editText.setText(itemTitle)
    }

    override fun provideMessageForBlank(): String {
        return when {
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> getString(R.string.popup_playlist_name_not_valid)
            else -> throw IllegalArgumentException("invalid media id category $mediaId")
        }
    }

    override suspend fun onItemValid(string: String) {
        var message: String
        try {
            viewModel.execute(string)
            message = successMessage(requireContext(), string)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            message = getString(R.string.popup_error_message)
        }
        requireActivity().toast(message)
    }

    private fun successMessage(context: Context, currentValue: String): String {
        return when {
            mediaId.isPlaylist || mediaId.isPodcastPlaylist -> context.getString(R.string.playlist_x_renamed_to_y, itemTitle, currentValue)
            else -> throw IllegalStateException("not a playlist, $mediaId")
        }
    }
}