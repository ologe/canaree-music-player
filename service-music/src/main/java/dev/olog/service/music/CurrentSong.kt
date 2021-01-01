package dev.olog.service.music

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.domain.entity.Favorite
import dev.olog.domain.entity.LastMetadata
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.InsertHistorySongUseCase
import dev.olog.domain.interactor.InsertMostPlayedUseCase
import dev.olog.domain.interactor.lastplayed.InsertLastPlayedAlbumUseCase
import dev.olog.domain.interactor.lastplayed.InsertLastPlayedArtistUseCase
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
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
    private val favoriteGateway: FavoriteGateway,
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

        insertHistorySongUseCase(entity.mediaId)
    }

    private suspend fun updateFavorite(mediaEntity: MediaEntity) {
        val isFavorite = favoriteGateway.isFavorite(
            trackId = mediaEntity.id,
            type = Favorite.Type.fromMediaId(mediaEntity.mediaId)
        )
        favoriteGateway.updatePlayingTrackFavorite(
            Favorite(
                trackId = mediaEntity.id,
                state = Favorite.State.fromBoolean(isFavorite),
                favoriteType = Favorite.Type.fromMediaId(mediaEntity.mediaId)
            )
        )
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        musicPreferencesUseCase.setLastMetadata(
            LastMetadata(title = entity.title, subtitle = entity.artist, id = entity.id)
        )
    }

}