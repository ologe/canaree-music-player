package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.data.db.entities.PlayingQueueEntity
import io.reactivex.*
import io.reactivex.rxkotlin.Singles

@Dao
abstract class PlayingQueueDao {

    @Query("""
        SELECT * FROM playing_queue
        ORDER BY progressive
    """)
     abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
     abstract fun deleteAllImpl()

    @Insert
     abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>, podcastList: Single<List<Song>>)
            : Observable<List<PlayingQueueSong>> {

        return this.getAllImpl()
                .toObservable()
                .flatMapSingle { ids ->  Singles.zip(songList, podcastList) { songList, podcastList ->

                    val result = mutableListOf<PlayingQueueSong>()
                    for (item in ids){
                        var song : Song? = songList.firstOrNull { it.id == item.songId }
                        if (song == null){
                            song = podcastList.firstOrNull { it.id == item.songId }
                        }
                        if (song == null){
                            continue
                        }

                        val itemToAdd = song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                        result.add(itemToAdd)

                    }
                    result.toList()

                } }
    }

    fun insert(list: List<UpdatePlayingQueueUseCaseRequest>) : Completable {

        return Single.fromCallable { deleteAllImpl() }
                .map { list.map {
                    val (mediaId, songId, idInPlaylist) = it
                    PlayingQueueEntity(
                        songId = songId,
                        category = mediaId.category.toString(),
                        categoryValue = mediaId.categoryValue,
                        idInPlaylist = idInPlaylist
                    )
                }
                }.flatMapCompletable { queueList -> CompletableSource { insertAllImpl(queueList) } }
    }

    private fun Song.toPlayingQueueSong(idInPlaylist: Int, category: String, categoryValue: String)
            : PlayingQueueSong {

        return PlayingQueueSong(
            this.id,
            idInPlaylist,
            MediaId.createCategoryValue(MediaIdCategory.valueOf(category), categoryValue),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.duration,
            this.dateAdded,
            this.path,
            this.folder,
            this.discNumber,
            this.trackNumber,
            this.isPodcast
        )
    }


}