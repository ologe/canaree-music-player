package dev.olog.msc.app

import android.app.AlarmManager
import android.app.Application
import android.appwidget.AppWidgetProvider
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.presentation.app.widget.defaul.WidgetColored
import dev.olog.msc.presentation.app.widget.queue.WidgetColoredWithQueue
import java.text.Collator
import java.util.*

@Module
class AppModule(private val app: App) {

    @Provides
    @ApplicationContext
    internal fun provideContext() : Context = app

    @Provides
    internal fun provideResources(): Resources = app.resources

    @Provides
    internal fun provideApplication(): Application = app

    @Provides
    internal fun provideContentResolver(): ContentResolver = app.contentResolver

    @Provides
    @ProcessLifecycle
    internal fun provideAppLifecycle(): Lifecycle {
        return ProcessLifecycleOwner.get().lifecycle
    }

    @Provides
    internal fun provideWidgetsClasses() : WidgetClasses {
        return object : WidgetClasses {
            override fun get(): List<Class<out AppWidgetProvider>> {
                return listOf(
                        WidgetColored::class.java,
                        WidgetColoredWithQueue::class.java
                )
            }
        }
    }

    @Provides
    fun provideConnectivityManager(): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideAlarmManager(): AlarmManager {
        return app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    fun provideCollator(): Collator {
        val instance = Collator.getInstance(Locale.UK)
        instance.strength = Collator.SECONDARY
//        instance.decomposition = Collator.CANONICAL_DECOMPOSITION
        return instance
    }

}