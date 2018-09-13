package dev.olog.msc.app

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.all.GetAllGenresUseCase
import dev.olog.msc.domain.interactor.all.GetAllPlaylistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.utils.k.extension.asFlowable
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Flowables
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class KeepDataAlive @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
        private val getAllGenresUseCase: GetAllGenresUseCase

) : DefaultLifecycleObserver {


    private var disposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        disposable = Flowables.combineLatest(
                getAllSongsUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
                getAllPlaylistsUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
                getAllGenresUseCase.execute().onErrorReturnItem(listOf()).asFlowable()
        ).subscribe({}, Throwable::printStackTrace)
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

}