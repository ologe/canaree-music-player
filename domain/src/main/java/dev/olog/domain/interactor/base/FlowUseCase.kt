package dev.olog.domain.interactor.base

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<T> {

    protected abstract fun buildUseCase(): Flow<T>

    operator fun invoke(): Flow<T> = buildUseCase()

}