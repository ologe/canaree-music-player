package dev.olog.data.history

import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaUri
import dev.olog.core.history.HistoryGateway
import dev.olog.core.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.HistoryQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.playable.Podcast_episodes_view
import dev.olog.data.playable.Songs_view
import dev.olog.data.playable.toDomain
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class HistoryRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: HistoryQueries,
    private val dateTimeFactory: DateTimeFactory,
) : HistoryGateway {

    override fun getSongs(): List<Song> {
        return queries.selectAllSongs()
            .executeAsList()
            .map(Songs_view::toDomain)
    }

    override fun getPodcastEpisodes(): List<Song> {
        return queries.selectAllPodcastEpisodes()
            .executeAsList()
            .map(Podcast_episodes_view::toDomain)
    }

    override fun observeSongs(): Flow<List<Song>> {
        return queries.selectAllSongs()
            .mapToFlowList(schedulers.io)
            .mapListItem(Songs_view::toDomain)
    }

    override fun observePodcastEpisodes(): Flow<List<Song>> {
        return queries.selectAllPodcastEpisodes()
            .mapToFlowList(schedulers.io)
            .mapListItem(Podcast_episodes_view::toDomain)
    }

    override suspend fun insert(uri: MediaUri) = withContext(schedulers.io) {
        queries.insert(uri.id, dateTimeFactory.currentTimeMillis())
    }
}