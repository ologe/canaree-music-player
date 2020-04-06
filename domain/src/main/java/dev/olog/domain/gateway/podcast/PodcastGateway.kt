package dev.olog.domain.gateway.podcast

import dev.olog.domain.entity.PodcastPosition
import kotlinx.coroutines.flow.Flow

interface PodcastGateway {

    fun getCurrentPosition(podcastId: Long, duration: Long): Long
    fun observeAllCurrentPositions(): Flow<List<PodcastPosition>>
    fun saveCurrentPosition(podcastId: Long, position: Long)
}