package dev.olog.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.olog.data.blacklist.db.BlacklistDao
import dev.olog.data.blacklist.db.BlacklistEntity
import dev.olog.data.db.equalizer.EqualizerPresetEntity
import dev.olog.data.db.equalizer.EqualizerPresetsDao
import dev.olog.data.db.history.HistoryDao
import dev.olog.data.db.history.HistoryEntity
import dev.olog.data.db.history.PodcastHistoryEntity
import dev.olog.data.db.lastfm.LastFmAlbumEntity
import dev.olog.data.db.lastfm.LastFmArtistEntity
import dev.olog.data.db.lastfm.LastFmDao
import dev.olog.data.db.lastfm.LastFmTrackEntity
import dev.olog.data.db.lyrics.LyricsSyncAdjustmentDao
import dev.olog.data.db.lyrics.LyricsSyncAdjustmentEntity
import dev.olog.data.db.lyrics.OfflineLyricsDao
import dev.olog.data.db.lyrics.OfflineLyricsEntity
import dev.olog.data.db.most.played.PlaylistMostPlayedDao
import dev.olog.data.db.most.played.PlaylistMostPlayedEntity
import dev.olog.data.db.playlist.PodcastPlaylistDao
import dev.olog.data.db.playlist.PodcastPlaylistEntity
import dev.olog.data.db.playlist.PodcastPlaylistTrackEntity
import dev.olog.data.db.queue.PlayingQueueDao
import dev.olog.data.db.queue.PlayingQueueEntity
import dev.olog.data.db.recent.search.RecentSearchesDao
import dev.olog.data.db.recent.search.RecentSearchesEntity
import dev.olog.data.favourites.FavoriteDao
import dev.olog.data.favourites.FavoriteEntity
import dev.olog.data.mediastore.MediaStoreAudioDao
import dev.olog.data.mediastore.MediaStoreAudioEntity
import dev.olog.data.mediastore.podcast.MediaStorePodcastsView
import dev.olog.data.mediastore.podcast.MediaStorePodcastsViewDao
import dev.olog.data.mediastore.podcast.MediaStorePodcastsViewSorted
import dev.olog.data.mediastore.podcast.PodcastPositionDao
import dev.olog.data.mediastore.podcast.PodcastPositionEntity
import dev.olog.data.mediastore.podcast.album.MediaStorePodcastAlbumsView
import dev.olog.data.mediastore.podcast.album.MediaStorePodcastAlbumsViewDao
import dev.olog.data.mediastore.podcast.album.MediaStorePodcastAlbumsViewSorted
import dev.olog.data.mediastore.podcast.artist.MediaStorePodcastArtistsView
import dev.olog.data.mediastore.podcast.artist.MediaStorePodcastArtistsViewDao
import dev.olog.data.mediastore.podcast.artist.MediaStorePodcastArtistsViewSorted
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.MediaStoreSongsViewDao
import dev.olog.data.mediastore.song.MediaStoreSongsViewSorted
import dev.olog.data.mediastore.song.album.MediaStoreAlbumsView
import dev.olog.data.mediastore.song.album.MediaStoreAlbumsViewDao
import dev.olog.data.mediastore.song.album.MediaStoreAlbumsViewSorted
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsView
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsViewDao
import dev.olog.data.mediastore.song.artist.MediaStoreArtistsViewSorted
import dev.olog.data.mediastore.song.folder.MediaStoreFoldersView
import dev.olog.data.mediastore.song.folder.MediaStoreFoldersViewDao
import dev.olog.data.mediastore.song.folder.MediaStoreFoldersViewSorted
import dev.olog.data.mediastore.song.genre.MediaStoreGenreDao
import dev.olog.data.mediastore.song.genre.MediaStoreGenreEntity
import dev.olog.data.mediastore.song.genre.MediaStoreGenreTrackEntity
import dev.olog.data.mediastore.song.genre.MediaStoreGenresView
import dev.olog.data.mediastore.song.genre.MediaStoreGenresViewDao
import dev.olog.data.mediastore.song.genre.MediaStoreGenresViewSorted
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistDao
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistEntity
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistTrackEntity
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistsView
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistsViewDao
import dev.olog.data.mediastore.song.playlist.MediaStorePlaylistsViewSorted
import dev.olog.data.playing.PlayingDao
import dev.olog.data.playing.PlayingEntity
import dev.olog.data.podcast.PodcastDao
import dev.olog.data.podcast.album.LastPlayedPodcastAlbumEntity
import dev.olog.data.podcast.album.PodcastAlbumDao
import dev.olog.data.podcast.artist.LastPlayedPodcastArtistEntity
import dev.olog.data.podcast.artist.PodcastArtistDao
import dev.olog.data.song.SongDao
import dev.olog.data.song.album.AlbumDao
import dev.olog.data.song.album.LastPlayedAlbumEntity
import dev.olog.data.song.artist.ArtistDao
import dev.olog.data.song.artist.LastPlayedArtistEntity
import dev.olog.data.song.folder.FolderDao
import dev.olog.data.song.folder.FolderMostPlayedEntity
import dev.olog.data.song.genre.GenreDao
import dev.olog.data.song.genre.GenreMostPlayedEntity
import dev.olog.data.song.playlist.LegacyPlaylistDao
import dev.olog.data.song.playlist.PlaylistEntity
import dev.olog.data.song.playlist.PlaylistTrackEntity
import dev.olog.data.sort.db.SortDao
import dev.olog.data.sort.db.SortEntity
import dev.olog.data.sort.db.SortTypeConverters


@Database(
    entities = [
        MediaStoreAudioEntity::class,
        MediaStoreGenreEntity::class,
        MediaStoreGenreTrackEntity::class,
        MediaStorePlaylistEntity::class,
        MediaStorePlaylistTrackEntity::class,

        BlacklistEntity::class,
        SortEntity::class,

        PlayingQueueEntity::class,
        PlayingEntity::class,

        FolderMostPlayedEntity::class,
        PlaylistMostPlayedEntity::class,
        GenreMostPlayedEntity::class,

        FavoriteEntity::class,

        RecentSearchesEntity::class,

        HistoryEntity::class,
        PodcastHistoryEntity::class,

        LastPlayedAlbumEntity::class,
        LastPlayedArtistEntity::class,
        LastPlayedPodcastAlbumEntity::class,
        LastPlayedPodcastArtistEntity::class,

        LastFmTrackEntity::class,
        LastFmAlbumEntity::class,
        LastFmArtistEntity::class,

        OfflineLyricsEntity::class,

        PlaylistEntity::class,
        PlaylistTrackEntity::class,

        PodcastPlaylistEntity::class,
        PodcastPlaylistTrackEntity::class,

        PodcastPositionEntity::class,

        LyricsSyncAdjustmentEntity::class,
        EqualizerPresetEntity::class
    ],
    views = [
        MediaStoreSongsView::class,
        MediaStoreSongsViewSorted::class,
        MediaStoreArtistsView::class,
        MediaStoreArtistsViewSorted::class,
        MediaStoreAlbumsView::class,
        MediaStoreAlbumsViewSorted::class,
        MediaStoreFoldersView::class,
        MediaStoreFoldersViewSorted::class,
        MediaStoreGenresView::class,
        MediaStoreGenresViewSorted::class,
        MediaStorePlaylistsView::class,
        MediaStorePlaylistsViewSorted::class,

        MediaStorePodcastsView::class,
        MediaStorePodcastsViewSorted::class,
        MediaStorePodcastAlbumsView::class,
        MediaStorePodcastAlbumsViewSorted::class,
        MediaStorePodcastArtistsView::class,
        MediaStorePodcastArtistsViewSorted::class,
    ],
    version = 19,
    exportSchema = true
)
@TypeConverters(
    CustomTypeConverters::class,
    SortTypeConverters::class,
)
abstract class AppDatabase : RoomDatabase() {

    // mediastore
    abstract fun mediaStoreAudioDao(): MediaStoreAudioDao
    abstract fun mediaStoreGenreDao(): MediaStoreGenreDao
    abstract fun mediaStorePlaylistDao(): MediaStorePlaylistDao

    // mediastore song views
    abstract fun mediaStoreSongsViewDao(): MediaStoreSongsViewDao
    abstract fun mediaStoreArtistsViewDao(): MediaStoreArtistsViewDao
    abstract fun mediaStoreAlbumsViewDao(): MediaStoreAlbumsViewDao
    abstract fun mediaStoreFoldersViewDao(): MediaStoreFoldersViewDao
    abstract fun mediaStoreGenresViewDao(): MediaStoreGenresViewDao
    abstract fun mediaStorePlaylistsViewDao(): MediaStorePlaylistsViewDao

    // mediastore podcast views
    abstract fun mediaStorePodcastDao(): MediaStorePodcastsViewDao
    abstract fun mediaStorePodcastAlbumsDao(): MediaStorePodcastAlbumsViewDao
    abstract fun mediaStorePodcastArtistsDao(): MediaStorePodcastArtistsViewDao

    // utils
    abstract fun blacklistDao(): BlacklistDao
    abstract fun sortDao(): SortDao

    // song queries
    abstract fun songDao(): SongDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun folderDao(): FolderDao
    abstract fun genreDao(): GenreDao

    // podcast queries
    abstract fun podcastDao(): PodcastDao
    abstract fun podcastAlbumDao(): PodcastAlbumDao
    abstract fun podcastArtistDao(): PodcastArtistDao
    // todo

    abstract fun playingDao(): PlayingDao
    abstract fun playingQueueDao(): PlayingQueueDao

    abstract fun playlistMostPlayedDao(): PlaylistMostPlayedDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun recentSearchesDao(): RecentSearchesDao

    abstract fun historyDao(): HistoryDao

    abstract fun lastFmDao(): LastFmDao

    abstract fun offlineLyricsDao(): OfflineLyricsDao

    abstract fun playlistDao(): LegacyPlaylistDao
    abstract fun podcastPlaylistDao(): PodcastPlaylistDao

    abstract fun podcastPositionDao(): PodcastPositionDao

    abstract fun lyricsSyncAdjustmentDao(): LyricsSyncAdjustmentDao
    abstract fun equalizerPresetsDao(): EqualizerPresetsDao
}