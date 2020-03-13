package dev.olog.media

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import dev.olog.media.connection.MusicServiceConnectionState
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.model.*
import dev.olog.test.shared.MainCoroutineRule
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.junit.Rule

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
    private val sut = MediaExposer(
        context,
        onConnectionChanged,
        coroutineRule.testDispatcher.asSchedulers(),
        config
    )



}