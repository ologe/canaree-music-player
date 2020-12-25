package dev.olog.service.music.player.crossfade

import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.shared.android.utils.assertMainThread
import javax.inject.Inject

private enum class CurrentPlayer {
    PLAYER_NOT_SET,
    PLAYER_ONE,
    PLAYER_TWO
}

/**
 * Delegates calls to the right player [CrossFadePlayer]
 */
internal class CrossFadePlayerSwitcher @Inject internal constructor(
    private val playerOne: CrossFadePlayer,
    private val playerTwo: CrossFadePlayer

): IPlayerDelegate<PlayerMediaEntity> {

    private var current = CurrentPlayer.PLAYER_NOT_SET

    override fun prepare(model: PlayerMediaEntity, isTrackEnded: Boolean){
        assertMainThread()

        val player = getNextPlayer()
        player?.prepare(model.toSimpleCrossFadeModel(), isTrackEnded)

        if (!isTrackEnded){
            getSecondaryPlayer()?.stop()
        }
    }

    override var playbackSpeed: Float
        get() = getCurrentPlayer()?.playbackSpeed ?: 0f
        set(value) {
            getCurrentPlayer()?.playbackSpeed = value
            getSecondaryPlayer()?.playbackSpeed = value
        }

    override fun resume() {
        getCurrentPlayer()?.resume()
    }

    override fun pause() {
        getCurrentPlayer()?.pause()
        getSecondaryPlayer()?.stop()
    }

    override fun seekTo(where: Long) {
        getCurrentPlayer()?.seekTo(where)
        getSecondaryPlayer()?.stop()
    }

    override fun isPlaying(): Boolean = getCurrentPlayer()?.isPlaying() ?: false
    override fun getBookmark(): Long = getCurrentPlayer()?.getBookmark() ?: 0L
    override fun getDuration(): Long = getCurrentPlayer()?.getDuration() ?: 0L

    override fun setVolume(volume: Float) {
        getCurrentPlayer()?.setVolume(volume)
        getSecondaryPlayer()?.setVolume(volume)
    }

    private fun getNextPlayer(): CrossFadePlayer? {
        val current = when (current){
            CurrentPlayer.PLAYER_NOT_SET,
            CurrentPlayer.PLAYER_TWO -> CurrentPlayer.PLAYER_ONE
            CurrentPlayer.PLAYER_ONE -> CurrentPlayer.PLAYER_TWO
        }

        this.current = current

        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            CurrentPlayer.PLAYER_NOT_SET -> null // it should not happen
        }
    }

    private fun getCurrentPlayer(): CrossFadePlayer? {
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            CurrentPlayer.PLAYER_NOT_SET -> null // it should not happen
        }
    }

    private fun getSecondaryPlayer(): CrossFadePlayer? {
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerTwo
            CurrentPlayer.PLAYER_TWO -> playerOne
            CurrentPlayer.PLAYER_NOT_SET -> null // it should not happen
        }
    }

    private fun PlayerMediaEntity.toSimpleCrossFadeModel(): CrossFadePlayer.Model {
        return CrossFadePlayer.Model(this, false, -1)
    }

}