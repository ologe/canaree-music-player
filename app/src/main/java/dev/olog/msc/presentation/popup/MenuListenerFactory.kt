package dev.olog.msc.presentation.popup

import dev.olog.core.entity.podcast.Podcast
import dev.olog.core.entity.podcast.PodcastAlbum
import dev.olog.core.entity.podcast.PodcastArtist
import dev.olog.core.entity.podcast.PodcastPlaylist
import dev.olog.core.entity.track.*
import dev.olog.msc.presentation.popup.album.AlbumPopupListener
import dev.olog.msc.presentation.popup.artist.ArtistPopupListener
import dev.olog.msc.presentation.popup.folder.FolderPopupListener
import dev.olog.msc.presentation.popup.genre.GenrePopupListener
import dev.olog.msc.presentation.popup.playlist.PlaylistPopupListener
import dev.olog.msc.presentation.popup.podcast.PodcastPopupListener
import dev.olog.msc.presentation.popup.podcastalbum.PodcastAlbumPopupListener
import dev.olog.msc.presentation.popup.podcastartist.PodcastArtistPopupListener
import dev.olog.msc.presentation.popup.podcastplaylist.PodcastPlaylistPopupListener
import dev.olog.msc.presentation.popup.song.SongPopupListener
import javax.inject.Inject
import javax.inject.Provider

class MenuListenerFactory @Inject constructor(
        private val folderPopupListener: Provider<FolderPopupListener>,
        private val playlistPopupListener: Provider<PlaylistPopupListener>,
        private val songPopupListener: Provider<SongPopupListener>,
        private val albumPopupListener: Provider<AlbumPopupListener>,
        private val artistPopupListener: Provider<ArtistPopupListener>,
        private val genrePopupListener: Provider<GenrePopupListener>,
        private val podcastPopupListener: Provider<PodcastPopupListener>,
        private val podcastPlaylistPopupListener: Provider<PodcastPlaylistPopupListener>,
        private val podcastAlbumPopupListener: Provider<PodcastAlbumPopupListener>,
        private val podcastArtistPopupListener: Provider<PodcastArtistPopupListener>
) {

    fun folder(folder: Folder, song: Song?) = folderPopupListener.get().setData(folder, song)
    fun playlist(playlist: Playlist, song: Song?) = playlistPopupListener.get().setData(playlist, song)
    fun song(song: Song) = songPopupListener.get().setData(song)
    fun album(album: Album, song: Song?) = albumPopupListener.get().setData(album, song)
    fun artist(artist: Artist, song: Song?) = artistPopupListener.get().setData(artist, song)
    fun genre(genre: Genre, song: Song?) = genrePopupListener.get().setData(genre, song)
    fun podcast(podcast: Podcast) = podcastPopupListener.get().setData(podcast)
    fun podcastPlaylist(podcastPlaylist: PodcastPlaylist, song: Podcast?) = podcastPlaylistPopupListener.get().setData(podcastPlaylist, song)
    fun podcastAlbum(podcastAlbum: PodcastAlbum, song: Podcast?) = podcastAlbumPopupListener.get().setData(podcastAlbum, song)
    fun podcastArtist(podcastArtist: PodcastArtist, song: Podcast?) = podcastArtistPopupListener.get().setData(podcastArtist, song)

}