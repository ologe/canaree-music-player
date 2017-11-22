package dev.olog.domain.interactor

abstract class PrefsUseCase<Result> {

    abstract fun get(): Result

    abstract fun set(param: Result)

}
