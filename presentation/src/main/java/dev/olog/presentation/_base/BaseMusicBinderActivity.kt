package dev.olog.presentation._base

import android.support.v4.media.MediaBrowserCompat
import dev.olog.presentation.service_music.RxMusicServiceConnectionCallback
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import javax.inject.Inject

abstract class BaseMusicBinderActivity : BaseActivity() {

    @Inject protected lateinit var mediaBrowser: MediaBrowserCompat
    @Inject protected lateinit var connectionCallback: RxMusicServiceConnectionCallback
    @Inject protected lateinit var mediaControllerCallback: RxMusicServiceControllerCallback

}