package dev.olog.core.gateway.podcast

import dev.olog.core.entity.PodcastPosition
import kotlinx.coroutines.flow.Flow

interface PodcastGateway {

    fun getCurrentPosition(podcastId: Long, duration: Long): Long
    fun observeAllCurrentPositions(): Flow<List<PodcastPosition>>
    fun saveCurrentPosition(podcastId: Long, position: Long)
}