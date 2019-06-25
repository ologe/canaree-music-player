//package dev.olog.msc.music.service
//
//import android.arch.lifecycle.DefaultLifecycleObserver
//import android.arch.lifecycle.Lifecycle
//import android.arch.lifecycle.LifecycleOwner
//import android.content.Context
//import android.speech.tts.TextToSpeech
//import android.speech.tts.UtteranceProgressListener
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.MediaControllerCompat
//import android.support.v4.media.session.MediaSessionCompat
//import dev.olog.presentation.AppConstants
//import dev.olog.core.dagger.ApplicationContext
//import dev.olog.msc.dagger.qualifier.ServiceLifecycle
//import dev.olog.msc.dagger.scope.PerService
//import java.util.*
//import javax.inject.Inject
//
//@PerService
//class TrackSpeech @Inject constructor(
//        @ServiceLifecycle lifecycle: Lifecycle,
//        @ApplicationContext context: Context,
//        private val controller: MediaControllerCompat,
//        private val volumeFading: VolumeFading
//
//) : DefaultLifecycleObserver, TextToSpeech.OnInitListener, UtteranceProgressListener() {
//
//    private val UTTERANCE_ID = "track speech?"
//    private val speech = TextToSpeech(context, this)
//
//    private var lastSpoken = ""
//
//    init {
//        lifecycle.addObserver(this)
//        speech.setOnUtteranceProgressListener(this)
//    }
//
//    override fun onInit(status: Int) {
//        speech.language = Locale.UK
//    }
//
//    override fun onDestroy(owner: LifecycleOwner) {
//        speech.shutdown()
//    }
//
//    fun speak(){
//        val current = makeCurrentSpeech()
//        if (!speech.isSpeaking || lastSpoken != current){
//            speech.stop()
//            speech.speak(current, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
//            lastSpoken = current
//        }
//    }
//
//    private fun makeCurrentSpeech(): String {
//        val metadata = controller.metadata
//        val title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
//        val artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
//        return SpeechMetadata(title, artist).playerAppearance()
//    }
//
//    override fun onDone(utteranceId: String?) {
//        if (utteranceId == UTTERANCE_ID){
//            volumeFading.fadeIn()
//        }
//    }
//
//    override fun onStart(utteranceId: String?) {
//        if (utteranceId == UTTERANCE_ID){
//            volumeFading.fadeOut()
//        }
//    }
//
//    override fun onError(utteranceId: String?) {
//        if (utteranceId == UTTERANCE_ID){
//            volumeFading.fadeIn()
//        }
//    }
//}
//
//private class SpeechMetadata(
//        private val title: String,
//        private val artist: String
//) {
//
//    fun playerAppearance(): String {
//        if (artist == AppConstants.UNKNOWN_ARTIST){
//            return title
//        }
//        return "$title - $artist"
//                .replace("(?i)ft".toRegex(), " featuring ")
//                .replace("(?i)feat".toRegex(), " featuring ")
//    }
//
//}