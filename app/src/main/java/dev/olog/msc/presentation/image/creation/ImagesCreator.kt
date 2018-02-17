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
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.FolderGateway
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesCreator @Inject constructor(
        @ApplicationContext context: Context,
        @ProcessLifecycle lifecycle: Lifecycle,
        private val folderGateway: FolderGateway,
        private val folderImagesCreator: FolderImagesCreator,
        private val playlistGateway: PlaylistGateway,
        private val playlistImagesCreator: PlaylistImagesCreator,
        private val artistGateway: ArtistGateway,
        private val artistImagesCreator: ArtistImagesCreator,
        private val genreGateway: GenreGateway,
        private val genreImagesCreator: GenreImagesCreator


) : DefaultLifecycleObserver {

    private val hasStoragePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private val subscriptions = CompositeDisposable()
    private var folderDisposable : Disposable? = null
    private var playlistDisposable : Disposable? = null
    private var artistDisposable : Disposable? = null
    private var genreDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (hasStoragePermission){
            createImages()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        subscriptions.clear()
        folderDisposable.unsubscribe()
        playlistDisposable.unsubscribe()
        artistDisposable.unsubscribe()
        genreDisposable.unsubscribe()
    }

    fun createImages() {
        subscriptions.clear()

        folderGateway.getAll()
                .doOnNext {
                    folderDisposable.unsubscribe()
                    folderDisposable = folderImagesCreator.execute()
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        playlistGateway.getAll()
                .doOnNext {
                    playlistDisposable.unsubscribe()
                    playlistDisposable = playlistImagesCreator.execute()
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        artistGateway.getAll()
                .doOnNext {
                    artistDisposable.unsubscribe()
                    artistDisposable = artistImagesCreator.execute()
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)

        genreGateway.getAll()
                .doOnNext {
                    genreDisposable.unsubscribe()
                    genreDisposable = genreImagesCreator.execute()
                            .subscribe({}, Throwable::printStackTrace)
                }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

}