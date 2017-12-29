package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import dev.olog.data.entity.PlayingQueueEntity
import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.Song
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Dao
abstract class PlayingQueueDao {

    @Query("SELECT * FROM playing_queue ORDER BY progressive")
    internal abstract fun getAllImpl(): Flowable<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    internal abstract fun deleteAllImpl()

    @Insert
    internal abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    fun getAllAsSongs(songList: Single<List<Song>>): Flowable<List<PlayingQueueSong>> {

        return this.getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle { ids -> songList.flatMap { songs ->
                    val result : List<PlayingQueueSong> = ids.asSequence()
                            .map { it.songId }
                            .map { id -> songs.firstOrNull { it.id == id } }
                            .filter { it != null }
                            .map { it!! }
                            .map { song ->
                                val pos = ids.indexOfFirst { it.songId == song.id }
                                val item = ids[pos]
                                song.toPlayingQueueSong(item.category, item.categoryValue)
                            }
                            .toList()
                    Single.just(result)
                } }
    }

    fun insert(list: List<Pair<MediaId, Long>>) : Completable {

        return Single.fromCallable { deleteAllImpl() }
                .map { list.map {
                    val (mediaId, songId) = it
                    PlayingQueueEntity(
                            songId = songId,
                            category = mediaId.category.toString(),
                            categoryValue = mediaId.categoryValue)
                } }.flatMapCompletable { queueList -> CompletableSource { insertAllImpl(queueList) } }
    }

    private fun Song.toPlayingQueueSong(category: String, categoryValue: String): PlayingQueueSong {
        return PlayingQueueSong(
                this.id,
                MediaId.createCategoryValue(MediaIdCategory.valueOf(category), categoryValue),
                this.artistId,
                this.albumId,
                this.title,
                this.artist,
                this.album,
                this.image,
                this.duration,
                this.dateAdded,
                this.isRemix,
                this.isExplicit,
                this.path,
                this.folder,
                this.trackNumber
        )
    }

}