package dev.olog.feature.detail.detail.model

import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.detail.R

internal sealed class DetailFragmentModel {

    abstract val layoutType: Int

    interface Playable {
        val mediaId: MediaId.Track
    }

    val isPlayable: Boolean
        get() = this is Playable

    data class MainHeader(
        val mediaId: MediaId.Category,
        val title: String,
        val subtitle: String,
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_image
    }

    data class Biography(
        val content: String
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_biography
    }

    data class Track(
        override val layoutType: Int,
        override val mediaId: MediaId.Track,
        val title: String,
        val subtitle: String,
        val trackNumber: Int,
    ) : DetailFragmentModel(), Playable

    data class PlaylistTrack(
        override val layoutType: Int,
        override val mediaId: MediaId.Track,
        val title: String,
        val subtitle: String,
        val idInPlaylist: Long,
    ) : DetailFragmentModel(), Playable

    data class MostPlayedHeader(
        val title: String,
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_header
    }

    object MostPlayedList : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_list_most_played
    }

    data class RelatedArtistHeader(
        val title: String,
        val showSeeAll: Boolean,
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_header
    }

    object RelatedArtistList : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_list_related_artists
    }

    data class RecentlyAddedHeader(
        val title: String,
        val subtitle: String,
        val showSeeAll: Boolean,
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_header_recently_added
    }

    object RecentlyAddedList : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_list_recently_added
    }

    data class AlbumsHeader(
        val title: String
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_header_albums
    }

    object AlbumsList : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_list_albums
    }

    object Shuffle : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_shuffle
    }

    data class AllTracksHeader(
        val title: String,
        val subtitle: String
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_header_all_song
    }

    object EmptyState : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_empty_state
    }

    data class Duration(
        val content: String
    ) : DetailFragmentModel() {
        override val layoutType: Int = R.layout.item_detail_song_footer
    }

}