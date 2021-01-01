package dev.olog.feature.dialog.delete

import android.app.RecoverableSecurityException
import androidx.core.text.parseAsHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.feature.dialog.R
import dev.olog.feature.dialog.base.BaseDialog
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.launch
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.extensions.withArguments
import dev.olog.shared.android.utils.isQ
import javax.inject.Inject

@AndroidEntryPoint
class DeleteDialog: BaseDialog() {

    companion object {
        const val TAG = "DeleteDialog"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"
        const val ARGUMENTS_LIST_SIZE = "$TAG.arguments.list_size"
        const val ARGUMENTS_ITEM_TITLE = "$TAG.arguments.item_title"

        fun newInstance(mediaId: MediaId, listSize: Int, itemTitle: String): DeleteDialog {
            return DeleteDialog().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString(),
                    ARGUMENTS_LIST_SIZE to listSize,
                    ARGUMENTS_ITEM_TITLE to itemTitle
            )
        }
    }

    private val mediaId by argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)
    private val itemTitle by argument<String>(ARGUMENTS_ITEM_TITLE)
    private val listSize by argument<Int>(ARGUMENTS_LIST_SIZE)

    @Inject lateinit var presenter: DeleteDialogPresenter

    override fun extendBuilder(builder: MaterialAlertDialogBuilder): MaterialAlertDialogBuilder {
        return builder.setTitle(R.string.popup_delete)
            .setMessage(createMessage().parseAsHtml())
            .setPositiveButton(R.string.popup_positive_delete, null)
            .setNegativeButton(R.string.popup_negative_no, null)
    }

    override fun positionButtonAction() {
        launch {
            catchRecoverableSecurityException(this@DeleteDialog) {
                tryExecute()
            }
        }
    }

    private suspend fun tryExecute(){
        var message: String
        try {
            presenter.execute(mediaId)
            message = successMessage()
        } catch (ex: Throwable) {
            if (isQ() && ex is RecoverableSecurityException){
                throw ex
            }
            ex.printStackTrace()
            message = failMessage()
        }
        requireActivity().toast(message)
        dismiss()
    }

    override suspend fun onRecoverableSecurityExceptionRecovered() {
        tryExecute()
    }

    private fun successMessage(): String {
        return when (mediaId.category) {
            MediaIdCategory.PLAYLISTS -> getString(R.string.playlist_x_deleted, itemTitle)
            MediaIdCategory.SONGS -> getString(R.string.song_x_deleted, itemTitle)
            else -> resources.getQuantityString(
                R.plurals.xx_songs_deleted_from_y,
                listSize, listSize, itemTitle
            )
        }
    }

    private fun failMessage(): String {
        return getString(R.string.popup_error_message)
    }

    private fun createMessage() : String {
        return when {
            mediaId.isAll || mediaId.isLeaf -> getString(R.string.delete_song_y, itemTitle)
            mediaId.isPlaylist -> getString(R.string.delete_playlist_y, itemTitle)
            else -> resources.getQuantityString(R.plurals.delete_xx_songs_from_y, listSize, listSize)
        }
    }

}