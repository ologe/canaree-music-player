package dev.olog.shared_android.interfaces

import android.support.v4.media.MediaBrowserServiceCompat

interface MusicServiceClass {

    fun get(): Class<out MediaBrowserServiceCompat>

}