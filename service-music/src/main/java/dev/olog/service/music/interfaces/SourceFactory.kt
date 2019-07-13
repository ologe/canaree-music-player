package dev.olog.service.music.interfaces

import com.google.android.exoplayer2.source.MediaSource

internal interface SourceFactory <T>{
    fun get(model: T) : MediaSource
}