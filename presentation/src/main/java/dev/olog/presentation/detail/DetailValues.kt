package dev.olog.presentation.detail

import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack

data class DetailValues (
    val songs: List<DisplayableItem>,
    val mostPlayed: List<DisplayableTrack>,
    val recentlyAdded: List<DisplayableTrack>,
    val relatedArtists: List<DisplayableAlbum>,
    val siblings: List<DisplayableAlbum>
)