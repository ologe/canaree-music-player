package dev.olog.service.music

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.MediaId
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.injection.dagger.PerService
import dev.olog.core.entity.LastMetadata
import dev.olog.core.interactor.*
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.interfaces.PlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.shared.CustomScope
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@PerService
class CurrentSong @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    insertMostPlayedUseCase: InsertMostPlayedUseCase,
    insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: PlayerLifecycle

) : DefaultLifecycleObserver, CoroutineScope by CustomScope() {

    private var isFavoriteDisposable: Disposable? = null

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onPrepare(entity: MediaEntity) {
            updateFavorite(entity)
            saveLastMetadata(entity)
        }

        override fun onMetadataChanged(entity: MediaEntity) {
            launch { channel.send(entity) }
            updateFavorite(entity)
            saveLastMetadata(entity)
        }
    }

    init {
        lifecycle.addObserver(this)

        playerLifecycle.addListener(playerListener)

        launch(Dispatchers.IO) {
            for (entity in channel) {
                if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
                    insertLastPlayedArtistUseCase(entity.mediaId)
                } else if (entity.mediaId.isAlbum || entity.mediaId.isPodcastAlbum) {
                    insertLastPlayedAlbumUseCase(entity.mediaId)
                }

                try {
                    MediaId.playableItem(entity.mediaId, entity.id)
                    insertMostPlayedUseCase(entity.mediaId)
                } catch (ex: Exception) {
                }

                insertHistorySongUseCase(InsertHistorySongUseCase.Input(entity.id, entity.isPodcast))
            }
        }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        isFavoriteDisposable.unsubscribe()
        cancel()
    }

    private fun updateFavorite(mediaEntity: MediaEntity) {
        isFavoriteDisposable.unsubscribe()
        val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        isFavoriteDisposable = isFavoriteSongUseCase
            .execute(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
            .map { if (it) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE }
            .flatMapCompletable {
                updateFavoriteStateUseCase.execute(
                    FavoriteStateEntity(
                        mediaEntity.id,
                        it,
                        type
                    )
                )
            }
            .subscribe({}, Throwable::printStackTrace)
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        launch {
            musicPreferencesUseCase.setLastMetadata(
                LastMetadata(
                    entity.title, entity.artist, entity.id
                )
            )
        }
    }

}