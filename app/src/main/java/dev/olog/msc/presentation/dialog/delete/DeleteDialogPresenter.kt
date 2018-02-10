package dev.olog.msc.presentation.dialog.delete

import android.app.Application
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.dialog.DeleteUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

class DeleteDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val itemTitle: String,
        private val listSize : Int,
        private val deleteUseCase: DeleteUseCase
) {


    fun execute(): Completable {
        return deleteUseCase.execute(mediaId)
                .doOnComplete { createSuccessMessage() }
                .doOnError { createErrorMessage() }
    }

    private fun createSuccessMessage(){
        val message = when (mediaId.category) {
            MediaIdCategory.PLAYLIST -> application.getString(R.string.playlist_x_deleted, itemTitle)
            MediaIdCategory.SONGS -> application.getString(R.string.song_x_deleted, itemTitle)
            else -> application.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, listSize, listSize, itemTitle)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

}