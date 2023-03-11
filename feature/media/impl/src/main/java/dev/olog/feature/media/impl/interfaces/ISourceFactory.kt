package dev.olog.feature.media.impl.interfaces

import com.google.android.exoplayer2.source.MediaSource

interface ISourceFactory <T>{
    fun get(model: T) : MediaSource
}