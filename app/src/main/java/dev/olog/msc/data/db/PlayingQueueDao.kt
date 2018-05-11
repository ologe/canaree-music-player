package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.PlayingQueueEntity
import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.playing.queue.UpdatePlayingQueueUseCaseRequest
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.*

@Dao
abstract class PlayingQueueDao {

    @Query("""
        SELECT * FROM playing_queue
        ORDER BY progressive
    """)
    internal abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract fun deleteAllImpl()

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>): Observable<List<PlayingQueueSong>> {
        return this.getAllImpl()
                .toObservable()
                .flatMapSingle { ids -> songList.flatMap { songs ->
                    val result : List<PlayingQueueSong> = ids
                            .map { it.songId }
                            .mapNotNull { id -> songs.firstOrNull { it.id == id } }
                            .map { song ->
                                val pos = ids.indexOfFirst { it.songId == song.id }
                                val item = ids[pos]
                                song.toPlayingQueueSong(item.idInPlaylist, item.category, item.categoryValue)
                            }
                    Single.just(result)
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

    private fun Song.toPlayingQueueSong(idInPlaylist: Int, category: String, categoryValue: String): PlayingQueueSong {
        return PlayingQueueSong(
                this.id,
                idInPlaylist,
                MediaId.createCategoryValue(MediaIdCategory.valueOf(category), categoryValue),
                this.artistId,
                this.albumId,
                this.title,
                this.artist,
                this.album,
                this.image,
                this.duration,
                this.dateAdded,
                this.path,
                this.folder,
                this.discNumber,
                this.trackNumber
        )
    }

}