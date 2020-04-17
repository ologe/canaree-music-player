package dev.olog.data.spotify.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dev.olog.data.spotify.workers.PlaylistBuilderWorker
import dev.olog.data.spotify.workers.SpotifyTrackAudioFeatureFetcherWorker
import dev.olog.data.spotify.workers.SpotifyTrackFetcherWorker
import dev.olog.lib.network.worker.ChildWorkerFactory
import dev.olog.lib.network.worker.WorkerKey

@AssistedModule
@Module(includes = [AssistedInject_SpotifyWorkersModule::class])
abstract class SpotifyWorkersModule {

    @Binds
    @IntoMap
    @WorkerKey(SpotifyTrackFetcherWorker::class)
    internal abstract fun bindTrackFetcher(factory: SpotifyTrackFetcherWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(SpotifyTrackAudioFeatureFetcherWorker::class)
    internal abstract fun bindTrackAudioFeatureFetcher(factory: SpotifyTrackAudioFeatureFetcherWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(PlaylistBuilderWorker::class)
    internal abstract fun bindGeneratePlaylists(factory: PlaylistBuilderWorker.Factory): ChildWorkerFactory

}