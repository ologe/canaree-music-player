package dev.olog.presentation.dialog_add_favorite

import android.app.Application
import android.text.TextUtils
import dev.olog.domain.interactor.dialog.AddToFavoriteUseCase
import dev.olog.presentation.R
import dev.olog.shared.MediaId
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

class AddFavoriteDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val addToFavoriteUseCase: AddToFavoriteUseCase
) {

    fun execute(): Completable {
        return addToFavoriteUseCase.execute(mediaId)
                .doOnSuccess { createSuccessMessage(it) }
                .doOnError { createErrorMessage() }
                .toCompletable()
    }

    private fun createSuccessMessage(string: String){
        val message = if (TextUtils.isDigitsOnly(string)){
            val size = string.toInt()
            application.resources.getQuantityString(R.plurals.xx_songs_added_to_favorites, size, size)
        } else {
            application.getString(R.string.song_x_added_to_favorites, string)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

}