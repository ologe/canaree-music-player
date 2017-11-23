package dev.olog.presentation.music_service

import android.support.v4.media.session.MediaControllerCompat

interface MediaControllerProvider {

    fun getSupportMediaController(): MediaControllerCompat?

}