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
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.domain.interactor.util.*
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesCreator @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val getAllFoldersUseCase: GetAllFoldersNewRequestUseCase,
        private val getAllPlaylistsUseCase: GetAllPlaylistsNewRequestUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsNewRequestUseCase,
        private val getAllArtistsUseCase: GetAllArtistsNewRequestUseCase,
        private val getAllGenresUseCase: GetAllGenresNewRequestUseCase,

        private val folderImagesCreator: FolderImagesCreator,
        private val playlistImagesCreator: PlaylistImagesCreator,
        private val albumImagesCreator: AlbumImagesCreator,
        private val artistImagesCreator: ArtistImagesCreator,
        private val genreImagesCreator: GenreImagesCreator,
        private val appPreferencesUseCase: AppPreferencesUseCase

) : DefaultLifecycleObserver {

    private val subscriptions = CompositeDisposable()
    private var folderDisposable : Disposable? = null
    private var playlistDisposable : Disposable? = null
    private var albumDisposable : Disposable? = null
    private var artistDisposable : Disposable? = null
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
        albumDisposable.unsubscribe()
        artistDisposable.unsubscribe()
        genreDisposable.unsubscribe()
        subscriptions.clear()
    }

    fun execute() {
        unsubscribe()

        getAllFoldersUseCase.execute()
                .onErrorReturn { listOf() }
                .doOnNext {
                    folderDisposable.unsubscribe()
                    folderDisposable = folderImagesCreator.execute()
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        getAllPlaylistsUseCase.execute()
                .doOnNext {
                    playlistDisposable.unsubscribe()
                    playlistDisposable = playlistImagesCreator.execute(it)
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        Observables.combineLatest(
                appPreferencesUseCase.observeAutoDownloadImages(),
                getAllAlbumsUseCase.execute(),
                { downloadType, albums -> ImageCreatorPojo(downloadType, albums) }
        ).doOnNext {
                    albumDisposable.unsubscribe()
                    albumDisposable = albumImagesCreator.execute(it)
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        Observables.combineLatest(
                appPreferencesUseCase.observeAutoDownloadImages(),
                getAllArtistsUseCase.execute(),
                { downloadType, artists -> ImageCreatorPojo(downloadType, artists) }
        )
                .doOnNext {
                    artistDisposable.unsubscribe()
                    artistDisposable = artistImagesCreator.execute(it)
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        getAllGenresUseCase.execute()
                .doOnNext {
                    genreDisposable.unsubscribe()
                    genreDisposable = genreImagesCreator.execute(it)
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

}

data class ImageCreatorPojo<T>(
        val canUseMobile: Boolean,
        val data: List<T>

)