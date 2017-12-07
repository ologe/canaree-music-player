package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.domain.interactor.favorite.AddSongToFavoriteUseCase
import dev.olog.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.domain.interactor.favorite.RemoveSongFromFavoriteUseCase
import dev.olog.domain.interactor.music_service.InsertHistorySongUseCase
import dev.olog.music_service.di.PerService
import dev.olog.music_service.model.MediaEntity
import dev.olog.music_service.utils.ImageUtils
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.constants.MetadataConstants
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@PerService
class PlayerMetadata @Inject constructor(
        @ApplicationContext private val context: Context,
        private val mediaSession: MediaSessionCompat,
        private val insertHistorySongUseCase: InsertHistorySongUseCase,
        private val addSongToFavoriteUseCase: AddSongToFavoriteUseCase,
        private val removeSongFromFavoriteUseCase: RemoveSongFromFavoriteUseCase,
        private val isFavoriteSongUseCase: IsFavoriteSongUseCase

) : DefaultLifecycleObserver {

    private val builder = MediaMetadataCompat.Builder()

    fun update(entity: MediaEntity) {

        insertHistorySongUseCase.execute(entity.id)
                .timeout(2, TimeUnit.SECONDS)
                .subscribe({}, Throwable::printStackTrace)

        isFavoriteSongUseCase.execute(entity.id)
                .subscribe({ isFavorite ->

                    println("dio cane $isFavorite")

                    builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MediaIdHelper.songId(entity.id))
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, entity.title)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, entity.artist)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, entity.album)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, entity.duration)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, entity.image)
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, ImageUtils.getBitmapFromUri(context, entity.image))
                            .putLong(MetadataConstants.IS_EXPLICIT, if(entity.isExplicit) 1L else 0L)
                            .putLong(MetadataConstants.IS_REMIX, if(entity.isRemix) 1L else 0L)
                            .putRating(MediaMetadataCompat.METADATA_KEY_USER_RATING, RatingCompat.newHeartRating(isFavorite))
                            .build()

                    mediaSession.setMetadata(builder.build())

                }, Throwable::printStackTrace)
    }

    fun toggleFavorite(songId: Long){
        isFavoriteSongUseCase.execute(songId)
                .doOnSuccess { isFavorite ->
                    val extras = Bundle()
                    val value = if (isFavorite) {
                        MetadataConstants.ANIMATE_TO_NOT_FAVORITE
                    } else {
                        MetadataConstants.ANIMATE_TO_FAVORITE
                    }
                    extras.putInt(MetadataConstants.IS_FAVORITE, value)
                    mediaSession.setExtras(extras)
                }.flatMap { isFavorite ->
                    if (isFavorite){
                        removeSongFromFavoriteUseCase.execute(songId)
                    } else {
                        addSongToFavoriteUseCase.execute(songId)
                    }
                }.timeout(2, TimeUnit.SECONDS)
                .subscribe({}, Throwable::printStackTrace)
    }

}
