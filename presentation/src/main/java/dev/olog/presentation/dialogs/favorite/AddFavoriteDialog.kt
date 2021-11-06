package dev.olog.presentation.dialogs.favorite

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.act
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.lazyFast
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId: MediaId by lazyFast {
        val mediaId = arguments!!.getString(ARGUMENTS_MEDIA_ID)!!
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { arguments!!.getString(ARGUMENTS_ITEM_TITLE)!! }
    private val listSize: Int by lazyFast { arguments!!.getInt(ARGUMENTS_LIST_SIZE) }

    @Inject lateinit var presenter: AddFavoriteDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(localization.R.string.popup_add_to_favorites)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(localization.R.string.popup_positive_ok, null)
            .setNegativeButton(localization.R.string.popup_negative_cancel, null)
    }

    override fun positionButtonAction(context: Context) {
        viewLifecycleOwner.lifecycleScope.launch {
            var message: String
            try {
                presenter.execute(mediaId)
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
        return context.getString(localization.R.string.popup_error_message)
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