package dev.olog.feature.main

import android.app.Activity
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.feature.about.api.FeatureAboutNavigator
import dev.olog.feature.equalizer.api.FeatureEqualizerNavigator
import dev.olog.feature.library.api.LibraryPreferences
import dev.olog.feature.library.api.TabCategory
import dev.olog.feature.library.api.toTabCategory
import dev.olog.feature.main.api.FeatureMainNavigator
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
    private val gateway: SortPreferences,
    private val libraryPrefs: LibraryPreferences,

) {

    companion object {
        private const val SAVE_AS_PLAYLIST_ID = -12345
    }

    /**
     * @param category null when from search
     *                 [MediaId.playingQueueId] when from playing queue
     *                 valid category when form tab
     */
    fun show(
        activity: FragmentActivity,
        anchor: View,
        category: MediaIdCategory?
    ) {
        val popup = PopupMenu(anchor.context, anchor)
        val layoutId = when (category) {
            MediaIdCategory.ALBUMS -> R.menu.main_albums
            MediaIdCategory.SONGS -> R.menu.main_songs
            MediaIdCategory.ARTISTS -> R.menu.main_artists
            else -> R.menu.main
        }
        popup.inflate(layoutId)

        if (category == null || category == MediaIdCategory.PLAYING_QUEUE){
            popup.menu.removeItem(R.id.gridSize)
        }

        val sortModel = when (category) {
            MediaIdCategory.ALBUMS -> initializeAlbumSort(popup.menu)
            MediaIdCategory.SONGS -> initializeTracksSort(popup.menu)
            MediaIdCategory.ARTISTS -> initializeArtistSort(popup.menu)
            else -> null
        }

        if (category == MediaIdCategory.PLAYING_QUEUE) {
            popup.menu.add(
                Menu.NONE,
                SAVE_AS_PLAYLIST_ID, Menu.NONE, anchor.context.getString(localization.R.string.save_as_playlist)
            )
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> featureAboutNavigator.toAbout(activity)
                R.id.equalizer -> featureEqualizerNavigator.toEqualizer(activity)
                R.id.settings -> featureSettingsNavigator.toSettings(activity)
                R.id.sleepTimer -> featureMainNavigator.toSleepTimerDialog(activity)
                SAVE_AS_PLAYLIST_ID -> featurePlaylistNavigator.toCreatePlaylistDialog(
                    activity = activity,
                    mediaId = MediaId.playingQueueId,
                    listSize = -1,
                    itemTitle = ""
                )
                R.id.gridSize1 -> updateSpanCount(anchor, category!!.toTabCategory(), 1)
                R.id.gridSize2 -> updateSpanCount(anchor, category!!.toTabCategory(), 2)
                R.id.gridSize3 -> updateSpanCount(anchor, category!!.toTabCategory(), 3)
                R.id.gridSize4 -> updateSpanCount(anchor, category!!.toTabCategory(), 4)
                else -> {
                    when (category) {
                        MediaIdCategory.ALBUMS -> handleAllAlbumsSorting(activity, it, sortModel!!)
                        MediaIdCategory.SONGS -> handleAllSongsSorting(activity, it, sortModel!!)
                        MediaIdCategory.ARTISTS -> handleAllArtistsSorting(activity, it, sortModel!!)
                        else -> Log.w("MainPopup", "not handled $category")
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

    private fun initializeTracksSort(menu: Menu): Sort {
        val sort = gateway.getAllTracksSort()
        val item = when (sort.type) {
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            SortType.ARTIST -> R.id.by_artist
            SortType.DURATION -> R.id.by_duration
            SortType.RECENTLY_ADDED -> R.id.by_date
            else -> throw IllegalStateException("invalid for tracks ${sort.type}")
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeAlbumSort(menu: Menu): Sort {
        val sort = gateway.getAllAlbumsSort()
        val item = when (sort.type) {
            SortType.TITLE -> R.id.by_title
            SortType.ARTIST -> R.id.by_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeArtistSort(menu: Menu): Sort {
        val sort = gateway.getAllArtistsSort()
        val item = when (sort.type) {
            SortType.ARTIST -> R.id.by_artist
            SortType.ALBUM_ARTIST -> R.id.by_album_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.direction == SortDirection.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllSongsSorting(
        activity: FragmentActivity,
        menuItem: MenuItem,
        sort: Sort
    ) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortDirection.ASCENDING else SortDirection.DESCENDING
            Sort(type = model.type, direction = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album -> SortType.ALBUM
                R.id.by_duration -> SortType.DURATION
                R.id.by_date -> SortType.RECENTLY_ADDED
                else -> null
            } ?: return
            Sort(type = newSortType, direction = model.direction)
        }

        gateway.setAllTracksSort(model)
        activity.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllAlbumsSorting(
        activity: FragmentActivity,
        menuItem: MenuItem,
        sort: Sort
    ) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortDirection.ASCENDING else SortDirection.DESCENDING
            Sort(type = model.type, direction = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                else -> null
            } ?: return
            Sort(type = newSortType, direction = model.direction)
        }

        gateway.setAllAlbumsSort(model)
        activity.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllArtistsSorting(
        activity: FragmentActivity,
        menuItem: MenuItem,
        sort: Sort
    ) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortDirection.ASCENDING else SortDirection.DESCENDING
            Sort(type = model.type, direction = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album_artist -> SortType.ALBUM_ARTIST
                else -> null
            } ?: return
            Sort(type = newSortType, direction = model.direction)
        }

        gateway.setAllArtistsSort(model)
        activity.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

}