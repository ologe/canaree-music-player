package dev.olog.feature.playlist.dialog.create

import android.content.Context
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.platform.fragment.BaseEditTextDialog
import dev.olog.platform.navigation.FragmentTagFactory
import dev.olog.shared.extension.argument
import dev.olog.shared.extension.toast
import dev.olog.shared.extension.withArguments

@AndroidEntryPoint
class NewPlaylistDialog : BaseEditTextDialog() {

    companion object {
        val TAG = FragmentTagFactory.create(NewPlaylistDialog::class)
        val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val viewModel by viewModels<NewPlaylistDialogViewModel>()

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val title by argument<String>(ARGUMENTS_ITEM_TITLE)
    private val listSize by argument<Int>(ARGUMENTS_LIST_SIZE)

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return super.extendBuilder(builder)
            .setTitle(localization.R.string.popup_new_playlist)
            .setPositiveButton(localization.R.string.popup_positive_create, null)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
    }

    override fun setupEditText(layout: TextInputLayout, editText: TextInputEditText) {
        editText.hint = getString(localization.R.string.popup_new_playlist)
    }

    override fun provideMessageForBlank(): String {
        return getString(localization.R.string.popup_playlist_name_not_valid)
    }

    override suspend fun onItemValid(string: String) {
        var message: String
        try {
            viewModel.execute(mediaId, string)
            message = successMessage(requireContext(), string).toString()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            message = getString(localization.R.string.popup_error_message)
        }
        toast(message)
    }


    private fun successMessage(context: Context, currentValue: String): CharSequence {
//        if (mediaId.isPlayingQueue){ todo
//            return context.getString(localization.R.string.queue_saved_as_playlist, currentValue)
//        }
        if (mediaId.isLeaf){
            return context.getString(localization.R.string.added_song_x_to_playlist_y, title, currentValue)
        }
        return context.resources.getQuantityString(
            localization.R.plurals.xx_songs_added_to_playlist_y,
                listSize, listSize, currentValue)
    }
}