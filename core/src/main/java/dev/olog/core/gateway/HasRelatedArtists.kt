package dev.olog.core.gateway

import dev.olog.core.entity.track.Artist
import kotlinx.coroutines.flow.Flow

interface HasRelatedArtists<Param> {
    fun observeRelatedArtists(params: Param): Flow<List<Artist>>
}