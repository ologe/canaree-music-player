package dev.olog.presentation.service_music

import android.support.v4.media.MediaBrowserServiceCompat

interface MusicServiceBinder {

    fun get(): Class<out MediaBrowserServiceCompat>

}