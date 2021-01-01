package dev.olog.domain.interactor.base

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCaseWithParam<T, Param> {

    protected abstract fun buildUseCase(param: Param): Flow<T>

    operator fun invoke(param: Param): Flow<T> = buildUseCase(param)

}