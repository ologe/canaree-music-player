package dev.olog.feature.dialog.playlist.create

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
class CreatePlaylistDialog : BaseEditTextDialog() {

    private val viewModel by viewModels<CreatePlaylistDialogViewModel>()

    private val mediaId: MediaId by argument(Params.MEDIA_ID, MediaId::fromString)
    private val title by argument<String>(Params.TITLE)
    private val listSize by argument<Int>(Params.SIZE)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return super.extendBuilder(builder)
            .setTitle(R.string.popup_new_playlist)
            .setPositiveButton(R.string.popup_positive_create, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {
        editText.hint = getString(R.string.popup_new_playlist)
    }

    override fun provideMessageForBlank(): String {
        return getString(R.string.popup_playlist_name_not_valid)
    }

    override suspend fun onItemValid(string: String) {
        var message: String
        try {
            viewModel.execute(string)
            message = successMessage(requireActivity(), string).toString()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            message = getString(R.string.popup_error_message)
        }
        requireActivity().toast(message)
    }


    private fun successMessage(context: Context, currentValue: String): CharSequence {
//        if (mediaId.isPlayingQueue){ TODO
//            return context.getString(R.string.queue_saved_as_playlist, currentValue)
//        }
        return when (mediaId) {
            is MediaId.Category -> context.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y,
                listSize, listSize, currentValue)
            is MediaId.Track -> context.getString(R.string.added_song_x_to_playlist_y, title, currentValue)
        }
    }
}