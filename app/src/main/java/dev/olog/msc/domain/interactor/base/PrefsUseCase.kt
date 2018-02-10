package dev.olog.msc.domain.interactor.base

abstract class PrefsUseCase<Model> {

    abstract fun get(): Model

    abstract fun set(param: Model)

}
