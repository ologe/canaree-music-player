package dev.olog.presentation.dialogs.delete

import android.app.RecoverableSecurityException
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.dialogs.BaseDialog
import dev.olog.presentation.utils.asHtml
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.utils.isQ
import dev.olog.shared.lazyFast
import timber.log.Timber
import javax.inject.Inject

class DeleteDialog: BaseDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "${TAG}_arguments_media_id"
        const val ARGUMENTS_LIST_SIZE = "${TAG}_arguments_list_size"
        const val ARGUMENTS_ITEM_TITLE = "${TAG}_arguments_item_title"

        @JvmStatic
        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId: MediaId by lazyFast {
        val mediaId = getArgument<String>(ARGUMENTS_MEDIA_ID)
        MediaId.fromString(mediaId)
    }
    private val title: String by lazyFast { getArgument(ARGUMENTS_ITEM_TITLE) }
    private val listSize: Int by lazyFast { getArgument(ARGUMENTS_LIST_SIZE) }

    @Inject lateinit var presenter: DeleteDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_delete)
            .setMessage(createMessage().asHtml())
            .setPositiveButton(R.string.popup_positive_delete, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction(context: Context) {
        launchWhenResumed {
            catchRecoverableSecurityException(this@DeleteDialog) {
                tryExecute()
            }
        }
    }

    private suspend fun tryExecute(){
        var message: String
        try {
            presenter.execute(mediaId)
            message = successMessage(act)
        } catch (ex: Exception) {
            Timber.e(ex)
            if (isQ() && ex is RecoverableSecurityException){
                throw ex
            }
            message = failMessage(act)
        }
        act.toast(message)
        dismiss()
    }

    override suspend fun onRecoverableSecurityExceptionRecovered() {
        tryExecute()
    }

    private fun successMessage(context: Context): String {
        return when (mediaId.category) {
            MediaIdCategory.PLAYLISTS -> context.getString(R.string.playlist_x_deleted, title)
            MediaIdCategory.SONGS -> context.getString(R.string.song_x_deleted, title)
            else -> context.resources.getQuantityString(
                R.plurals.xx_songs_deleted_from_y,
                listSize, listSize, title
            )
        }
    }

    private fun failMessage(context: Context): String {
        return context.getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        val itemTitle = getArgument<String>(ARGUMENTS_ITEM_TITLE)

        return when {
            mediaId.isAll || mediaId.isLeaf -> getString(R.string.delete_song_y, itemTitle)
            mediaId.isPlaylist -> getString(R.string.delete_playlist_y, itemTitle)
            else -> requireContext().resources.getQuantityString(R.plurals.delete_xx_songs_from_y, listSize, listSize)
        }
    }

}