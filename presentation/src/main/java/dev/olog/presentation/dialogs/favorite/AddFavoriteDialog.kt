package dev.olog.presentation.dialogs.favorite

import android.content.Context
import android.content.DialogInterface
import dev.olog.core.MediaId

import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.withArguments
import javax.inject.Inject

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

    @Inject lateinit var mediaId: MediaId
    @Inject @JvmField var listSize: Int = 0
    @Inject lateinit var title: String
    @Inject lateinit var presenter: AddFavoriteDialogPresenter

    override fun title(context: Context): CharSequence {
        return context.getString(R.string.popup_add_to_favorites)
    }

    override fun message(context: Context): CharSequence {
        return createMessage().asHtml()
    }

    override fun negativeButtonMessage(context: Context): Int {
        return R.string.popup_negative_cancel
    }

    override fun positiveButtonMessage(context: Context): Int {
        return R.string.popup_positive_ok
    }

    override fun successMessage(context: Context): String {
        if (mediaId.isLeaf){
            return context.getString(R.string.song_x_added_to_favorites, title)
        }
        return context.resources.getQuantityString(R.plurals.xx_songs_added_to_favorites, listSize, listSize)
    }

    override fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    override fun positiveAction(dialogInterface: DialogInterface, which: Int) {
        return presenter.execute()
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