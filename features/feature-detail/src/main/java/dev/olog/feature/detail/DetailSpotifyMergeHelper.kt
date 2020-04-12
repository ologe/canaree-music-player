package dev.olog.feature.detail

import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.spotify.SpotifyTrack
import dev.olog.domain.entity.track.Song
import dev.olog.feature.detail.mapper.toDetailDisplayableItem
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.DisplayableTrack
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.xdrop.fuzzywuzzy.FuzzySearch

internal suspend fun mergeTracks(
    songList: List<Song>,
    immutableSpotifyList: List<SpotifyTrack>,
    parentMediaId: PresentationId.Category,
    order: SortEntity
): Pair<List<DisplayableTrack>, List<DisplayableTrack>> = coroutineScope {
    if (immutableSpotifyList.isEmpty()) {
        return@coroutineScope songList
            .map { it.toDetailDisplayableItem(parentMediaId, order.type) }
            .toMutableList() to emptyList<DisplayableTrack>()
    }

    val spotifyTrackNames = immutableSpotifyList.map { it.name }
    val mutableSpotifyList = immutableSpotifyList.toMutableList()

    val matches = songList.map {
        async { FuzzySearch.extractOne(it.displayName, spotifyTrackNames).index }
    }.awaitAll()

    val songListResult = songList.mapIndexed { index: Int, song: Song ->
        val match = matches[index]
        if (match == -1) {
            // not found
            song
        } else {
            val spotifySong = immutableSpotifyList[match]
            mutableSpotifyList.remove(spotifySong)
            if (song.trackNumber == 0) {
                song.copy(
                    trackColumn = spotifySong.discNumber * 1000 + spotifySong.trackNumber
                )
            } else {
                song
            }
        }
    }.map { it.toDetailDisplayableItem(parentMediaId, order.type) }

    val spotifyListResult = mutableSpotifyList
        .map { it.toDetailDisplayableItem() }

    return@coroutineScope songListResult to spotifyListResult
}