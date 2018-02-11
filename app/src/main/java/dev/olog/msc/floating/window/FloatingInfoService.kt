package dev.olog.msc.floating.window

import android.app.Notification
import android.app.Service
import android.content.Intent
import dev.olog.msc.FirebaseAnalytics
import dev.olog.msc.floating.window.api.HoverMenu
import dev.olog.msc.floating.window.api.HoverView
import dev.olog.msc.utils.TextUtils
import javax.inject.Inject

class FloatingInfoService : BaseFloatingService() {

    @Inject lateinit var hoverMenu: CustomHoverMenu
    @Inject lateinit var notification : InfoNotification

    private var creationTime : Long? = null

    companion object {
        const val TAG = "FloatingInfoService"
        const val ACTION_STOP = "$TAG.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        creationTime = System.currentTimeMillis()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null && intent.action != null){
            when (intent.action){
                ACTION_STOP -> {
                    stopSelf()
                    return Service.START_NOT_STICKY
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        creationTime?.let {
            val lifeMillis = System.currentTimeMillis() - it
            val life = TextUtils.getReadableSongLength(lifeMillis)
            FirebaseAnalytics.trackFloatingServiceLife(life)
        }
    }

    override fun onHoverMenuLaunched(intent: Intent, hoverView: HoverView) {
        hoverView.setMenu(createHoverMenu())
        hoverView.collapse()

        hoverView.addOnExpandAndCollapseListener(onExpansionListener)
    }

    override fun onHoverMenuExitingByUserRequest() {
        super.onHoverMenuExitingByUserRequest()
        hoverView.removeOnExpandAndCollapseListener(onExpansionListener)
    }

    private val onExpansionListener = object : HoverView.Listener {
        override fun onCollapsing() {
            hoverMenu.sections.forEach { it.tabView.setHidden(true) }
        }

        override fun onExpanding() {
            hoverMenu.sections.forEach { it.tabView.setExpanded() }
        }

        override fun onExpanded() {
        }

        override fun onClosing() {
        }

        override fun onClosed() {
        }

        override fun onCollapsed() {
        }
    }

    override fun getForegroundNotificationId(): Int = InfoNotification.NOTIFICATION_ID

    override fun getForegroundNotification(): Notification {
        return notification.buildNotification()
    }

    private fun createHoverMenu() : HoverMenu = hoverMenu

}