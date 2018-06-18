package dev.olog.msc.presentation.app.widget.queue

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.android.AndroidInjection
import javax.inject.Inject

class QueueWidgetService : RemoteViewsService() {

    @Inject lateinit var factory: QueueRemoteViewsFactory

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return factory
    }


}