package dev.olog.feature.media

import androidx.lifecycle.DefaultLifecycleObserver
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
import dev.olog.core.ServiceScope
import dev.olog.core.gateway.PlayingGateway
import dev.olog.core.interactor.InsertHistorySongUseCase
import dev.olog.core.interactor.InsertMostPlayedUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedAlbumUseCase
import dev.olog.core.interactor.lastplayed.InsertLastPlayedArtistUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.media.api.model.MediaEntity
import dev.olog.feature.media.api.model.MetadataEntity
import dev.olog.feature.media.interfaces.IPlayerLifecycle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ServiceScoped
internal class CurrentSong @Inject constructor(
    insertMostPlayedUseCase: InsertMostPlayedUseCase,
    insertHistorySongUseCase: InsertHistorySongUseCase,
    insertLastPlayedAlbumUseCase: InsertLastPlayedAlbumUseCase,
    insertLastPlayedArtistUseCase: InsertLastPlayedArtistUseCase,
    playerLifecycle: IPlayerLifecycle,
    serviceScope: ServiceScope,
    schedulers: Schedulers,
    private val playingGateway: PlayingGateway,
) : DefaultLifecycleObserver,
    IPlayerLifecycle.Listener {

    private val channel = Channel<MediaEntity>(Channel.UNLIMITED)

    init {
        playerLifecycle.addListener(this)

        serviceScope.launch(schedulers.io) {
            for (entity in channel) {
                playingGateway.update(entity.id.toString())

                if (entity.mediaId.isArtist || entity.mediaId.isPodcastArtist) {
                    insertLastPlayedArtistUseCase(entity.mediaId)
                } else if (entity.mediaId.isAlbum || entity.mediaId.isPodcastAlbum) {
                    insertLastPlayedAlbumUseCase(entity.mediaId)
                }

                MediaId.playableItem(entity.mediaId, entity.id)
                insertMostPlayedUseCase(entity.mediaId)

                insertHistorySongUseCase(
                    InsertHistorySongUseCase.Input(
                        entity.id,
                        entity.isPodcast
                    )
                )
            }
        }

    }

    override fun onMetadataChanged(metadata: MetadataEntity) {
        channel.trySend(metadata.entity)
    }

}