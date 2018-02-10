package dev.olog.msc.module

import android.app.Service
import android.appwidget.AppWidgetProvider
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.floating.window.FloatingInfoService
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.presentation.app.widget.WidgetClassic
import dev.olog.msc.presentation.app.widget.WidgetColored
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.shortcuts.ShortcutsActivity
import dev.olog.shared_android.interfaces.*

@Module
class SharedClassModule {

    @Provides
    internal fun provideMainActivityClass(): MainActivityClass {
        return object : MainActivityClass {
            override fun get(): Class<out AppCompatActivity> {
                return MainActivity::class.java
            }
        }
    }

    @Provides
    internal fun provideMusicServiceClass(): MusicServiceClass {
        return object : MusicServiceClass {
            override fun get(): Class<out MediaBrowserServiceCompat> {
                return MusicService::class.java
            }
        }
    }

    @Provides
    internal fun provideFloatingInfoClass(): FloatingInfoServiceClass {
        return object : FloatingInfoServiceClass {
            override fun get(): Class<out Service> {
                return FloatingInfoService::class.java
            }
        }
    }

    @Provides
    internal fun provideShortcutActivityClass(): ShortcutActivityClass {
        return object : ShortcutActivityClass {
            override fun get(): Class<out AppCompatActivity> {
                return ShortcutsActivity::class.java
            }
        }
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