package dev.olog.msc.dagger

import dagger.Module

@Module(
    includes = [
        dev.olog.feature.about.dagger.FeatureAboutDagger.AppModule::class,
		dev.olog.feature.detail.dagger.FeatureDetailDagger.AppModule::class,
		dev.olog.feature.library.dagger.FeatureLibraryDagger.AppModule::class,
		dev.olog.feature.onboarding.dagger.FeatureOnboardingDagger.AppModule::class,
		dev.olog.feature.player.dagger.FeaturePlayerDagger.AppModule::class,
		dev.olog.feature.player.mini.FeatureMiniPlayerDagger.AppModule::class,
		dev.olog.feature.queue.dagger.FeaturePlayingQueueDagger.AppModule::class,
		dev.olog.feature.search.dagger.FeatureSearchDagger.AppModule::class,
		dev.olog.feature.service.floating.di.FeatureFloatingWindowDagger.AppModule::class,
		dev.olog.feature.service.music.di.FeatureMusicServiceDagger.AppModule::class,
		dev.olog.feature.settings.dagger.FeatureSettingsDagger.AppModule::class,
		dev.olog.presentation.playlist.chooser.di.FeaturePlaylistChooserDagger.AppModule::class,
		dev.olog.presentation.main.di.FeatureMainActivityDagger.AppModule::class
    ]
)
class FeaturesModule
