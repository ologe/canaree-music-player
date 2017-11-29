package dev.olog.presentation.dialog_entry

import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.presentation.model.DisplayableItem

data class DialogModel(
        val item: DisplayableItem,
        val useCase: CompletableUseCaseWithParam<Any>
)