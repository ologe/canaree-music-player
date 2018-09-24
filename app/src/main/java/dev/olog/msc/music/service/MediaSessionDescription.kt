package dev.olog.msc.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.msc.R
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.item.*
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MediaSessionDescription @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val mediaSession: MediaSessionCompat,
        private val queueTitleFetcher: QueueTitleFetcher

) : DefaultLifecycleObserver{

    private var queueTitleDisposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        queueTitleDisposable.unsubscribe()
    }

    fun update(songList: List<MediaEntity>){
        val bundle = mediaSession.controller.extras ?: Bundle()
        val mediaId = if (songList.isNotEmpty()){
            songList[0].mediaId
        } else {
            MediaId.songId(-1, false)
        }
        bundle.putInt(MusicConstants.EXTRA_QUEUE_CATEGORY, mediaId.category.ordinal)
        mediaSession.setExtras(bundle)

        queueTitleDisposable.unsubscribe()
        queueTitleDisposable = queueTitleFetcher.search(mediaId)
                .subscribe(mediaSession::setQueueTitle, Throwable::printStackTrace)
    }

}

class QueueTitleFetcher @Inject constructor(
        @ApplicationContext private val context: Context,
        private val getFolderUseCase: GetFolderUseCase,
        private val getPlaylistUseCase: GetPlaylistUseCase,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getArtistUseCase: GetArtistUseCase,
        private val getGenreUseCase: GetGenreUseCase

){

    fun search(mediaId: MediaId): Single<String>{
        return when (mediaId.category){
            MediaIdCategory.FOLDERS -> searchFolder(mediaId)
            MediaIdCategory.PLAYLISTS -> searchPlaylist(mediaId)
            MediaIdCategory.ALBUMS -> searchAlbum(mediaId)
            MediaIdCategory.ARTISTS -> searchArtist(mediaId)
            MediaIdCategory.GENRES -> searchGenre(mediaId)
            else -> default()
        }
    }

    private fun searchFolder(mediaId: MediaId): Single<String>{
        return getFolderUseCase.execute(mediaId)
                .firstOrError()
                .map { it.title }
    }

    private fun searchPlaylist(mediaId: MediaId): Single<String>{
        return getPlaylistUseCase.execute(mediaId)
                .firstOrError()
                .map { it.title }
    }

    private fun searchAlbum(mediaId: MediaId): Single<String>{
        return getAlbumUseCase.execute(mediaId)
                .firstOrError()
                .map { "${it.title}${TextUtils.MIDDLE_DOT_SPACED}${it.artist}" }
    }

    private fun searchArtist(mediaId: MediaId): Single<String>{
        return getArtistUseCase.execute(mediaId)
                .firstOrError()
                .map { it.name }
    }

    private fun searchGenre(mediaId: MediaId): Single<String>{
        return getGenreUseCase.execute(mediaId)
                .firstOrError()
                .map { it.name }
    }

    private fun default(): Single<String> {
        return Single.just(context.getString(R.string.common_all_tracks))
    }

}