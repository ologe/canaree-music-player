package dev.olog.core.interactor.lastplayed

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import javax.inject.Inject

class InsertRecentlyPlayedUseCase @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val albumGateway: AlbumGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
) {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId.category) {
            MediaIdCategory.ARTISTS -> {
                if (mediaId.isPodcast) {
                    podcastArtistGateway.addRecentlyPlayed(mediaId.id)
                } else {
                    artistGateway.addRecentlyPlayed(mediaId.id)
                }
            }
            MediaIdCategory.ALBUMS -> {
                if (mediaId.isPodcast) {
                    podcastAlbumGateway.addRecentlyPlayed(mediaId.id)
                } else {
                    albumGateway.addRecentlyPlayed(mediaId.id)
                }
            }
            else -> {}
        }

    }
}