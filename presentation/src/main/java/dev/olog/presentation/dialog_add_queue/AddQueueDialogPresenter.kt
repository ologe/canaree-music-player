package dev.olog.presentation.dialog_add_queue

import io.reactivex.Completable
import javax.inject.Inject

class AddQueueDialogPresenter @Inject constructor(
        private val mediaId: String
) {

    fun execute(): Completable {
        return Completable.complete()
    }

}