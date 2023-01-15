package dev.olog.feature.media.api

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import dev.olog.feature.media.api.connection.IMediaConnectionCallback
import dev.olog.feature.media.api.connection.OnConnectionChanged
import dev.olog.feature.media.api.controller.IMediaControllerCallback
import dev.olog.feature.media.api.model.*
import dev.olog.shared.android.permission.PermissionManager
import kotlinx.coroutines.flow.Flow

interface MediaExposer :
    IMediaControllerCallback,
    IMediaConnectionCallback {

    val callback: MediaControllerCompat.Callback

    fun initialize(controller: MediaControllerCompat)
    fun connect()
    fun disconnect()

    fun observePlaybackState(): LiveData<PlayerPlaybackState>
    fun observeMetadata(): LiveData<PlayerMetadata>
    fun observeRepeat(): LiveData<PlayerRepeatMode>
    fun observeShuffle(): LiveData<PlayerShuffleMode>
    fun observeQueue(): Flow<List<PlayerItem>>

    interface Factory {
        fun create(
            context: Context,
            lifecycle: Lifecycle,
            onConnectionChanged: OnConnectionChanged,
            permissionManager: PermissionManager,
        ): MediaExposer
    }

}