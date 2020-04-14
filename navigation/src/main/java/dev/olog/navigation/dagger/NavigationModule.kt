package dev.olog.navigation.dagger

import android.app.Activity
import android.app.Service
import android.appwidget.AppWidgetProvider
import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds
import dev.olog.navigation.Navigator
import dev.olog.navigation.NavigatorImpl
import dev.olog.navigation.screens.Activities
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.navigation.screens.Services
import dev.olog.navigation.screens.Widgets
import javax.inject.Singleton

@Module
abstract class NavigationModule {

    @Binds
    @Singleton
    internal abstract fun provideNavigator(impl: NavigatorImpl): Navigator

    @Multibinds
    internal abstract fun fragments(): Map<FragmentScreen, @JvmSuppressWildcards Fragment>

    @Multibinds
    internal abstract fun activityClasses(): Map<Activities, @JvmSuppressWildcards Class<out Activity>>

    @Multibinds
    internal abstract fun serviceClasses(): Map<Services, @JvmSuppressWildcards Class<out Service>>

    @Multibinds
    internal abstract fun widgetsClasses(): Map<Widgets, @JvmSuppressWildcards Class<out AppWidgetProvider>>

}