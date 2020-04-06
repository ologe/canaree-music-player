package dev.olog.domain.gateway.base

import dev.olog.domain.entity.track.Artist
import kotlinx.coroutines.flow.Flow

interface HasRelatedArtists<Param> {
    fun observeRelatedArtists(param: Param): Flow<List<Artist>>
}