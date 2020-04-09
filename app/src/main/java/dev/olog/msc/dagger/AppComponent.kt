package dev.olog.msc.dagger

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dev.olog.analytics.AnalyticsModule
import dev.olog.data.di.*
import dev.olog.feature.service.floating.di.FeatureFloatingWindowDagger
import dev.olog.feature.service.music.di.FeatureMusicServiceDagger
import dev.olog.lib.equalizer.EqualizerModule
import dev.olog.lib.image.loader.di.LibImageLoaderDagger
import dev.olog.msc.app.App
import dev.olog.msc.appwidgets.WidgetBindingModule
import dev.olog.msc.schedulers.SchedulersModule
import dev.olog.msc.viewmodel.ViewModelModule
import dev.olog.navigation.dagger.NavigationModule
import dev.olog.presentation.main.di.FeatureMainActivityDagger
import dev.olog.presentation.playlist.chooser.di.FeaturePlaylistChooserDagger
import dev.olog.shared.android.theme.ThemeModule
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        SchedulersModule::class,
        ViewModelModule::class,

        NavigationModule::class,

        // libs
        LibImageLoaderDagger.AppModule::class,

        // feature
        FeatureFloatingWindowDagger.AppModule::class,
        FeatureMusicServiceDagger.AppModule::class,
        FeaturePlaylistChooserDagger.AppModule::class,
        FeatureMainActivityDagger.AppModule::class,

        NetworkModule::class,
        ServiceModule::class,
        AnalyticsModule::class,

        RepositoryHelperModule::class,
        PreferenceModule::class,
        DataModule::class,
        EqualizerModule::class,

        AndroidInjectionModule::class,
        WidgetBindingModule::class,
        ThemeModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<App> {


    @Component.Factory
    interface Factory : AndroidInjector.Factory<App>

}