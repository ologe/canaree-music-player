package dev.olog.domain.interactor.base

abstract class PrefsUseCase<Result> {

    abstract fun get(): Result

    abstract fun set(param: Result)

}
