package dev.olog.feature.media.impl.player.crossfade

import dev.olog.feature.media.impl.interfaces.IPlayerDelegate
import dev.olog.feature.media.impl.model.PlayerMediaEntity
import dev.olog.shared.assertMainThread
import javax.inject.Inject

private enum class CurrentPlayer {
    PLAYER_NOT_SET,
    PLAYER_ONE,
    PLAYER_TWO
}

/**
 * Delegates calls to the right player [CrossFadePlayer]
 */
class CrossFadePlayerSwitcher @Inject constructor(
    private val playerOne: CrossFadePlayer,
    private val playerTwo: CrossFadePlayer

): IPlayerDelegate<PlayerMediaEntity> {

    private var current = CurrentPlayer.PLAYER_NOT_SET

    override fun prepare(mediaEntity: PlayerMediaEntity, bookmark: Long){
        assertMainThread()

        val player = getNextPlayer()
        player?.prepare(mediaEntity.toSimpleCrossFadeModel(), bookmark)
    }

    override fun setPlaybackSpeed(speed: Float) {
        getCurrentPlayer()?.setPlaybackSpeed(speed)
        getSecondaryPlayer()?.setPlaybackSpeed(speed)
    }

    override fun play(mediaEntity: PlayerMediaEntity, hasFocus: Boolean, isTrackEnded: Boolean) {
        assertMainThread()
        val player = getNextPlayer()
        player?.play(mediaEntity.toSimpleCrossFadeModel(), hasFocus, isTrackEnded)
        if (!isTrackEnded){
            getSecondaryPlayer()?.stop()
        }
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