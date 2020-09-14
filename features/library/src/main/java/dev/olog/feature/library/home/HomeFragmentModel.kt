package dev.olog.feature.library.home

import dev.olog.feature.presentation.base.model.PresentationId

sealed class HomeFragmentModel {

    // TODO spotify suggestions - Made for you

    object Empty : HomeFragmentModel()

    data class Header(
        val title: String
    ) : HomeFragmentModel()

    data class RecentlyPlayedAlbums(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    data class RecentlyAddedAlbums(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    data class RecentlyPlayedArtists(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }

    data class RecentlyAddedArtists(
        val items: List<Item>
    ) : HomeFragmentModel() {
        fun isNotEmpty(): Boolean = items.isNotEmpty()
    }


    data class Item(
        val mediaId: PresentationId.Category,
        val title: String,
        val subtitle: String
    ) : HomeFragmentModel()

}