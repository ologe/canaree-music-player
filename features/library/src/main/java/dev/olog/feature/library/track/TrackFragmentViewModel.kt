package dev.olog.feature.library.track

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.SortPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.library.track.TrackFragmentItem.Shuffle
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.navigation.Params
import dev.olog.shared.TextUtils.MIDDLE_DOT_SPACED
import dev.olog.shared.coroutines.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

internal class TrackFragmentViewModel @ViewModelInject constructor(
    @Assisted private val bundle: SavedStateHandle,
    private val schedulers: Schedulers,
    private val podcastGateway: PodcastGateway,
    private val appPreferencesUseCase: SortPreferences,
    private val trackGateway: TrackGateway
) : ViewModel() {

    private val isPodcast: Boolean
        get() = bundle.get(Params.PODCAST)!!

    val allPodcastPositions: Flow<Map<Long, Int>>
        get() = podcastGateway.observeAllCurrentPositions()
            .map {
                it.groupBy { it.id }.mapValues { it.value[0].position.toInt() }
            }.flowOn(schedulers.cpu)

    // TODO separate track/podcast order??
    val sortOrder: SortEntity
        get() = appPreferencesUseCase.getAllTracksSort()

    val data: Flow<List<TrackFragmentItem>>
        get() {
            return if (isPodcast) {
                trackGateway.observeAllPodcasts()
                    .mapListItem { it.toPodcast() }
            } else {
                trackGateway.observeAllTracks()
                    .mapListItem { it.toTrack() }
                    .map { it.startWithIfNotEmpty(Shuffle) }
            }
        }


    private fun Song.toTrack(): TrackFragmentItem.Track {
        return TrackFragmentItem.Track(
            mediaId = presentationId,
            title = title,
            subtitle = "${artist}$MIDDLE_DOT_SPACED${album}"
        )
    }

    private fun Song.toPodcast(): TrackFragmentItem.Podcast {
        return TrackFragmentItem.Podcast(
            mediaId = presentationId,
            title = title,
            subtitle = "${artist}$MIDDLE_DOT_SPACED${album}",
            duration = TimeUnit.MILLISECONDS.toMinutes(duration)
        )
    }

}