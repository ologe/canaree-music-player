package dev.olog.media

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import dev.olog.media.connection.MusicServiceConnectionState
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.model.*
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import dev.olog.test.shared.schedulers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// TODO made testable
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MediaExposerTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val context = mock<Context>()
    private val onConnectionChanged = mock<OnConnectionChanged>()

    private val connectionPublisher = ConflatedBroadcastChannel<MusicServiceConnectionState>()
    private val metadataPublisher = ConflatedBroadcastChannel<PlayerMetadata>()
    private val statePublisher = ConflatedBroadcastChannel<PlayerPlaybackState>()
    private val repeatModePublisher = ConflatedBroadcastChannel<PlayerRepeatMode>()
    private val shuffleModePublisher = ConflatedBroadcastChannel<PlayerShuffleMode>()
    private val queuePublisher = ConflatedBroadcastChannel<List<PlayerItem>>(emptyList())

    private val config = MediaExposer.Config(
        connectionPublisher,
        metadataPublisher,
        statePublisher,
        repeatModePublisher,
        shuffleModePublisher,
        queuePublisher
    )
    private lateinit var sut: MediaExposer

    @Test
    fun `test connect, no permission`() = coroutineRule.runBlockingTest {
        sut = buildSut(hasPermissionStorage = false)

        sut.connect()
    }

    @Test
    fun `test connect, success`() = coroutineRule.runBlockingTest {
        sut = buildSut()

        sut.connect()
    }

    private fun buildSut(hasPermissionStorage: Boolean = true): MediaExposer {
        return MediaExposer(
            context,
            onConnectionChanged,
            coroutineRule.schedulers,
            config
        ) { hasPermissionStorage }
    }

}