package dev.olog.msc.dagger

import dagger.Module
import dev.olog.feature.service.floating.di.FeatureFloatingWindowDagger
import dev.olog.feature.service.music.di.FeatureMusicServiceDagger
import dev.olog.presentation.main.di.FeatureMainActivityDagger
import dev.olog.presentation.playlist.chooser.di.FeaturePlaylistChooserDagger

@Module(
    includes = [
        FeatureFloatingWindowDagger.AppModule::class,
        FeatureMusicServiceDagger.AppModule::class,
        FeaturePlaylistChooserDagger.AppModule::class,
        FeatureMainActivityDagger.AppModule::class
    ]
)
abstract class FeaturesModule