package dev.olog.presentation.dialogs.playlist.create

import android.content.Context
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseEditTextDialog
import dev.olog.platform.extension.act
import dev.olog.platform.extension.toast
import dev.olog.platform.extension.withArguments
import dev.olog.shared.lazyFast

@AndroidEntryPoint
class NewPlaylistDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "NewPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val viewModel by viewModels<NewPlaylistDialogViewModel>()

    private val mediaId: MediaId by lazyFast {
        val mediaId = arguments!!.getString(ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { arguments!!.getString(ARGUMENTS_ITEM_TITLE)!! }
    private val listSize: Int by lazyFast { arguments!!.getInt(ARGUMENTS_LIST_SIZE) }

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
            viewModel.execute(mediaId, string)
            message = successMessage(act, string).toString()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            message = getString(R.string.popup_error_message)
        }
        act.toast(message)
    }


    private fun successMessage(context: Context, currentValue: String): CharSequence {
        if (mediaId.isPlayingQueue){
            return context.getString(R.string.queue_saved_as_playlist, currentValue)
        }
        if (mediaId.isLeaf){
            return context.getString(R.string.added_song_x_to_playlist_y, title, currentValue)
        }
        return context.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y,
                listSize, listSize, currentValue)
    }
}