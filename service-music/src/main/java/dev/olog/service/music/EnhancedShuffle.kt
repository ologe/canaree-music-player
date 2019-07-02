package dev.olog.service.music

import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.PlayerLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.shared.extensions.removeFirst
import javax.inject.Inject

@PerService
class EnhancedShuffle @Inject constructor(
    playerLifecycle: PlayerLifecycle

) {

    private var lastListened = mutableListOf<Long>()

    private val playerListener = object : PlayerLifecycle.Listener {
        override fun onMetadataChanged(metadata: MetadataEntity) {
            val mediaEntity = metadata.entity
            lastListened.removeFirst { it == mediaEntity.id }
            lastListened.add(0, mediaEntity.id)
        }
    }

    init {
        playerLifecycle.addListener(playerListener)
    }

    fun shuffle(list: MutableList<MediaEntity>): List<MediaEntity> {
        val halfListSize = list.size / 2

        lastListened = lastListened.take(halfListSize).toMutableList()

        val shuffled = list.shuffled().toMutableList()

        for (l in lastListened.reversed()) {
            val index = shuffled.indexOfFirst { it.id == l }
            if (index in 0..halfListSize) {
                val item = shuffled[index]
                shuffled.removeAt(index)
                shuffled.add(item)
            }
        }

        return shuffled
    }

}