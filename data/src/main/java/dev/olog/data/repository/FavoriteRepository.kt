package dev.olog.data.repository

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteEnum.FAVORITE
import dev.olog.core.entity.favorite.FavoriteEnum.NOT_FAVORITE
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.FavoritesQueries
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.playable.Podcast_episodes_view
import dev.olog.data.playable.Songs_view
import dev.olog.data.playable.toDomain
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FavoriteRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: FavoritesQueries,
) : FavoriteGateway {

    override fun observeToggleFavorite(): Flow<FavoriteEnum> {
        return queries.selectCurrentFavorite()
            .mapToFlowOneOrNull(schedulers.io)
            .map {
                if (it != null) FAVORITE else NOT_FAVORITE
            }
    }

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

    override suspend fun addSingle(playableId: Long) = withContext(schedulers.io) {
        queries.insert(playableId)
    }

    override suspend fun addGroup(playableIds: List<Long>) = withContext(schedulers.io) {
        queries.transaction {
            for (id in playableIds) {
                queries.insert(id)
            }
        }
    }

    override suspend fun deleteSingle(playableId: Long) = withContext(schedulers.io) {
        queries.delete(playableId)
    }

    override suspend fun deleteGroup(playableIds: List<Long>) = withContext(schedulers.io) {
        queries.transaction {
            for (id in playableIds) {
                queries.delete(id)
            }
        }
    }

    override suspend fun deleteAll(type: FavoriteType) = withContext(schedulers.io) {
        queries.clear(type.isPodcast())
    }

    override suspend fun isFavorite(playableId: Long): Boolean {
        return queries.isFavorite(playableId)
            .executeAsOneOrNull() != null
    }

    override suspend fun toggleFavorite() = withContext(schedulers.io) {
        queries.transaction {
            val current = queries.selectCurrentFavorite().executeAsOneOrNull()
            if (current != null) {
                queries.removePlayingItemFromFavorites()
            } else {
                queries.addPlayingItemToFavorites()
            }
        }
    }
}