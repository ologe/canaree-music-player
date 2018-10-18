package dev.olog.msc.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.all.GetAllGenresUseCase
import dev.olog.msc.domain.interactor.all.GetAllPlaylistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllPodcastUseCase
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
        private val getAllGenresUseCase: GetAllGenresUseCase,
        private val getAllPodcastUseCase: GetAllPodcastUseCase

) : DefaultLifecycleObserver {


    private var disposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        disposable = Flowables.combineLatest(
                getAllSongsUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
                getAllPlaylistsUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
                getAllGenresUseCase.execute().onErrorReturnItem(listOf()).asFlowable(),
                getAllPodcastUseCase.execute().onErrorReturnItem(listOf()).asFlowable()
        ) { _, _, _, _ -> 0}.subscribe({}, Throwable::printStackTrace)
    }

    override fun onStop(owner: LifecycleOwner) {
        disposable.unsubscribe()
    }

}