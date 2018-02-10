package dev.olog.msc.presentation

import android.support.v4.media.session.MediaControllerCompat

interface MediaControllerProvider {

    fun getSupportMediaController(): MediaControllerCompat?

}