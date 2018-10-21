package dev.olog.msc.presentation.main

import androidx.fragment.app.FragmentActivity
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener

class CastBehavior(activity: FragmentActivity) {

    private val castContext = CastContext.getSharedInstance(activity)

    private var castSession: CastSession? = null
    private lateinit var sessionManagerListener: SessionManagerListener<CastSession>

    fun initializeMediaButton(mediaRouteButton: MediaRouteButton){
//        CastButtonFactory.setUpMediaRouteButton(mediaRouteButton.context,  mediaRouteButton)
    }

    private fun setupCastListener(){
        sessionManagerListener = object : SessionManagerListener<CastSession> {

            override fun onSessionEnded(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                onApplicationConnected(session)
            }

            override fun onSessionResumeFailed(session: CastSession, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionStarted(session: CastSession, sessionId: String?) {
                onApplicationConnected(session)
            }

            override fun onSessionStartFailed(session: CastSession?, error: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionSuspended(session: CastSession, reason: Int) {}

            override fun onSessionStarting(session: CastSession) {}

            override fun onSessionResuming(session: CastSession, sessionId: String) {}

            override fun onSessionEnding(session: CastSession) {}
        }
    }

    private fun onApplicationConnected(session: CastSession){
        castSession = session

    }

    private fun onApplicationDisconnected(){

    }

    private fun buildMediaInfo() {

    }

}