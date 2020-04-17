package dev.olog.data.spotify.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.olog.data.spotify.db.SpotifyTracksDao
import dev.olog.data.spotify.dto.RemoteSpotifyTrackAudioFeature
import dev.olog.data.spotify.entity.SpotifyTrackAudioFeatureEntity
import dev.olog.data.spotify.mapper.toEntity
import dev.olog.data.spotify.service.SpotifyService
import dev.olog.lib.network.retrofit.fix
import dev.olog.lib.network.worker.ChildWorkerFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

internal class SpotifyTrackAudioFeatureFetcherWorker @AssistedInject constructor(
    @Assisted arg0: Context,
    @Assisted arg1: WorkerParameters,
    private val spotifyService: SpotifyService,
    private val spotifyTracksDao: SpotifyTracksDao
) : CoroutineWorker(arg0, arg1) {

    companion object {
        const val TAG = "SpotifyTrackAudioFeatureFetcherWorker"
        const val PROGRESS = "Progress"
        private const val DELAY = 200 // 10 request per second
    }

    override suspend fun doWork(): Result = coroutineScope {

        var await = 0L

        val alreadyDone = spotifyTracksDao.getTracksAudioFeature()
            .map { it.localId }

        val tracks = spotifyTracksDao.getValidTracks()
            .filter { it.localId !in alreadyDone }

        println("need to fetch ${tracks.size} audio features")
        val size =  tracks.size.toFloat()
        val counter = AtomicInteger(0)

        val itemsToAdd = Vector<SpotifyTrackAudioFeatureEntity>()

        tracks.map {
            await += DELAY
            async {
                delay(await)
                val audioFeature = spotifyService.getTrackAudioFeature(it.spotifyId)
                    .fix(RemoteSpotifyTrackAudioFeature.EMPTY)

                val current = counter.incrementAndGet()
                val progress = current / size * 100f

                setProgress(workDataOf(SpotifyTrackFetcherWorker.PROGRESS to progress.toInt()))

                itemsToAdd.add(audioFeature.toEntity(it))
            }
        }.awaitAll()

        spotifyTracksDao.insertMultipleTrackAudioFeature(itemsToAdd)

        Result.success()
    }

    @AssistedInject.Factory
    internal interface Factory : ChildWorkerFactory

}