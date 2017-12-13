package dev.olog.presentation.service_music

import android.support.v4.media.session.MediaControllerCompat

interface MediaControllerProvider {

    fun getSupportMediaController(): MediaControllerCompat?
    fun setSupportMediaController(mediaController: MediaControllerCompat?)

}