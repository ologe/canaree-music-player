package dev.olog.msc.presentation.main.di

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.media.MediaProvider
import dev.olog.injection.dagger.ActivityContext
import dev.olog.msc.presentation.edit.EditItemViewModel
import dev.olog.presentation.main.MainActivityViewModel
import dev.olog.msc.presentation.navigator.NavigatorImpl
import dev.olog.presentation.pro.BillingImpl
import dev.olog.presentation.pro.IBilling
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.navigator.Navigator

@Module
abstract class MainActivityModule {

    @Binds
    @ActivityContext
    internal abstract fun provideContext(instance: MainActivity): Context

    @Binds
    internal abstract fun provideActivity(instance: MainActivity): Activity

    @Binds
    internal abstract fun provideSupportActivity(instance: MainActivity): AppCompatActivity

    @Binds
    internal abstract fun provideFragmentActivity(instance: MainActivity): FragmentActivity

    @Binds
    internal abstract fun provideMusicGlue(instance: MainActivity): MediaProvider

    @Binds
    @IntoMap
    @ViewModelKey(EditItemViewModel::class)
    internal abstract fun provideViewModel(viewModel: EditItemViewModel): ViewModel

    @Binds
    @PerActivity
    abstract fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @PerActivity
    abstract fun provideBilling(impl: BillingImpl): IBilling

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun proviewViewModel(impl: MainActivityViewModel): ViewModel

    @Module
    companion object {

        @Provides
        @JvmStatic
        @ActivityLifecycle
        internal fun provideLifecycle(instance: MainActivity): Lifecycle = instance.lifecycle

    }

}