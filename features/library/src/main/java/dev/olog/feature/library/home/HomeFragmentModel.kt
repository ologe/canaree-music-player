package dev.olog.feature.library.home

import dev.olog.feature.presentation.base.model.PresentationId
import javax.annotation.concurrent.Immutable

@Immutable
sealed class HomeFragmentModel {

    // TODO spotify suggestions - Made for you

    @Immutable
    object Empty : HomeFragmentModel()

    @Immutable
    data class Header(
        val title: String
    ) : HomeFragmentModel()

    @Immutable
    data class RecentlyPlayedAlbums(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    @Immutable
    data class RecentlyAddedAlbums(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    @Immutable
    data class RecentlyPlayedArtists(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    @Immutable
    data class RecentlyAddedArtists(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    @Immutable
    data class Item(
        val mediaId: PresentationId.Category,
        val title: String,
        val subtitle: String
    ) : HomeFragmentModel()

}