package dev.olog.msc

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.AppInitializer
import dev.olog.data.dagger.NetworkInterceptor
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlipperModule {

    @Binds
    @IntoSet
    @Singleton
    abstract fun provideFlipperInitializer(impl: FlipperInitializer): AppInitializer

    companion object {

        @Provides
        @Singleton
        fun provideFlipperNetworkPlugin(): NetworkFlipperPlugin {
            return NetworkFlipperPlugin()
        }

        @Provides
        @IntoSet
        @NetworkInterceptor
        @Singleton
        fun provideFlipperNetworkInterceptor(plugin: NetworkFlipperPlugin): Interceptor {
            return FlipperOkhttpInterceptor(plugin)
        }

    }

}