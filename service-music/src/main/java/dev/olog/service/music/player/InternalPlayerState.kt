package dev.olog.service.music.player

import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.SkipType
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO rename
@ServiceScoped
internal class InternalPlayerState @Inject constructor(

) {

    private val _state = MutableStateFlow<Data?>(null)
    val state: Flow<Data>
        get() = _state.filterNotNull()

    suspend fun prepare(
        entity: MediaEntity,
        positionInQueue: PositionInQueue,
        bookmark: Long,
    ) = withContext(NonCancellable) {
        _state.value = Data(
            prepare = true,
            entity = entity,
            positionInQueue = positionInQueue,
            isPlaying = false,
            bookmark = bookmark,
            skipType = SkipType.NONE,
        )
    }

    fun play(
        entity: MediaEntity,
        positionInQueue: PositionInQueue,
        skipType: SkipType,
        isPlaying: Boolean,
        bookmark: Long
    ) {
        _state.value = Data(
            prepare = false,
            entity = entity,
            positionInQueue = positionInQueue,
            isPlaying = isPlaying,
            bookmark = bookmark,
            skipType = skipType,
        )
    }

    fun resume(
        isPlaying: Boolean,
        bookmark: Long,
    ) {
        val currentState = _state.value!!
        _state.value = Data(
            prepare = false,
            isPlaying = isPlaying,
            bookmark = bookmark,
            // keep the same
            entity = currentState.entity,
            positionInQueue = currentState.positionInQueue,
            skipType = currentState.skipType,
        )
    }

    fun pause(
        bookmark: Long
    ) {
        val currentState = _state.value!!
        _state.value = Data(
            prepare = false,
            isPlaying = false,
            bookmark = bookmark,
            // keep the same
            entity = currentState.entity,
            positionInQueue = currentState.positionInQueue,
            skipType = currentState.skipType,
        )
    }

    internal data class Data(
        val prepare: Boolean,
        val entity: MediaEntity,
        val positionInQueue: PositionInQueue,
        val isPlaying: Boolean,
        val bookmark: Long,
        val skipType: SkipType,
    ) {
        
        fun isSameMetadata(other: Data): Boolean {
            return entity == other.entity && 
                positionInQueue == other.positionInQueue
        }

        fun isDifferentMetadata(other: Data): Boolean {
            return !isSameMetadata(other)
        } 
        
        fun isSameState(other: Data): Boolean {
            return prepare == other.prepare && 
                isPlaying == other.isPlaying &&
                bookmark == other.bookmark && 
                skipType == other.skipType
        }

        fun isDifferentState(other: Data): Boolean {
            return !isSameState(other)
        }
    }

}
