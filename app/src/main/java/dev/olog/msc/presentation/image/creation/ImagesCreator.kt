package dev.olog.msc.presentation.image.creation

import android.Manifest
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.util.GetAllFoldersNewRequestUseCase
import dev.olog.msc.domain.interactor.util.GetAllGenresNewRequestUseCase
import dev.olog.msc.domain.interactor.util.GetAllPlaylistsNewRequestUseCase
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesCreator @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle private val lifecycle: Lifecycle,
        private val getAllFoldersUseCase: GetAllFoldersNewRequestUseCase,
        private val getAllPlaylistsUseCase: GetAllPlaylistsNewRequestUseCase,
        private val getAllGenresUseCase: GetAllGenresNewRequestUseCase,

        private val folderImagesCreator: FolderImagesCreator,
        private val playlistImagesCreator: PlaylistImagesCreator,
        private val genreImagesCreator: GenreImagesCreator

) : DefaultLifecycleObserver {

    private val subscriptions = CompositeDisposable()
    private var folderDisposable : Disposable? = null
    private var playlistDisposable : Disposable? = null
    private var genreDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        val storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (storagePermission == PackageManager.PERMISSION_GRANTED){
            execute()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        unsubscribe()
    }

    private fun unsubscribe(){
        folderDisposable.unsubscribe()
        playlistDisposable.unsubscribe()
        genreDisposable.unsubscribe()
        subscriptions.clear()
    }

    fun execute() {
        unsubscribe()

        getAllFoldersUseCase.execute()
                .onErrorReturnItem(listOf())
                .subscribe({
                    folderDisposable.unsubscribe()
                    folderDisposable = folderImagesCreator.execute()
                            .subscribe({}, Throwable::printStackTrace)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        getAllPlaylistsUseCase.execute()
                .subscribe({
                    playlistDisposable.unsubscribe()
                    playlistDisposable = playlistImagesCreator.execute(it)
                            .subscribe({}, Throwable::printStackTrace)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        getAllGenresUseCase.execute()
                .subscribe({
                    genreDisposable.unsubscribe()
                    genreDisposable = genreImagesCreator.execute(it)
                            .subscribe({}, Throwable::printStackTrace)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

}