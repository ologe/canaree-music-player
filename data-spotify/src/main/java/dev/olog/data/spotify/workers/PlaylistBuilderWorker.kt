package dev.olog.data.spotify.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.olog.data.spotify.db.GeneratedPlaylistsDao
import dev.olog.data.spotify.db.SpotifyTracksDao
import dev.olog.data.spotify.entity.GeneratedPlaylistEntity
import dev.olog.lib.network.worker.ChildWorkerFactory
import kotlinx.coroutines.coroutineScope

class PlaylistBuilderWorker @AssistedInject constructor(
    @Assisted arg0: Context,
    @Assisted arg1: WorkerParameters,
    private val spotifyTracksDao: SpotifyTracksDao,
    private val generatedPlaylistsDao: GeneratedPlaylistsDao
) : CoroutineWorker(arg0, arg1) {

    companion object {
        const val TAG = "PlaylistBuilderWorker"
    }

    override suspend fun doWork(): Result = coroutineScope {
        generatedPlaylistsDao.clearPlaylists()

        val audioFeatures = spotifyTracksDao.getTracksAudioFeature()

//        drawAudioFeature("acousticness", audioFeatures.map { it.acousticness })
//        drawAudioFeature("danceability", audioFeatures.map { it.danceability })
//        drawAudioFeature("energy", audioFeatures.map { it.energy })
//        drawAudioFeature("instrumentalness", audioFeatures.map { it.instrumentalness })
//        drawAudioFeature("liveness", audioFeatures.map { it.liveness })
//        drawAudioFeature("loudness", audioFeatures.map { it.loudness })
//        drawAudioFeature("speechiness", audioFeatures.map { it.speechiness })
//        drawAudioFeature("tempo", audioFeatures.map { it.tempo })
//        drawAudioFeature("valence", audioFeatures.map { it.valence })

        // TODO made n categories, shown 4 for each
        val dance = GeneratedPlaylistEntity(
            title = "Vamos a bailar",
            tracks = audioFeatures.filter { it.danceability > 0.75 }.map { it.localId }
        )
        val heavyAndLoud = GeneratedPlaylistEntity(
            title = "Heavy and Loud",
            tracks = audioFeatures.filter {
                it.energy > 0.75 && it.loudness > -5f
            }.map { it.localId }
        )

        val instrumental = GeneratedPlaylistEntity(
            title = "Instrumental",
            tracks = audioFeatures.filter {
                it.instrumentalness > 0.75f
            }.map { it.localId }
        )

        val live = GeneratedPlaylistEntity(
            title = "Live",
            tracks = audioFeatures.filter {
                it.liveness >= 0.8f
            }.map { it.localId }
        )

        val chill = GeneratedPlaylistEntity(
            title = "Chill",
            tracks = audioFeatures.filter {
                it.valence <= .25f
            }.map { it.localId }
        )

        val electronic = GeneratedPlaylistEntity(
            title = "Electronic",
            tracks = audioFeatures.filter {
                it.tempo in (120f..140f) && it.speechiness < 0.33f
            }.map { it.localId }
        )

        val acoustic = GeneratedPlaylistEntity(
            title = "Acoustic",
            tracks = audioFeatures.filter {
                it.acousticness > 0.9f
            }.map { it.localId }
        )

        generatedPlaylistsDao.createPlaylists(
            listOf(
                dance,
                heavyAndLoud,
                instrumental,
                live,
                chill,
                electronic,
                acoustic
            )
        )

        Result.success()
    }

    private fun drawAudioFeature(category: String, list: List<Double>) {
        println("xx category $category")
        val current = list
            .sortedBy { it }
            .map { it * 10f }
            .map { it.toInt() }
            .groupBy { it }
            .map { it.key to it.value.size }
        println("xx $current")
        println("\nxx *********\n")
    }

    @AssistedInject.Factory
    internal interface Factory : ChildWorkerFactory

}