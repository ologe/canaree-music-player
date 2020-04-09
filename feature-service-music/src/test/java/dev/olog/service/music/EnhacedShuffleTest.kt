package dev.olog.service.music

import com.nhaarman.mockitokotlin2.mock
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.queue.EnhancedShuffle
import dev.olog.service.music.shared.MusicServiceData
import dev.olog.test.shared.MainCoroutineRule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EnhacedShuffleTest {

    @get:Rule
    var coroutinesMainDispatcherRule = MainCoroutineRule()

    private val enhancedShuffle = EnhancedShuffle(mock())

    @Test
    fun `test shuffle with empty list`() = runBlockingTest {

        // given
        val queue = emptyList<MediaEntity>()

        val justPlayed = MusicServiceData.mediaEntityList(5)

        // when, after playing n songs
        for (metadataEntity in justPlayed) {
            enhancedShuffle.onMetadataChanged(MetadataEntity(metadataEntity, SkipType.NONE))
        }
        // shuffle
        val shuffled = enhancedShuffle.shuffle(queue.toMutableList())

        // then check if last plaeyd song are at the end of queue
        assertEquals(
            emptyList<MediaEntity>(), // expected
            shuffled                  // actual
        )
    }

    // something is not working well when 'just played' is more than half of new queue
    @Test
    fun `test shuffle`() = runBlockingTest {

        // given
        val queue = MusicServiceData.mediaEntityList(10)

        val justPlayed = queue.take(5)
        val notPlayedYet = queue.drop(justPlayed.size)
        // sanity check
        assertEquals(queue, justPlayed + notPlayedYet)

        // when, after playing n songs
        for (metadataEntity in justPlayed) {
            enhancedShuffle.onMetadataChanged(MetadataEntity(metadataEntity, SkipType.NONE))
        }
        // shuffle
        val shuffled = enhancedShuffle.shuffle(queue.toMutableList())

        // then check if last plaeyd song are at the end of queue
        assertEquals(
            justPlayed.take(justPlayed.size),
            shuffled.takeLast(justPlayed.size)
        )
    }

}