package dev.olog.presentation.dialog_entry

import dev.olog.presentation.model.DisplayableItem
import io.reactivex.Completable

data class DialogModel(
        val item: DisplayableItem,
        val useCase: Completable?
)