package dev.olog.feature.media.api

import dev.olog.core.MediaId
import dev.olog.core.entity.sort.Sort
import dev.olog.feature.media.api.model.PlayerItem
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.feature.media.api.model.PlayerRepeatMode
import dev.olog.feature.media.api.model.PlayerShuffleMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal object DummyMediaProvider : MediaProvider {

    override fun observeMetadata(): Flow<PlayerMetadata> {
        return emptyFlow()
    }

    override fun observePlaybackState(): Flow<PlayerPlaybackState> {
        return emptyFlow()
    }

    override fun observeRepeat(): Flow<PlayerRepeatMode> {
        return emptyFlow()
    }

    override fun observeShuffle(): Flow<PlayerShuffleMode> {
        return emptyFlow()
    }

    override fun observeQueue(): Flow<List<PlayerItem>> {
        return emptyFlow()
    }

    override fun playFromMediaId(mediaId: MediaId, filter: String?, sort: Sort?) {
        
    }

    override fun playMostPlayed(mediaId: MediaId) {
        
    }

    override fun playRecentlyAdded(mediaId: MediaId) {
        
    }

    override fun skipToQueueItem(idInPlaylist: Int) {
        
    }

    override fun shuffle(mediaId: MediaId, filter: String?) {
        
    }

    override fun skipToNext() {
        
    }

    override fun skipToPrevious() {
        
    }

    override fun playPause() {
        
    }

    override fun seekTo(where: Long) {
        
    }

    override fun toggleShuffleMode() {
        
    }

    override fun toggleRepeatMode() {
        
    }

    override fun addToPlayNext(mediaId: MediaId) {
        
    }

    override fun togglePlayerFavorite() {
        
    }

    override fun swap(from: Int, to: Int) {
        
    }

    override fun swapRelative(from: Int, to: Int) {
        
    }

    override fun remove(position: Int) {
        
    }

    override fun removeRelative(position: Int) {
        
    }

    override fun moveRelative(position: Int) {
        
    }

    override fun replayTenSeconds() {
        
    }

    override fun forwardTenSeconds() {
        
    }

    override fun replayThirtySeconds() {
        
    }

    override fun forwardThirtySeconds() {
        
    }
}