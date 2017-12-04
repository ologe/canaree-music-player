package dev.olog.floating_info

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.view.ContextThemeWrapper
import io.mattcarroll.hover.HoverMenu
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.window.HoverMenuService
import javax.inject.Inject

class FloatingService : HoverMenuService() {

    companion object {
        fun showFloatingMenu(context: Context){
            val intent = Intent(context, FloatingService::class.java)
            context.startService(intent)
        }
    }

    @Inject lateinit var activityClass : ActivityClass

    override fun getContextForHoverMenu(): Context {
        return ContextThemeWrapper(this, R.style.AppTheme)
    }

    override fun onHoverMenuLaunched(intent: Intent, hoverView: HoverView) {
        hoverView.setMenu(createHoverMenu())
        hoverView.collapse()
    }

    override fun getForegroundNotificationId(): Int = 0xABC

    override fun getForegroundNotification(): Notification? {
        return NotificationCompat.Builder(this, "helper")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.vd_video)
                .setContentTitle("helper")
                .setContentText("helper text")
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        Intent(this, activityClass.get()),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .build()
    }

    private fun createHoverMenu() : HoverMenu {
        return CustomHoverMenu(applicationContext, "info menu id")
    }

}