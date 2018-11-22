package dev.olog.msc.presentation.main.di

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.ActivityContext
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.dagger.scope.PerActivity
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.edit.EditItemViewModel
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.navigator.NavigatorImpl
import dev.olog.msc.pro.BillingImpl
import dev.olog.msc.pro.IBilling

@Module(includes = [MainActivityModule.Binding::class])
class MainActivityModule(
        private val activity: MainActivity
) {

    @Provides
    fun provideInstance() = activity

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    @ActivityLifecycle
    internal fun provideLifecycle(): Lifecycle = activity.lifecycle

    @Provides
    internal fun provideLifecycleOwner(): LifecycleOwner = activity

    @Provides
    internal fun provideActivity(): Activity = activity

    @Provides
    internal fun provideSupportActivity(): AppCompatActivity = activity

    @Provides
    internal fun provideFragmentActivity(): androidx.fragment.app.FragmentActivity = activity

    @Provides
    internal fun provideMusicGlue(): MediaProvider = activity

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(EditItemViewModel::class)
        fun provideViewModel(viewModel: EditItemViewModel): ViewModel

        @Binds
        @PerActivity
        fun provideNavigator(navigatorImpl: NavigatorImpl): Navigator

        @Binds
        @PerActivity
        fun provideBilling(impl: BillingImpl): IBilling

    }

}