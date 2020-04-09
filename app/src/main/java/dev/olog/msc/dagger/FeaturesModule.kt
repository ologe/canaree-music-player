package dev.olog.msc.dagger

import dagger.Module
import dev.olog.feature.library.dagger.FeatureLibraryDagger
import dev.olog.feature.search.dagger.FeatureSearchDagger
import dev.olog.feature.service.floating.di.FeatureFloatingWindowDagger
import dev.olog.feature.service.music.di.FeatureMusicServiceDagger
import dev.olog.presentation.main.di.FeatureMainActivityDagger
import dev.olog.presentation.playlist.chooser.di.FeaturePlaylistChooserDagger

@Module(
    includes = [
        FeatureLibraryDagger.AppModule::class,
        FeatureSearchDagger.AppModule::class,

        // legacy
        FeaturePlaylistChooserDagger.AppModule::class,
        FeatureMainActivityDagger.AppModule::class,

        // services
        FeatureFloatingWindowDagger.AppModule::class,
        FeatureMusicServiceDagger.AppModule::class
    ]
)
abstract class FeaturesModule