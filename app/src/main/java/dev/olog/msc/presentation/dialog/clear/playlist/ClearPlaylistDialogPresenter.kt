package dev.olog.msc.presentation.dialog.clear.playlist

import android.app.Application
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.dialog.ClearPlaylistUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

class ClearPlaylistDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val itemTitle: String,
        private val clearPlaylistUseCase: ClearPlaylistUseCase

) {

    fun execute(): Completable {
        return clearPlaylistUseCase.execute(mediaId)
                .doOnComplete { createSuccessMessage() }
                .doOnError { createErrorMessage() }
    }

    private fun createSuccessMessage(){
        val message = application.getString(R.string.playlist_x_cleared, itemTitle)
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

}