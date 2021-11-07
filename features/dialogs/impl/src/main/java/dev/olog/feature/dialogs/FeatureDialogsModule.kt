package dev.olog.feature.dialogs

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class FeatureDialogsModule {

    @Binds
    @ActivityScoped
    abstract fun provideNavigator(impl: FeatureDialogsNavigatorImpl): FeatureDialogsNavigator

}