package dev.olog.service.music

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.entity.LastMetadata
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.interactor.InsertHistorySongUseCase
import dev.olog.core.interactor.InsertMostPlayedUseCase
import dev.olog.core.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.core.interactor.favorite.UpdateFavoriteStateUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedAlbumUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedArtistUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.player.InternalPlayerState
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ServiceScoped
internal class CurrentSong @Inject constructor(
    schedulers: Schedulers,
    lifecycleOwner: LifecycleOwner,
    private val insertMostPlayedUseCase: InsertMostPlayedUseCase,
    private val insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerState: InternalPlayerState,
) {

    init {
        playerState.state
            .map { it.entity }
            .distinctUntilChanged()
            .mapLatest(this::onTrackChanged)
            .flowOn(schedulers.cpu)
            .launchIn(lifecycleOwner.lifecycleScope)

    }

    private suspend fun onTrackChanged(entity: MediaEntity) {
        insertSong(entity)
        updateFavorite(entity)
        saveLastMetadata(entity)
    }

    private suspend fun insertSong(entity: MediaEntity) {
        if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
            insertLastPlayedArtistUseCase(entity.mediaId)
        } else if (entity.mediaId.isAlbum || entity.mediaId.isPodcastAlbum) {
            insertLastPlayedAlbumUseCase(entity.mediaId)
        }

        insertMostPlayedUseCase(entity.mediaId)

        insertHistorySongUseCase(
            InsertHistorySongUseCase.Input(
                entity.id,
                entity.isPodcast
            )
        )
    }

    private suspend fun updateFavorite(mediaEntity: MediaEntity) {
        val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        val isFavorite = isFavoriteSongUseCase(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
        val isFavoriteEnum = if (isFavorite) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE
        updateFavoriteStateUseCase(FavoriteStateEntity(mediaEntity.id, isFavoriteEnum, type))
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        musicPreferencesUseCase.setLastMetadata(
            LastMetadata(title = entity.title, subtitle = entity.artist, id = entity.id)
        )
    }

}