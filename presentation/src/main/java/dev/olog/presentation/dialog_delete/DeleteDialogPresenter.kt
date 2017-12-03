package dev.olog.presentation.dialog_delete

import android.app.Application
import dev.olog.domain.interactor.dialog.DeleteUseCase
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Named

class DeleteDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: String,
        @Named ("item title") private val itemTitle: String,
        private val listSize : Int,
        private val deleteUseCase: DeleteUseCase
) {


    fun execute(): Completable {
        return deleteUseCase.execute(mediaId)
                .doOnComplete { createSuccessMessage() }
                .doOnError { createErrorMessage() }
    }

    private fun createSuccessMessage(){
        val category = MediaIdHelper.extractCategory(mediaId)
        val message = when (category) {
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> application.getString(R.string.playlist_x_deleted, itemTitle)
            MediaIdHelper.MEDIA_ID_BY_ALL -> application.getString(R.string.song_x_deleted, itemTitle)
            else -> application.resources.getQuantityString(R.plurals.added_xx_songs_to_playlist_y, listSize, listSize, itemTitle)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

}