package dev.olog.msc.presentation.image.creation

import android.Manifest
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.domain.interactor.util.*
import dev.olog.msc.utils.k.extension.isConnected
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates

@Singleton
class ImagesCreator @Inject constructor(
        @ApplicationContext private val context: Context,
        @ProcessLifecycle private val lifecycle: Lifecycle,
        private val getAllFoldersUseCase: GetAllFoldersNewRequestUseCase,
        private val getAllPlaylistsUseCase: GetAllPlaylistsNewRequestUseCase,
        private val getAllSongsUseCAse: GetAllSongsNewRequestUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsNewRequestUseCase,
        private val getAllArtistsUseCase: GetAllArtistsNewRequestUseCase,
        private val getAllGenresUseCase: GetAllGenresNewRequestUseCase,

        private val folderImagesCreator: FolderImagesCreator,
        private val playlistImagesCreator: PlaylistImagesCreator,
        private val songsImagesCreator: SongImageCreator,
        private val albumImagesCreator: AlbumImagesCreator,
        private val artistImagesCreator: ArtistImagesCreator,
        private val genreImagesCreator: GenreImagesCreator,
        private val appPreferencesUseCase: AppPreferencesUseCase

) : DefaultLifecycleObserver {

    companion object {
        var CAN_DOWNLOAD_ON_MOBILE by Delegates.notNull<Boolean>()
    }

    private val subscriptions = CompositeDisposable()
    private var folderDisposable : Disposable? = null
    private var playlistDisposable : Disposable? = null
    private var songDisposable : Disposable? = null
    private var albumDisposable : Disposable? = null
    private var artistDisposable : Disposable? = null
    private var genreDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        CAN_DOWNLOAD_ON_MOBILE = appPreferencesUseCase.getCanDownloadOnMobile()
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
        songDisposable.unsubscribe()
        subscriptions.clear()
    }

    fun execute() {
        return

        unsubscribe()

        appPreferencesUseCase.observeCanDownloadOnMobile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ CAN_DOWNLOAD_ON_MOBILE = it }, Throwable::printStackTrace)
                .addTo(subscriptions)

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

        Observables.combineLatest(
                ReactiveNetwork.observeNetworkConnectivity(context)
                        .map { it.isConnected() },
                appPreferencesUseCase.observeCanDownloadOnMobile(),
                getAllAlbumsUseCase.execute(),
                { isConnected, _, albums -> albums to isConnected })
                .subscribe({ (data, isConnected) ->
                    albumDisposable.unsubscribe()
                    if (isConnected){
                        albumDisposable = albumImagesCreator.execute(data)
                                .subscribe({
                                    // fetch song images only after downloading all album images
                                    // due to only 5 request per second limit of LastFm
                                    fetchSongImages()
                                }, Throwable::printStackTrace)
                    }
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        Observables.combineLatest(
                ReactiveNetwork.observeNetworkConnectivity(context)
                        .map { it.isConnected() },
                appPreferencesUseCase.observeCanDownloadOnMobile(),
                getAllArtistsUseCase.execute(),
                { isConnected, _, artists -> artists to isConnected })
                .subscribe({ (data, isConnected) ->
                    artistDisposable.unsubscribe()
                    if (isConnected){
                        artistDisposable = artistImagesCreator.execute(data)
                                .subscribe({}, Throwable::printStackTrace)
                    }
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

    private fun fetchSongImages(){
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
            Observables.combineLatest(
                    ReactiveNetwork.observeNetworkConnectivity(context)
                            .map { it.isConnected() },
                    appPreferencesUseCase.observeCanDownloadOnMobile(),
                    getAllSongsUseCAse.execute(),
                    { isConnected, _, songs -> songs to isConnected })
                    .subscribe({ (data, isConnected) ->
                        songDisposable.unsubscribe()
                        if (isConnected){
                            songDisposable = songsImagesCreator.execute(data)
                                    .subscribe({}, Throwable::printStackTrace)
                        }
                    }, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
    }

}