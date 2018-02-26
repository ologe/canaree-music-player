package dev.olog.msc.app

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.tab.GetAllGenresUseCase
import dev.olog.msc.domain.interactor.tab.GetAllPlaylistsUseCase
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

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
        disposable = Observables.combineLatest(
                getAllSongsUseCase.execute().onErrorReturn { listOf() },
                getAllPlaylistsUseCase.execute().onErrorReturn { listOf() },
                getAllGenresUseCase.execute().onErrorReturn { listOf() }
        ).subscribe({}, Throwable::printStackTrace)
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

}