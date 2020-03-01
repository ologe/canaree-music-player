package dev.olog.presentation.detail

import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack

data class DetailValues (
    val songs: List<DisplayableItem>,
    val mostPlayed: List<DisplayableTrack>,
    val recentlyAdded: List<DisplayableItem>,
    val relatedArtists: List<DisplayableItem>,
    val siblings: List<DisplayableItem>
)