package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import dev.olog.msc.data.entity.MiniQueueEntity
import dev.olog.msc.data.entity.PlayingQueueEntity
import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.entity.Podcast
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.playing.queue.UpdatePlayingQueueUseCaseRequest
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.*
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers

@Dao
abstract class PlayingQueueDao {

    @Query("""
        SELECT * FROM playing_queue
        ORDER BY progressive
    """)
    internal abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract fun deleteAllImpl()

    @Query("""
        SELECT *
        FROM mini_queue
        ORDER BY timeAdded
    """)
    internal abstract fun getMiniQueueImpl(): Flowable<List<MiniQueueEntity>>

    fun observeMiniQueue(songList: Single<List<Song>>, podcastList: Single<List<Podcast>>)
            : Observable<List<PlayingQueueSong>> {


        return getMiniQueueImpl()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .flatMapSingle { ids ->  Singles.zip(songList, podcastList) { songList, podcastList ->
                    ids.map { it.id }
                            .mapNotNull { id ->
                                val song = songList.firstOrNull { it.id == id }
                                if (song == null){
                                    podcastList.firstOrNull { it.id == id }
                                } else {
                                    song
                                }
                            }.mapNotNull { song ->
                                if (song is Song){ // songs
                                    val pos = ids.indexOfFirst { it.id == song.id }
                                    val item = ids[pos]
//                                    TODO
                                    song.toPlayingQueueSong(item.idInPlaylist, MediaIdCategory.SONGS.toString(), "")
                                } else if (song is Podcast){ // podcast
                                    val pos = ids.indexOfFirst { it.id == song.id }
                                    val item = ids[pos]
//                                    TODO
                                    song.toPlayingQueueSong(item.idInPlaylist, MediaIdCategory.PODCASTS.toString(), "")
                                } else null
                            }

                } }
    }

    @Transaction
    open fun updateMiniQueue(list: List<Pair<Int, Long>>) {
        deleteMiniQueueImpl()
        insertMiniQueueImpl(list.map { MiniQueueEntity(it.first, it.second, System.nanoTime()) })
    }

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    @Query("DELETE FROM mini_queue")
    internal abstract fun deleteMiniQueueImpl()

    @Insert
    internal abstract fun insertMiniQueueImpl(list: List<MiniQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>, podcastList: Single<List<Podcast>>)
            : Observable<List<PlayingQueueSong>> {

        return this.getAllImpl()
                .toObservable()
                .flatMapSingle { ids ->  Singles.zip(songList, podcastList) { songList, podcastList ->
                    ids.map { it.songId }
                            .mapNotNull { id ->
                                val song = songList.firstOrNull { it.id == id }
                                if (song == null){
                                    podcastList.firstOrNull { it.id == id }
                                } else {
                                    song
                                }
                            }.mapNotNull { song ->
                                if (song is Song){
                                    val pos = ids.indexOfFirst { it.songId == song.id }
                                    val item = ids[pos]
                                    song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                                } else if (song is Podcast){
                                    val pos = ids.indexOfFirst { it.songId == song.id }
                                    val item = ids[pos]
                                    song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                                } else null
                            }

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
                    ) }
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
                this.image,
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
                this.image,
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