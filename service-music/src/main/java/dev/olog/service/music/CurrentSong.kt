package dev.olog.service.music

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
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
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@ServiceScoped
internal class CurrentSong @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val insertMostPlayedUseCase: InsertMostPlayedUseCase,
    private val insertHistorySongUseCase: InsertHistorySongUseCase,
    private val musicPreferencesUseCase: MusicPreferencesGateway,
    private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
    private val updateFavoriteStateUseCase: UpdateFavoriteStateUseCase,
    private val insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    private val insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: IPlayerLifecycle

) : IPlayerLifecycle.Listener {

    companion object {
        private val TAG = "SM:${CurrentSong::class.java.simpleName}"
    }

    private var isFavoriteJob by autoDisposeJob()

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    init {
        playerLifecycle.addListener(this)

        channel.consumeAsFlow()
            .onEach(this::insertSong)
            .flowOn(Dispatchers.Default)
            .launchIn(lifecycleOwner.lifecycleScope)

    }

    private suspend fun insertSong(entity: MediaEntity) {
        Log.v(TAG, "on new item ${entity.title}")
        if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
            Log.v(TAG, "insert last played artist ${entity.title}")
            insertLastPlayedArtistUseCase(entity.mediaId)
        } else if (entity.mediaId.isAlbum || entity.mediaId.isPodcastAlbum) {
            Log.v(TAG, "insert last played album ${entity.title}")
            insertLastPlayedAlbumUseCase(entity.mediaId)
        }

        Log.v(TAG, "insert most played ${entity.title}")
        MediaId.playableItem(entity.mediaId, entity.id)
        insertMostPlayedUseCase(entity.mediaId)

        Log.v(TAG, "insert to history ${entity.title}")
        insertHistorySongUseCase(
            InsertHistorySongUseCase.Input(
                entity.id,
                entity.isPodcast
            )
        )
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
        Log.v(TAG, "updateFavorite ${mediaEntity.title}")

        isFavoriteJob = lifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            val type = if (mediaEntity.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
            val isFavorite =
                isFavoriteSongUseCase(IsFavoriteSongUseCase.Input(mediaEntity.id, type))
            val isFavoriteEnum =
                if (isFavorite) FavoriteEnum.FAVORITE else FavoriteEnum.NOT_FAVORITE
            updateFavoriteStateUseCase(FavoriteStateEntity(mediaEntity.id, isFavoriteEnum, type))
        }
    }

    private fun saveLastMetadata(entity: MediaEntity) {
        Log.v(TAG, "saveLastMetadata ${entity.title}")
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            musicPreferencesUseCase.setLastMetadata(
                LastMetadata(
                    entity.title, entity.artist, entity.id
                )
            )
        }
    }

}