package dev.olog.feature.library.tracks

import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.*
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class TracksFragmentViewModel @Inject constructor(
    private val trackGateway: TrackGateway,
    private val podcastGateway: PodcastGateway,
    private val sortPreferences: SortPreferences,
    private val schedulers: Schedulers
) : ViewModel() {

    fun data(isPodcast: Boolean): Flow<List<DisplayableItem>> {
        return if (isPodcast) {
            trackGateway.observeAllPodcasts()
        } else {
            trackGateway.observeAllTracks()
        }.map {
            it.map { it.toTabDisplayableItem() }
                .startWithIfNotEmpty(shuffleHeader)
        }.flowOn(schedulers.cpu)
    }

    fun observeAllCurrentPositions() = podcastGateway.observeAllCurrentPositions()
        .map {
            it.groupBy { it.id }.mapValues { it.value[0].position.toInt() }
        }.flowOn(schedulers.cpu)

    fun getAllTracksSortOrder(): SortEntity {
        return sortPreferences.getAllTracksSort()
    }

    private val shuffleHeader = DisplayableHeader(
        R.layout.item_tracks_header,
        PresentationId.headerId("header"), ""
    )

    private fun Song.toTabDisplayableItem(): DisplayableItem {
        return DisplayableTrack(
            type = if (isPodcast) R.layout.item_track_podcast else R.layout.item_track,
            mediaId = presentationId,
            title = title,
            artist = artist,
            album = album,
            idInPlaylist = if (isPodcast) TimeUnit.MILLISECONDS.toMinutes(duration) // TOOD what's that??
                .toInt() else this.idInPlaylist,
            dataModified = this.dateModified,
            duration = this.duration
        )
    }

}