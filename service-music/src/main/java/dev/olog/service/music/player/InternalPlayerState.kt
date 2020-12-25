package dev.olog.service.music.player

import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.SkipType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber
import javax.inject.Inject

// TODO rename
@ServiceScoped
internal class InternalPlayerState @Inject constructor(

) {

    private val _state = MutableStateFlow<Data?>(null)
    val state: Flow<Data>
        get() = _state.filterNotNull()

    fun prepare(
        entity: MediaEntity,
        positionInQueue: PositionInQueue,
        bookmark: Long,
        skipType: SkipType,
        isPlaying: Boolean,
    ) {
        Timber.i("prepare ${entity.id}, position=$positionInQueue, bookmark=$bookmark")
        _state.value = Data(
            entity = entity,
            skipType = skipType,
            state = Data.State(
                positionInQueue = positionInQueue,
                isPlaying = isPlaying,
                bookmark = bookmark,
            ),
        )
    }

    fun resume(
        bookmark: Long,
    ) {
        Timber.i("resume bookmark=$bookmark")
        val currentState = _state.value!! // TODO can assert??
        _state.value = Data(

            // keep the same
            entity = currentState.entity,
            skipType = currentState.skipType,
            state = Data.State(
                isPlaying = true,
                bookmark = bookmark,
                positionInQueue = currentState.state?.positionInQueue ?: PositionInQueue.IN_MIDDLE,
            ),
        )
    }

    fun pause(
        bookmark: Long
    ) {
        Timber.i("pause bookmark=$bookmark")
        val currentState = _state.value!! // TODO can assert??
        _state.value = Data(
            // keep the same
            entity = currentState.entity,
            skipType = currentState.skipType,
            state = Data.State(
                isPlaying = false,
                bookmark = bookmark,
                positionInQueue = currentState.state?.positionInQueue ?: PositionInQueue.IN_MIDDLE,
            ),
        )
    }

    internal data class Data(
        val entity: MediaEntity,
        val state: State?,
        val skipType: SkipType,
    ) {

        data class State(
            val isPlaying: Boolean,
            val bookmark: Long,
            val positionInQueue: PositionInQueue,
        )

    }

}
