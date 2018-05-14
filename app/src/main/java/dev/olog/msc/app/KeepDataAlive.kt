package dev.olog.msc.app

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import dev.olog.msc.Permissions
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.all.GetAllGenresUseCase
import dev.olog.msc.domain.interactor.all.GetAllPlaylistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeepDataAlive @Inject constructor(
        @ApplicationContext private val context: Context,
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
        if (Permissions.canReadStorage(context)){
            execute()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        unsubscribe()
    }

    private fun unsubscribe(){
        disposable.unsubscribe()
    }

    fun execute(){
        if (!Permissions.canReadStorage(context)){
            return
        }

        unsubscribe()

        disposable = Observables.combineLatest(
                getAllSongsUseCase.execute().onErrorReturnItem(listOf()),
                getAllPlaylistsUseCase.execute().onErrorReturnItem(listOf()),
                getAllGenresUseCase.execute().onErrorReturnItem(listOf())
        ).subscribe({}, Throwable::printStackTrace)
    }

}