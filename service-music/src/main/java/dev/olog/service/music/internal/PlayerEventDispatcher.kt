package dev.olog.service.music.internal

import dev.olog.core.schedulers.Schedulers
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IPlayerLifecycle
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.player.PlayerImpl
import kotlinx.coroutines.withContext
import javax.inject.Inject

// simple ui thread delegate
internal class PlayerEventDispatcher @Inject constructor(
    private val schedulers: Schedulers,
    private val playerImpl: PlayerImpl
) : IPlayer, IPlayerLifecycle by playerImpl {

    override suspend fun prepare(playerModel: PlayerMediaEntity) = withContext(schedulers.main) {
        playerImpl.prepare(playerModel)
    }

    override suspend fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType) = withContext(schedulers.main) {
        playerImpl.playNext(playerModel, skipType)
    }

    override suspend fun play(playerModel: PlayerMediaEntity) = withContext(schedulers.main) {
        playerImpl.play(playerModel)
    }

    override suspend fun seekTo(millis: Long) = withContext(schedulers.main) {
        playerImpl.seekTo(millis)
    }

    override suspend fun resume() = withContext(schedulers.main) {
        playerImpl.resume()
    }

    override suspend fun pause(stopService: Boolean, releaseFocus: Boolean) = withContext(schedulers.main) {
        playerImpl.pause(stopService, releaseFocus)
    }

    override fun isPlaying(): Boolean {
        return playerImpl.isPlaying()
    }

    override fun getBookmark(): Long {
        return playerImpl.getBookmark()
    }

    override suspend fun forwardTenSeconds() = withContext(schedulers.main) {
        playerImpl.forwardTenSeconds()
    }

    override suspend fun replayTenSeconds() = withContext(schedulers.main) {
        playerImpl.replayTenSeconds()
    }

    override suspend fun forwardThirtySeconds() = withContext(schedulers.main) {
        playerImpl.forwardThirtySeconds()
    }

    override suspend fun replayThirtySeconds() = withContext(schedulers.main) {
        playerImpl.replayThirtySeconds()
    }

    override suspend fun stopService() = withContext(schedulers.main) {
        playerImpl.stopService()
    }

    override fun setVolume(volume: Float) {
        playerImpl.setVolume(volume)
    }

}