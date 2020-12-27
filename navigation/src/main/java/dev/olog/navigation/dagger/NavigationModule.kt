package dev.olog.navigation.dagger

import android.content.Intent
import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.Multibinds
import dev.olog.navigation.Navigator
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.navigation.destination.NavigationIntent
import dev.olog.navigation.internal.NavigatorImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    internal abstract fun provideNavigator(impl: NavigatorImpl): Navigator

    @Multibinds
    internal abstract fun fragments(): Map<FragmentScreen, @JvmSuppressWildcards Fragment>

    @Multibinds
    internal abstract fun navigationIntents(): Map<NavigationIntent, @JvmSuppressWildcards Intent>

}