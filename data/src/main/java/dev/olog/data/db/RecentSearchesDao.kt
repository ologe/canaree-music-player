package dev.olog.data.db

import android.arch.persistence.room.*
import dev.olog.data.entity.RecentSearchesEntity
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.Song
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.RecentSearchesTypes.ALBUM
import dev.olog.shared.RecentSearchesTypes.ARTIST
import dev.olog.shared.RecentSearchesTypes.SONG
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Dao
abstract class RecentSearchesDao {

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
    abstract fun deleteImpl(dataType: Int, itemId: Long)

    @Query("DELETE FROM recent_searches")
    abstract fun deleteAllImpl()

    open fun deleteSong(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(SONG, itemId) }
                .subscribeOn(Schedulers.io())
    }

    open fun deleteAlbum(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(ALBUM, itemId) }
                .subscribeOn(Schedulers.io())
    }

    open fun deleteArtist(itemId: Long): Completable {
        return Completable.fromCallable { deleteImpl(ARTIST, itemId) }
                .subscribeOn(Schedulers.io())
    }

    open fun deleteAll(): Completable {
        return Completable.fromCallable { deleteAllImpl() }
                .subscribeOn(Schedulers.io())
    }

    open fun insertSong(songId: Long): Completable{
        return deleteSong(songId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = SONG, itemId = songId)) }
                .subscribeOn(Schedulers.io())
    }

    open fun insertAlbum(albumId: Long): Completable{
        return deleteAlbum(albumId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = ALBUM, itemId = albumId)) }
                .subscribeOn(Schedulers.io())
    }

    open fun insertArtist(artistId: Long): Completable{
        return deleteArtist(artistId)
                .andThen { insertImpl(RecentSearchesEntity(dataType = ARTIST, itemId = artistId)) }
                .subscribeOn(Schedulers.io())
    }

    private fun searchSongMapper(recentSearch: RecentSearchesEntity, song: Song) : SearchResult {
        return SearchResult(MediaIdHelper.songId(song.id), recentSearch.dataType,
                song.title, song.image)
    }

    private fun searchAlbumMapper(recentSearch: RecentSearchesEntity, album: Album) : SearchResult {
        return SearchResult(MediaIdHelper.albumId(album.id), recentSearch.dataType,
                album.title, album.image)
    }

    private fun searchArtistMapper(recentSearch: RecentSearchesEntity, artist: Artist) : SearchResult {
        return SearchResult(MediaIdHelper.artistId(artist.id), recentSearch.dataType,
                artist.name, "")
    }

}