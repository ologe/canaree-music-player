package dev.olog.flavor

import dagger.Module
import dev.olog.feature.library.dagger.FeatureLibraryDagger
import dev.olog.feature.onboarding.dagger.FeatureOnboardingDagger
import dev.olog.feature.player.dagger.FeaturePlayerDagger
import dev.olog.feature.player.mini.FeatureMiniPlayerDagger
import dev.olog.feature.queue.dagger.FeaturePlayingQueueDagger
import dev.olog.feature.search.dagger.FeatureSearchDagger
import dev.olog.feature.service.music.di.FeatureMusicServiceDagger
import dev.olog.presentation.main.di.FeatureMainActivityDagger
import dev.olog.presentation.playlist.chooser.di.FeaturePlaylistChooserDagger

@Module(
    includes = [
		FeatureLibraryDagger.AppModule::class,
		FeatureOnboardingDagger.AppModule::class,
		FeaturePlayerDagger.AppModule::class,
		FeatureMiniPlayerDagger.AppModule::class,
		FeaturePlayingQueueDagger.AppModule::class,
		FeatureSearchDagger.AppModule::class,
		FeatureMusicServiceDagger.AppModule::class,
		FeaturePlaylistChooserDagger.AppModule::class,
		FeatureMainActivityDagger.AppModule::class
    ]
)
class FeaturesModule
