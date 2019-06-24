package dev.olog.msc.music.service.player.crossfade

import dev.olog.msc.music.service.model.PlayerMediaEntity
import dev.olog.msc.music.service.player.CustomExoPlayer
import dev.olog.shared.utils.assertMainThread
import javax.inject.Inject

private enum class CurrentPlayer {
    PLAYER_NOT_SET,
    PLAYER_ONE,
    PLAYER_TWO
}

class CrossFadePlayer @Inject internal constructor(
        private val playerOne: CrossFadePlayerImpl,
        private val playerTwo: CrossFadePlayerImpl

): CustomExoPlayer<PlayerMediaEntity> {

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

    private fun getNextPlayer(): CrossFadePlayerImpl? {
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

    private fun getCurrentPlayer(): CrossFadePlayerImpl? {
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            CurrentPlayer.PLAYER_NOT_SET -> null // it should not happen
        }
    }

    private fun getSecondaryPlayer(): CrossFadePlayerImpl? {
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerTwo
            CurrentPlayer.PLAYER_TWO -> playerOne
            CurrentPlayer.PLAYER_NOT_SET -> null // it should not happen
        }
    }

    private fun PlayerMediaEntity.toSimpleCrossFadeModel(): CrossFadePlayerImpl.Model {
        return CrossFadePlayerImpl.Model(this, false, -1)
    }

}