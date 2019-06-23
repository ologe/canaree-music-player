package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.podcast.Podcast
import dev.olog.core.entity.track.Song
import dev.olog.data.db.entities.MiniQueueEntity
import dev.olog.data.db.entities.PlayingQueueEntity
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import io.reactivex.*
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers

@Dao
abstract class PlayingQueueDao {

    @Query("""
        SELECT * FROM playing_queue
        ORDER BY progressive
    """)
     abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
     abstract fun deleteAllImpl()

    @Query("""
        SELECT *
        FROM mini_queue
        ORDER BY timeAdded
    """)
     abstract fun getMiniQueueImpl(): Flowable<List<MiniQueueEntity>>

    fun observeMiniQueue(songList: Single<List<Song>>, podcastList: Single<List<Podcast>>)
            : Observable<List<PlayingQueueSong>> {


        return getMiniQueueImpl()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMapSingle { ids ->  Singles.zip(songList, podcastList) { songList, podcastList ->
                    val result = mutableListOf<PlayingQueueSong>()
                    for (item in ids){
                        var song : Any? = songList.firstOrNull { it.id == item.id }
                        if (song == null){
                            song = podcastList.firstOrNull { it.id == item.id }
                        }
                        if (song == null){
                            continue
                        }

                        val itemToAdd = if (song is Song){
                            song.toPlayingQueueSong(item.idInPlaylist, MediaIdCategory.SONGS.toString(), "")
                        } else if (song is Podcast){
                            song.toPlayingQueueSong(item.idInPlaylist, MediaIdCategory.SONGS.toString(), "")
                        } else {
                            throw IllegalArgumentException("must be song or podcast, passed $song")
                        }
                        result.add(itemToAdd)

                    }
                    result.toList()

                } }
    }

    @Transaction
    open fun updateMiniQueue(list: List<Pair<Int, Long>>) {
        deleteMiniQueueImpl()
        insertMiniQueueImpl(list.map {
            MiniQueueEntity(
                it.first,
                it.second,
                System.nanoTime()
            )
        })
    }

    @Insert
     abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    @Query("DELETE FROM mini_queue")
     abstract fun deleteMiniQueueImpl()

    @Insert
     abstract fun insertMiniQueueImpl(list: List<MiniQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>, podcastList: Single<List<Podcast>>)
            : Observable<List<PlayingQueueSong>> {

        return this.getAllImpl()
                .toObservable()
                .flatMapSingle { ids ->  Singles.zip(songList, podcastList) { songList, podcastList ->

                    val result = mutableListOf<PlayingQueueSong>()
                    for (item in ids){
                        var song : Any? = songList.firstOrNull { it.id == item.songId }
                        if (song == null){
                            song = podcastList.firstOrNull { it.id == item.songId }
                        }
                        if (song == null){
                            continue
                        }

                        val itemToAdd = if (song is Song){
                            song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                        } else if (song is Podcast){
                            song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                        } else {
                            throw IllegalArgumentException("must be song or podcast, passed $song")
                        }
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
            false
        )
    }

    private fun Podcast.toPlayingQueueSong(idInPlaylist: Int, category: String, categoryValue: String)
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
            true
        )
    }


}