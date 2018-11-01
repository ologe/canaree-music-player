package dev.olog.msc.indexing

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.firebase.appindexing.FirebaseAppIndex
import com.google.firebase.appindexing.builders.Indexables
import dagger.android.AndroidInjection
import dev.olog.msc.domain.interactor.all.*
import javax.inject.Inject

enum class IndexType{
    ALL, TRACKS, ARTISTS, ALBUMS, PLAYLIST, GENRE
}

class MusicIndexingUpdateService : JobIntentService() {

    companion object {
        private const val TAG = "MusicIndexingUpdateService"
        private const val UNIQUE_JOB_ID = 42
        private const val INDEX_TYPE = "$TAG.INDEX_TYPE"

        fun enqueueWork(context: Context, indexType: IndexType) {
            val intent = Intent().apply {
                putExtra(INDEX_TYPE, indexType.ordinal)
            }
            JobIntentService.enqueueWork(context, MusicIndexingUpdateService::class.java,
                    UNIQUE_JOB_ID, intent)
        }
    }

    @Inject lateinit var getAllAlbumsUseCase: GetAllAlbumsUseCase
    @Inject lateinit var getAllArtistsUseCase: GetAllArtistsUseCase
    @Inject lateinit var getAllSongsUseCase: GetAllSongsUseCase
    @Inject lateinit var getAllPlaylistUseCase: GetAllPlaylistsUseCase
    @Inject lateinit var getAllGenresUseCase: GetAllGenresUseCase

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleWork(intent: Intent) {
        return

        val indexType = IndexType.values()[intent.getIntExtra(INDEX_TYPE, IndexType.ALL.ordinal)]

        when (indexType){
            IndexType.ALL -> {
                indexAlbums()
                indexArtists()
                indexTracks()
                indexPlaylists()
                indexGenres()
            }
            IndexType.ALBUMS -> indexAlbums()
            IndexType.ARTISTS -> indexArtists()
            IndexType.PLAYLIST -> indexPlaylists()
            IndexType.GENRE -> indexGenres()
            IndexType.TRACKS -> indexTracks()
        }
    }

    private fun indexAlbums(){
        getAllAlbumsUseCase.execute()
                .firstOrError()
                .blockingGet()
                .map {
                    val artist = Indexables.musicGroupBuilder()
                            .setName(it.artist)

                    Indexables.musicAlbumBuilder()
                            .setName(it.title)
                            .setByArtist(artist)
                            .setNumTracks(it.songs)
                            .build()
                }
                .also {
                    FirebaseAppIndex.getInstance().update(*it.toTypedArray())
                }
    }

    private fun indexArtists(){
        getAllArtistsUseCase.execute()
                .firstOrError()
                .blockingGet()
                .map { Indexables.newSimple(it.name, "test") }
                .also {
                    FirebaseAppIndex.getInstance().update(*it.toTypedArray())
                }
    }

    private fun indexTracks(){

    }

    private fun indexPlaylists(){

    }

    private fun indexGenres(){

    }

}
