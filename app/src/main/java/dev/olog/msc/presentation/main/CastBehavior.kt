package dev.olog.msc.presentation.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener

class CastBehavior(activity: FragmentActivity): DefaultLifecycleObserver {

    init {
//        activity.lifecycle.addObserver(this)
    }

//    private val castContext = CastContext.getSharedInstance(activity)
//    private val sessionManager = castContext.sessionManager
//
//    private var castSession: CastSession? = null
//    private val sessionManagerListener by lazyFast { CastSessionManager(
//            { onApplicationConnected(it) },
//            { onApplicationDisconnected() }
//    ) }

    override fun onResume(owner: LifecycleOwner) {
//        castSession = sessionManager.currentCastSession
//        sessionManager.addSessionManagerListener(sessionManagerListener as SessionManagerListener<Session>)
    }

    override fun onPause(owner: LifecycleOwner) {
//        sessionManager.removeSessionManagerListener(sessionManagerListener as SessionManagerListener<Session>)
//        castSession = null
    }

    fun initializeMediaButton(mediaRouteButton: MediaRouteButton){
        mediaRouteButton.visibility = View.GONE
//        CastButtonFactory.setUpMediaRouteButton(mediaRouteButton.context,  mediaRouteButton)
    }

    private fun onApplicationConnected(session: CastSession){
//        castSession = session
    }

    private fun onApplicationDisconnected(){
//        castSession = null
    }

}

class CastSessionManager(
        private val onAppConnected: ((CastSession) -> Unit),
        private val onAppDisconnected: (() -> Unit)

) : SessionManagerListener<CastSession> {

    override fun onSessionEnded(session: CastSession, error: Int) {
        onAppDisconnected()
    }

    override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
        onAppConnected(session)
    }

    override fun onSessionResumeFailed(session: CastSession, error: Int) {
        onAppDisconnected()
    }

    override fun onSessionStarted(session: CastSession, sessionId: String?) {
        onAppConnected(session)
    }

    override fun onSessionStartFailed(session: CastSession?, error: Int) {
        onAppDisconnected()
    }

    override fun onSessionSuspended(session: CastSession, reason: Int) {}

    override fun onSessionStarting(session: CastSession) {}

    override fun onSessionResuming(session: CastSession, sessionId: String) {}

    override fun onSessionEnding(session: CastSession) {}
}