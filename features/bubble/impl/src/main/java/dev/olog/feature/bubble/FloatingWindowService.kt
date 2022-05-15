package dev.olog.feature.bubble

import android.app.Notification
import android.app.Service
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.bubble.api.FloatingWindowsConstants
import dev.olog.feature.bubble.api.HoverMenu
import dev.olog.feature.bubble.api.HoverView
import dev.olog.feature.bubble.api.window.HoverMenuService
import dev.olog.feature.bubble.notification.FloatingWindowNotification
import javax.inject.Inject

@AndroidEntryPoint
class FloatingWindowService : HoverMenuService() {

    @Inject
    lateinit var hoverMenu: CustomHoverMenu
    @Inject
    lateinit var notification: FloatingWindowNotification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == FloatingWindowsConstants.ACTION_STOP){
            stopSelf()
            return Service.START_NOT_STICKY
        }

        return super.onStartCommand(intent, flags, startId)
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

    override fun getForegroundNotificationId(): Int = FloatingWindowNotification.NOTIFICATION_ID

    override fun getForegroundNotification(): Notification {
        return notification.buildNotification()
    }

    private fun createHoverMenu(): HoverMenu = hoverMenu

}