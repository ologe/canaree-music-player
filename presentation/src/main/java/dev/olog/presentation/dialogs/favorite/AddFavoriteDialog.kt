package dev.olog.presentation.dialogs.favorite

import android.content.Context
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFavoriteDialog : BaseDialog() {

    companion object {
        const val TAG = "AddFavoriteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): AddFavoriteDialog {
            return AddFavoriteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId,
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by argument<MediaId>(ARGUMENTS_MEDIA_ID)
    private val title by argument<String>(ARGUMENTS_ITEM_TITLE)
    private val listSize by argument<Int>(ARGUMENTS_LIST_SIZE)

    private val viewModel by viewModels<AddFavoriteDialogViewModel>()

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_add_to_favorites)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.popup_positive_ok, null)
            .setNegativeButton(R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        launch {
            var message: String
            try {
                viewModel.execute(mediaId)
                message = successMessage(act)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                message = failMessage(act)
            }
            act.toast(message)
            dismiss()
        }
    }

    private fun successMessage(context: Context): String {
        if (mediaId.isLeaf){
            return context.getString(R.string.song_x_added_to_favorites, title)
        }
        return context.resources.getQuantityString(R.plurals.xx_songs_added_to_favorites, listSize, listSize)
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return if (mediaId.isLeaf) {
            getString(R.string.add_song_x_to_favorite, title)
        } else {
            context!!.resources.getQuantityString(
                    R.plurals.add_xx_songs_to_favorite, listSize, listSize)
        }
    }

}