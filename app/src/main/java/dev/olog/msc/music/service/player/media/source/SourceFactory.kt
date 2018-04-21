package dev.olog.msc.music.service.player.media.source

import com.google.android.exoplayer2.source.MediaSource

interface SourceFactory <T>{
    fun get(model: T) : MediaSource
}