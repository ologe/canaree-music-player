package dev.olog.service.music

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.coroutines.DispatcherScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.LastMetadata
import dev.olog.domain.entity.favorite.FavoriteItemState
import dev.olog.domain.entity.favorite.FavoriteState
import dev.olog.domain.entity.favorite.FavoriteTrackType
import dev.olog.domain.interactor.InsertHistorySongUseCase
import dev.olog.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.domain.interactor.favorite.UpdateFavoriteStateUseCase
import dev.olog.domain.interactor.lastplayed.InsertLastPlayedAlbumUseCase
import dev.olog.domain.interactor.lastplayed.InsertLastPlayedArtistUseCase
import dev.olog.domain.interactor.mostplayed.InsertMostPlayedUseCase
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@PerService
internal class CurrentSong @Inject constructor(
    insertMostPlayedUseCase: InsertMostPlayedUseCase,
    insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: IPlayerLifecycle,
    schedulers: Schedulers

) : DefaultLifecycleObserver,
    IPlayerLifecycle.Listener {

    companion object {
        @JvmStatic
        private val TAG = "SM:${CurrentSong::class.java.simpleName}"
    }

    private val scope by DispatcherScope(schedulers.cpu)

    private var isFavoriteJob by autoDisposeJob()

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    init {
        playerLifecycle.addListener(this)

        scope.launch {
            for (entity in channel) {
                Timber.v("$TAG on new item ${entity.title}")
                val mediaId = entity.mediaId

                when (mediaId.category) {
                    MediaIdCategory.ARTISTS,
                    MediaIdCategory.PODCASTS_AUTHORS -> {
                        Timber.v("$TAG insert last played artist ${entity.title}")
                        insertLastPlayedArtistUseCase(entity.mediaId.parentId)
                    }
                    MediaIdCategory.ALBUMS -> {
                        Timber.v("$TAG insert last played album ${entity.title}")
                        insertLastPlayedAlbumUseCase(entity.mediaId.parentId)
                    }
                    else -> {}
                }

                Timber.v("$TAG insert most played ${entity.title}")
                insertMostPlayedUseCase(entity.mediaId)

                Timber.v("$TAG insert to history ${entity.title}")
                insertHistorySongUseCase(
                    InsertHistorySongUseCase.Input(entity.id, entity.isPodcast)
                )
            }
        }

    }

    override fun onDestroy(owner: LifecycleOwner) {
        isFavoriteJob = null
        channel.close()
        scope.cancel()
    }

    override fun onPrepare(metadata: MetadataEntity) {
        updateFavorite(metadata.entity)
        saveLastMetadata(metadata.entity)
    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        channel.offer(metadata.entity)
        updateFavorite(metadata.entity)
        saveLastMetadata(metadata.entity)
    }

    private fun updateFavorite(mediaEntity: MediaEntity) {
        Timber.v("$TAG updateFavorite ${mediaEntity.title}")

        isFavoriteJob = scope.launch {
            val type = if (mediaEntity.isPodcast) FavoriteTrackType.PODCAST else FavoriteTrackType.TRACK
            val isFavorite =
                isFavoriteSongUseCase(mediaEntity.id, type)
            val isFavoriteEnum =
                if (isFavorite) FavoriteState.FAVORITE else FavoriteState.NOT_FAVORITE
            updateFavoriteStateUseCase(FavoriteItemState(mediaEntity.id, isFavoriteEnum, type))
        }
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        Timber.v("$TAG saveLastMetadata ${entity.title}")
        scope.launch {
            musicPreferencesUseCase.setLastMetadata(
                LastMetadata(entity.title, entity.artist, entity.id)
            )
        }
    }

}