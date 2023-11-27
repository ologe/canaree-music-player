package dev.olog.presentation.dialogs.playlist.create

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseEditTextDialog
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import javax.inject.Inject

@AndroidEntryPoint
class NewPlaylistDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "NewPlaylistDialog"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                NavigationUtils.ARGUMENTS_MEDIA_ID to mediaId.toString(),
                NavigationUtils.ARGUMENTS_LIST_SIZE to listSize,
                NavigationUtils.ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: NewPlaylistDialogPresenter

    private val mediaId: MediaId by lazyFast {
        val mediaId = arguments!!.getString(NavigationUtils.ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { arguments!!.getString(NavigationUtils.ARGUMENTS_ITEM_TITLE)!! }
    private val listSize: Int by lazyFast { arguments!!.getInt(NavigationUtils.ARGUMENTS_LIST_SIZE) }

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
            presenter.execute(mediaId, string)
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