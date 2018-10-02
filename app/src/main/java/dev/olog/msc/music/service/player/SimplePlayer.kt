package dev.olog.msc.music.service.player

//class SimplePlayer @Inject constructor(
//        @ApplicationContext context: Context,
//        @ServiceLifecycle lifecycle: Lifecycle,
//        sourceFactory: DefaultSourceFactory,
//        volume: IPlayerVolume,
//        private val audioManager: Lazy<AudioManager>,
//        private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener
//
//): DefaultPlayer<MediaEntity>(context, lifecycle, sourceFactory, volume) {
//
//    init {
//        player.addListener(this)
//        player.addAudioDebugListener(onAudioSessionIdChangeListener)
//    }
//
//    override fun onDestroy(owner: LifecycleOwner) {
//        super.onDestroy(owner)
//        player.removeListener(this)
//        player.removeAudioDebugListener(onAudioSessionIdChangeListener)
//        onAudioSessionIdChangeListener.release()
//    }
//
//    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
////            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
//            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
//        }
//    }
//
//}