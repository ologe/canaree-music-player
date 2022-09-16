package dev.olog.feature.main

import android.app.Activity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.AlbumSortType
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.AllFoldersSort
import dev.olog.core.entity.sort.AllGenresSort
import dev.olog.core.entity.sort.AllPodcastAlbumsSort
import dev.olog.core.entity.sort.AllPodcastArtistsSort
import dev.olog.core.entity.sort.AllPodcastsSort
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.ArtistSortType
import dev.olog.core.entity.sort.FolderSortType
import dev.olog.core.entity.sort.GenreSortType
import dev.olog.core.entity.sort.PodcastAlbumSortType
import dev.olog.core.entity.sort.PodcastArtistSortType
import dev.olog.core.entity.sort.PodcastSortType
import dev.olog.core.entity.sort.SongSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.feature.about.api.FeatureAboutNavigator
import dev.olog.feature.equalizer.api.FeatureEqualizerNavigator
import dev.olog.feature.library.api.LibraryPreferences
import dev.olog.feature.library.api.TabCategory
import dev.olog.feature.library.api.toTabCategory
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.MainPopupDialogData
import dev.olog.feature.playlist.api.FeaturePlaylistNavigator
import dev.olog.feature.settings.api.FeatureSettingsNavigator
import dev.olog.shared.extension.findInContext
import javax.inject.Inject

class MainPopupDialog @Inject constructor(
    private val featureAboutNavigator: FeatureAboutNavigator,
    private val featureSettingsNavigator: FeatureSettingsNavigator,
    private val featureEqualizerNavigator: FeatureEqualizerNavigator,
    private val featureMainNavigator: FeatureMainNavigator,
    private val featurePlaylistNavigator: FeaturePlaylistNavigator,
    private val libraryPrefs: LibraryPreferences,
    private val folderGateway: FolderGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
) {

    fun show(
        activity: FragmentActivity,
        anchor: View,
        data: MainPopupDialogData,
    ) {
        val popup = PopupMenu(anchor.context, anchor)
        val layoutId = when (data) {
            is MainPopupDialogData.Search -> R.menu.search
            is MainPopupDialogData.PlayingQueue -> R.menu.playing_queue
            is MainPopupDialogData.Library -> when (data.category) {
                MediaIdCategory.FOLDERS -> R.menu.library_folders
                MediaIdCategory.PLAYLISTS,
                MediaIdCategory.PODCASTS_PLAYLIST -> R.menu.main // todo
                MediaIdCategory.SONGS,
                MediaIdCategory.PODCASTS -> R.menu.library_songs
                MediaIdCategory.ALBUMS,
                MediaIdCategory.PODCASTS_ALBUMS -> R.menu.library_albums
                MediaIdCategory.ARTISTS,
                MediaIdCategory.PODCASTS_ARTISTS -> R.menu.library_artists
                MediaIdCategory.GENRES -> R.menu.library_genres
                MediaIdCategory.HEADER -> TODO()
            }
        }
        popup.inflate(layoutId)

        val sortModel: Any? = if (data is MainPopupDialogData.Library) {
            when (data.category) {
                MediaIdCategory.FOLDERS -> initializeFolderSort(popup.menu)
                MediaIdCategory.PLAYLISTS -> TODO()
                MediaIdCategory.SONGS -> initializeTracksSort(popup.menu)
                MediaIdCategory.ALBUMS -> initializeAlbumSort(popup.menu)
                MediaIdCategory.ARTISTS -> initializeArtistSort(popup.menu)
                MediaIdCategory.GENRES -> initializeGenreSort(popup.menu)
                MediaIdCategory.PODCASTS_PLAYLIST -> TODO()
                MediaIdCategory.PODCASTS -> initializePodcastSort(popup.menu)
                MediaIdCategory.PODCASTS_ALBUMS -> initializePodcastAlbumSort(popup.menu)
                MediaIdCategory.PODCASTS_ARTISTS -> initializePodcastArtistSort(popup.menu)
                MediaIdCategory.HEADER -> error("invalid $data")
            }
        } else {
            null
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> featureAboutNavigator.toAbout(activity)
                R.id.equalizer -> featureEqualizerNavigator.toEqualizer(activity)
                R.id.settings -> featureSettingsNavigator.toSettings(activity)
                R.id.sleepTimer -> featureMainNavigator.toSleepTimerDialog(activity)
//                R.id.savePlaylist -> featurePlaylistNavigator.toCreatePlaylistDialog( todo
//                    activity = activity,
//                    mediaId = MediaId.playingQueueId,
//                    listSize = -1,
//                    itemTitle = ""
//                )
            }
            if (data is MainPopupDialogData.Library) {
                when (it.itemId) {
                    R.id.gridSize1 -> updateSpanCount(anchor, data.category.toTabCategory(), 1)
                    R.id.gridSize2 -> updateSpanCount(anchor, data.category.toTabCategory(), 2)
                    R.id.gridSize3 -> updateSpanCount(anchor, data.category.toTabCategory(), 3)
                    R.id.gridSize4 -> updateSpanCount(anchor, data.category.toTabCategory(), 4)
                    else -> {
                        when (data.category) {
                            MediaIdCategory.FOLDERS -> handleAllFoldersSorting(it, sortModel as AllFoldersSort)
                            MediaIdCategory.SONGS -> handleAllSongsSorting(it, sortModel as AllSongsSort)
                            MediaIdCategory.ALBUMS -> handleAllAlbumsSorting(it, sortModel as AllAlbumsSort)
                            MediaIdCategory.ARTISTS -> handleAllArtistsSorting(it, sortModel as AllArtistsSort)
                            MediaIdCategory.GENRES -> handleAllGenresSorting(it, sortModel as AllGenresSort)
                            MediaIdCategory.PODCASTS -> handleAllPodcastsSorting(it, sortModel as AllPodcastsSort)
                            MediaIdCategory.PODCASTS_ALBUMS -> handleAllPodcastAlbumsSorting(it, sortModel as AllPodcastAlbumsSort)
                            MediaIdCategory.PODCASTS_ARTISTS -> handleAllPodcastArtistsSorting(it, sortModel as AllPodcastArtistsSort)
                            else -> Log.w("MainPopup", "not handled $data")
                        }
                    }
                }
            }

            true
        }
        popup.show()
    }

    private fun updateSpanCount(view: View, category: TabCategory, spanCount: Int){
        val current = libraryPrefs.getSpanCount(category)
        libraryPrefs.setSpanCount(category, spanCount)
        if (current == 1 && spanCount > 1 || current > 1 && spanCount == 1){
            (view.context.findInContext<Activity>()).recreate()
        }
    }

    private fun initializeFolderSort(menu: Menu): AllFoldersSort {
        val sort = folderGateway.getSort()
        val item = when (sort.type) {
            FolderSortType.Title -> R.id.by_title
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeTracksSort(menu: Menu): AllSongsSort {
        val sort = songGateway.getSort()
        val item = when (sort.type) {
            SongSortType.Title -> R.id.by_title
            SongSortType.Artist -> R.id.by_artist
            SongSortType.Album -> R.id.by_album
            SongSortType.Duration -> R.id.by_duration
            SongSortType.Date -> R.id.by_date
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeAlbumSort(menu: Menu): AllAlbumsSort {
        val sort = albumGateway.getSort()
        val item = when (sort.type) {
            AlbumSortType.Title -> R.id.by_title
            AlbumSortType.Artist -> R.id.by_artist
            AlbumSortType.Date -> R.id.by_date
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeArtistSort(menu: Menu): AllArtistsSort {
        val sort = artistGateway.getSort()
        val item = when (sort.type) {
            ArtistSortType.Name -> R.id.by_artist
            ArtistSortType.Date -> R.id.by_date
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeGenreSort(menu: Menu): AllGenresSort {
        val sort = genreGateway.getSort()
        val item = when (sort.type) {
            GenreSortType.Name -> R.id.by_title
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializePodcastSort(menu: Menu): AllPodcastsSort {
        val sort = podcastGateway.getSort()
        val item = when (sort.type) {
            PodcastSortType.Title -> R.id.by_title
            PodcastSortType.Artist -> R.id.by_artist
            PodcastSortType.Album -> R.id.by_album
            PodcastSortType.Duration -> R.id.by_duration
            PodcastSortType.Date -> R.id.by_date
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializePodcastAlbumSort(menu: Menu): AllPodcastAlbumsSort {
        val sort = podcastAlbumGateway.getSort()
        val item = when (sort.type) {
            PodcastAlbumSortType.Title -> R.id.by_title
            PodcastAlbumSortType.Artist -> R.id.by_artist
            PodcastAlbumSortType.Date -> R.id.by_date
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializePodcastArtistSort(menu: Menu): AllPodcastArtistsSort {
        val sort = podcastArtistGateway.getSort()
        val item = when (sort.type) {
            PodcastArtistSortType.Name -> R.id.by_artist
            PodcastArtistSortType.Date -> R.id.by_date
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllFoldersSorting(
        menuItem: MenuItem,
        sort: AllFoldersSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_title -> sort.copy(type = FolderSortType.Title)
            else -> sort
        }
        folderGateway.setSort(newSort)
    }

    private fun handleAllSongsSorting(
        menuItem: MenuItem,
        sort: AllSongsSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_title -> sort.copy(type = SongSortType.Title)
            R.id.by_artist -> sort.copy(type = SongSortType.Artist)
            R.id.by_album -> sort.copy(type = SongSortType.Album)
            R.id.by_duration -> sort.copy(type = SongSortType.Duration)
            R.id.by_date -> sort.copy(type = SongSortType.Date)
            else -> sort
        }
        songGateway.setSort(newSort)
    }

    private fun handleAllAlbumsSorting(
        menuItem: MenuItem,
        sort: AllAlbumsSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_title -> sort.copy(type = AlbumSortType.Title)
            R.id.by_artist -> sort.copy(type = AlbumSortType.Artist)
            R.id.by_date -> sort.copy(type = AlbumSortType.Date)
            else -> sort
        }
        albumGateway.setSort(newSort)
    }

    private fun handleAllArtistsSorting(
        menuItem: MenuItem,
        sort: AllArtistsSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_artist -> sort.copy(type = ArtistSortType.Name)
            R.id.by_date -> sort.copy(type = ArtistSortType.Date)
            else -> sort
        }
        artistGateway.setSort(newSort)
    }

    private fun handleAllGenresSorting(
        menuItem: MenuItem,
        sort: AllGenresSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_title -> sort.copy(type = GenreSortType.Name)
            else -> sort
        }
        genreGateway.setSort(newSort)
    }

    private fun handleAllPodcastsSorting(
        menuItem: MenuItem,
        sort: AllPodcastsSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_title -> sort.copy(type = PodcastSortType.Title)
            R.id.by_artist -> sort.copy(type = PodcastSortType.Artist)
            R.id.by_album -> sort.copy(type = PodcastSortType.Album)
            R.id.by_duration -> sort.copy(type = PodcastSortType.Duration)
            R.id.by_date -> sort.copy(type = PodcastSortType.Date)
            else -> sort
        }
        podcastGateway.setSort(newSort)
    }

    private fun handleAllPodcastAlbumsSorting(
        menuItem: MenuItem,
        sort: AllPodcastAlbumsSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_title -> sort.copy(type = PodcastAlbumSortType.Title)
            R.id.by_artist -> sort.copy(type = PodcastAlbumSortType.Artist)
            R.id.by_date -> sort.copy(type = PodcastAlbumSortType.Date)
            else -> sort
        }
        podcastAlbumGateway.setSort(newSort)
    }

    private fun handleAllPodcastArtistsSorting(
        menuItem: MenuItem,
        sort: AllPodcastArtistsSort
    ) {
        val newSort = when (menuItem.itemId) {
            R.id.arranging -> sort.copy(direction = sort.direction.inverted())
            R.id.by_artist -> sort.copy(type = PodcastArtistSortType.Name)
            R.id.by_date -> sort.copy(type = PodcastArtistSortType.Date)
            else -> sort
        }
        podcastArtistGateway.setSort(newSort)
    }

}