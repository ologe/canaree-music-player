package dev.olog.presentation.dialogs.playlist.create

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseEditTextDialog
import dev.olog.feature.presentation.base.extensions.getArgument
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.feature.presentation.base.extensions.withArguments
import dev.olog.shared.lazyFast
import timber.log.Timber
import javax.inject.Inject

class NewPlaylistDialog : BaseEditTextDialog() {

    companion object {
        const val TAG = "NewPlaylistDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"
        const val ARGUMENTS_LIST_SIZE = "${TAG}_arguments_list_size"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}_arguments_item_title"

        @JvmStatic
        fun newInstance(mediaId: PresentationId, listSize: Int, itemTitle: String): NewPlaylistDialog {
            return NewPlaylistDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    @Inject lateinit var presenter: NewPlaylistDialogPresenter

    private val mediaId by lazyFast {
        getArgument<PresentationId>(ARGUMENTS_MEDIA_ID)
    }
    private val title by lazyFast { getArgument<String>(ARGUMENTS_ITEM_TITLE) }
    private val listSize by lazyFast { getArgument<Int>(ARGUMENTS_LIST_SIZE) }

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
            message = successMessage(requireActivity(), string).toString()
        } catch (ex: Exception) {
            Timber.e(ex)
            message = getString(R.string.popup_error_message)
        }
        requireActivity().toast(message)
    }


    private fun successMessage(context: Context, currentValue: String): CharSequence {
        return when (mediaId) {
            is PresentationId.Track -> context.getString(R.string.added_song_x_to_playlist_y, title, currentValue)
            is PresentationId.Category -> context.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y,
                listSize, listSize, currentValue)
        }
    }
}