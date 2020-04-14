@file:Suppress("UNCHECKED_CAST")

package dev.olog.feature.detail.model

import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.DisplayableTrack

class DetailValues (
    val songs: List<DisplayableItem>,
    mostPlayed: List<DisplayableItem>,
    recentlyAdded: List<DisplayableItem>,
    relatedArtists: List<DisplayableItem>,
    siblings: List<DisplayableItem>,
    spotifyAppearsOn: List<DisplayableItem>,
    spotifyAlbums: List<DisplayableItem>
) {

    val mostPlayed: List<DisplayableTrack> = mostPlayed as List<DisplayableTrack>
    val recentlyAdded: List<DisplayableTrack> = recentlyAdded as List<DisplayableTrack>
    val relatedArtists: List<DisplayableAlbum> = relatedArtists as List<DisplayableAlbum>
    val siblings: List<DisplayableAlbum> = siblings as List<DisplayableAlbum>
    val spotifySingles: List<DisplayableAlbum> = spotifyAppearsOn as List<DisplayableAlbum>
    val spotifyAlbums: List<DisplayableAlbum> = spotifyAlbums as List<DisplayableAlbum>

}