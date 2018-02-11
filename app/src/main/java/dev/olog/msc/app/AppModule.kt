package dev.olog.msc.app

import android.app.Application
import android.appwidget.AppWidgetProvider
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.presentation.app.widget.WidgetClassic
import dev.olog.msc.presentation.app.widget.WidgetColored

@Module
class AppModule(private val app: App) {

    @Provides
    @ApplicationContext
    fun provideContext() : Context = app

    @Provides
    fun provideResources(): Resources = app.resources

    @Provides
    fun provideApplication(): Application = app

    @Provides
    fun provideContentResolver(): ContentResolver = app.contentResolver

    @Provides
    @ProcessLifecycle
    fun provideAppLifecycle(): Lifecycle {
        return ProcessLifecycleOwner.get().lifecycle
    }

    @Provides
    internal fun provideWidgetsClasses() : WidgetClasses {
        return object : WidgetClasses {
            override fun get(): List<Class<out AppWidgetProvider>> {
                return listOf(
                        WidgetColored::class.java,
                        WidgetClassic::class.java
                )
            }
        }
    }

}