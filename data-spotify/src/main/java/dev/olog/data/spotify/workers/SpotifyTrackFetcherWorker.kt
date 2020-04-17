package dev.olog.data.spotify.workers

import android.content.Context
import android.provider.MediaStore.UNKNOWN_STRING
import androidx.work.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.olog.data.spotify.db.SpotifyTracksDao
import dev.olog.data.spotify.entity.SpotifyTrackEntity
import dev.olog.data.spotify.mapper.toEntity
import dev.olog.data.spotify.service.SpotifyService
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.lib.network.QueryNormalizer.normalize
import dev.olog.lib.network.retrofit.filter
import dev.olog.lib.network.retrofit.map
import dev.olog.lib.network.retrofit.orDefault
import dev.olog.lib.network.worker.ChildWorkerFactory
import dev.olog.shared.android.Permissions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

// TODO made a long running worker?? https://developer.android.com/topic/libraries/architecture/workmanager/advanced/long-running
internal class SpotifyTrackFetcherWorker @AssistedInject constructor(
    @Assisted arg0: Context,
    @Assisted arg1: WorkerParameters,
    private val workManager: WorkManager,
    private val trackGateway: TrackGateway,
    private val spotifyService: SpotifyService,
    private val spotifyTracksDao: SpotifyTracksDao
) : CoroutineWorker(arg0, arg1) {

    companion object {
        const val TAG = "SpotifyFetcherWorker"
        const val PROGRESS = "Progress"
        private const val DELAY = 200 // 10 request per second
    }

    override suspend fun doWork(): Result = coroutineScope {
        while (!Permissions.canReadStorage(applicationContext)) {
            delay(500)
        }

        var await = 0L

        setProgress(workDataOf(PROGRESS to 0))

        val alreadyDone = spotifyTracksDao.getTracks().map { it.localId }
        // TODO not very efficient filter
        val tracks = trackGateway.getAllTracks().filter { it.id !in alreadyDone }
        println("need to fetch ${tracks.size} tracks")

        val size =  tracks.size.toFloat()
        val counter = AtomicInteger(0)

        val itemsToAdd = Vector<SpotifyTrackEntity>()

        tracks.map {
                await += DELAY
                async {
                    delay(await)
                    val query = buildQuery(it)
                    val result = spotifyService.searchTrack(query)
                    val items = result
                        .map { it.tracks.items }
                        .filter { it.isNotEmpty() }
                        .orDefault(emptyList())
                    val current = counter.incrementAndGet()
                    val progress = current / size * 100f

                    setProgress(workDataOf(PROGRESS to progress.toInt()))

                    itemsToAdd.add(items.firstOrNull().toEntity(it))

                }
            }.awaitAll()

        setProgress(workDataOf(PROGRESS to 100))

        spotifyTracksDao.insertMultipleTrack(itemsToAdd)

        startFetchingAudioFeatures()

        Result.success()
    }


    // try without album is has all 3 fields and fails
    private fun buildQuery(track: Song): String {
        val artist = if (track.artist == UNKNOWN_STRING) "" else " artist:${track.artist}"
        val album = if (track.album == UNKNOWN_STRING || track.hasSameAlbumAsFolder) "" else " album:${track.album}"
        val title = track.title

        return "${normalize(title)}${normalize(artist)}${normalize(album)}"
    }

    private fun startFetchingAudioFeatures() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val work = OneTimeWorkRequestBuilder<SpotifyTrackAudioFeatureFetcherWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .addTag(SpotifyTrackAudioFeatureFetcherWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            SpotifyTrackAudioFeatureFetcherWorker.TAG,
            ExistingWorkPolicy.KEEP,
            work
        )

        // TODO await for work start to sync with progress??
    }

    @AssistedInject.Factory
    internal interface Factory : ChildWorkerFactory

}