package dev.olog.data.db

import android.arch.persistence.room.*
import dev.olog.data.entity.RecentSearchesEntity
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.Song
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class RecentSearchesDao {

    companion object {
        const val SONG = 0
        const val ARTIST = 1
        const val ALBUM = 2
    }

    @Query("SELECT * " +
            "from recent_searches " +
            "ORDER BY insertionTime DESC "+
            "LIMIT 50")
    internal abstract fun getAllImpl(): Flowable<List<RecentSearchesEntity>>

    fun getAll(songList: Single<List<Song>>,
               albumList: Single<List<Album>>,
               artistList: Single<List<Artist>>) : Flowable<List<SearchResult>> {

        return getAllImpl()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle {  it.toFlowable().flatMapMaybe { recentEntity ->
                        when (recentEntity.dataType){
                            SONG -> songList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchSongMapper(recentEntity, it) }
                                    .firstElement()
                            ALBUM -> albumList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchAlbumMapper(recentEntity, it) }
                                    .firstElement()
                            ARTIST -> artistList.flattenAsFlowable { it }
                                    .filter { it.id == recentEntity.itemId }
                                    .map { searchArtistMapper(recentEntity, it) }
                                    .firstElement()
                            else -> throw IllegalArgumentException("invalid recent element type ${recentEntity.dataType}")
                        } }.toList()
                }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertImpl(recent: RecentSearchesEntity)

    @Delete
    abstract fun deleteImpl(recentSearch: RecentSearchesEntity)

    @Query("DELETE FROM recent_searches WHERE dataType = :dataType AND itemId = :itemId")
    abstract fun deleteImpl(dataType: Int, itemId: Long): RecentSearchesEntity

    @Query("DELETE FROM recent_searches")
    abstract fun deleteAllImpl()

    fun delete(dataType: Int, itemId: Long): Completable {
        return Completable.fromCallable {
            deleteImpl(dataType, itemId)
        }.subscribeOn(Schedulers.io())
    }

    fun deleteAll(): Completable {
        return Completable.fromCallable { deleteAll() }
                .subscribeOn(Schedulers.io())
    }

    fun insertSong(song: Song): Completable{
        return Completable.fromCallable { insertImpl(
           RecentSearchesEntity(dataType = SONG, itemId = song.id)
        ) }.subscribeOn(Schedulers.io())
    }

    fun insertAlbum(album: Album): Completable{
        return Completable.fromCallable { insertImpl(
                RecentSearchesEntity(dataType = ALBUM, itemId = album.id)
        ) }.subscribeOn(Schedulers.io())
    }

    fun insertArtist(artist: Artist): Completable{
        return Completable.fromCallable { insertImpl(
                RecentSearchesEntity(dataType = ARTIST, itemId = artist.id)
        ) }.subscribeOn(Schedulers.io())
    }

    private fun searchSongMapper(recentSearch: RecentSearchesEntity, song: Song) : SearchResult {
        return SearchResult(MediaIdHelper.songId(song.id), recentSearch.dataType,
                song.title, song.image)
    }

    private fun searchAlbumMapper(recentSearch: RecentSearchesEntity, album: Album) : SearchResult {
        return SearchResult(MediaIdHelper.songId(album.id), recentSearch.dataType,
                album.title, album.image)
    }

    private fun searchArtistMapper(recentSearch: RecentSearchesEntity, artist: Artist) : SearchResult {
        return SearchResult(MediaIdHelper.songId(artist.id), recentSearch.dataType,
                artist.name, "")
    }

}