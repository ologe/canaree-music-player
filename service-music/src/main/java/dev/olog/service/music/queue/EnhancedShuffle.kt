package dev.olog.service.music.queue

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.DateTimeGenerator
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.player.InternalPlayerState
import dev.olog.shared.removeFirst
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import kotlin.random.Random

@ServiceScoped
internal class EnhancedShuffle @Inject constructor(
    lifecycleOwner: LifecycleOwner,
    playerState: InternalPlayerState,
    private val dateTimeGenerator: DateTimeGenerator,
    private val random: Random,
) {

    companion object {
        private const val MAX_SIZE = 200
    }

    private data class Model(
        val entity: MediaEntity,
        val timestamp: Long,
    )

    /**
     * lastListened[listening_now, ,.., listened_time_ago]
     */
    private var lastListened = ArrayDeque<Model>(MAX_SIZE)

    init {
        playerState.state
            .map { it.entity }
            .distinctUntilChanged()
            .mapLatest(this::onTrackChanged)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    private fun onTrackChanged(entity: MediaEntity) {
        lastListened.removeFirst { it.entity.id == entity.id }

        if (lastListened.size >= MAX_SIZE) {
            lastListened.removeLast()
        }

        val model = Model(
            entity = entity,
            timestamp = dateTimeGenerator.now()
        )
        lastListened.addFirst(model)
    }

    /*
     * The algorithm moves at the end of the queue recently played songs (found in [lastListened])
     */
    operator fun invoke(list: List<MediaEntity>): List<MediaEntity> {
        if (list.isEmpty()) {
            return list
        }

        // get a lastListened copy and keep only the items that are present in [list]
        val lastListenedCopy = lastListened.toMutableList().apply { retainAll { it.entity in list } }
        val lastListenedMap = lastListenedCopy.groupBy { it.entity.id }

        val shuffle = list.shuffled(random)

        val (neverPlayedTracks, alreadyPlayed) = shuffle.partition { !lastListenedMap.containsKey(it.id) }

        val alreadyPlayedSorted = alreadyPlayed.asSequence()
            .map { it to lastListenedMap[it.id]?.first() }
            .sortedBy { it.second?.timestamp ?: 0 }
            .map { it.first }
            .toList()

        return neverPlayedTracks +
            alreadyPlayedSorted.take(alreadyPlayedSorted.size / 2).shuffled(random) + // shuffle the first half
            alreadyPlayedSorted.drop(alreadyPlayedSorted.size / 2) // second half in reverse played order
    }

}