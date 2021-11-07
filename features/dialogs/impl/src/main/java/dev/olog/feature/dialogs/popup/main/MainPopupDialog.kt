package dev.olog.feature.dialogs.popup.main

import android.app.Activity
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.feature.about.FeatureAboutNavigator
import dev.olog.feature.dialogs.FeatureDialogsNavigator
import dev.olog.feature.dialogs.R
import dev.olog.feature.equalizer.FeatureEqualizerNavigator
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.library.TabCategory
import dev.olog.feature.library.toTabCategory
import dev.olog.feature.main.FeatureMainNavigator
import dev.olog.feature.playlist.FeaturePlaylistNavigator
import dev.olog.shared.android.extensions.findInContext
import javax.inject.Inject

class MainPopupDialog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val aboutNavigator: FeatureAboutNavigator,
    private val playlistNavigator: FeaturePlaylistNavigator,
    private val dialogsNavigator: FeatureDialogsNavigator,
    private val equalizerNavigator: FeatureEqualizerNavigator,
    private val featureMainNavigator: FeatureMainNavigator,
    private val gateway: SortPreferences,
    private val libraryPrefs: LibraryPrefs,
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
                R.id.about -> aboutNavigator.toAboutActivity(activity)
                R.id.equalizer -> {
                    equalizerNavigator.toEqualizer(activity)
                }
                R.id.settings -> {
                    featureMainNavigator.toSettings(activity)
                }
                R.id.sleepTimer -> {
                    dialogsNavigator.toSleepTimer(activity)
                }
                SAVE_AS_PLAYLIST_ID -> playlistNavigator.toCreatePlaylistDialog(
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
                        MediaIdCategory.ALBUMS -> handleAllAlbumsSorting(it, sortModel!!)
                        MediaIdCategory.SONGS -> handleAllSongsSorting(it, sortModel!!)
                        MediaIdCategory.ARTISTS -> handleAllArtistsSorting(it, sortModel!!)
                        else -> Log.w("MainPopup", "not handled $category")
                    }
                }
            }

            true
        }
        popup.show()
    }

    private fun updateSpanCount(view: View, category: TabCategory, spanCount: Int){
        val current = libraryPrefs.spanCount(category).get()
        libraryPrefs.spanCount(category).set(spanCount)
        if (current == 1 && spanCount > 1 || current > 1 && spanCount == 1){
            (view.context.findInContext<Activity>()).recreate()
        }
    }

    private fun initializeTracksSort(menu: Menu): SortEntity {
        val sort = gateway.getAllTracksSort()
        val item = when (sort.type) {
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            SortType.ARTIST -> R.id.by_artist
            SortType.DURATION -> R.id.by_duration
            SortType.RECENTLY_ADDED -> R.id.by_date
            else -> throw IllegalStateException("invalid for tracks ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeAlbumSort(menu: Menu): SortEntity {
        val sort = gateway.getAllAlbumsSort()
        val item = when (sort.type) {
            SortType.TITLE -> R.id.by_title
            SortType.ARTIST -> R.id.by_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeArtistSort(menu: Menu): SortEntity {
        val sort = gateway.getAllArtistsSort()
        val item = when (sort.type) {
            SortType.ARTIST -> R.id.by_artist
            SortType.ALBUM_ARTIST -> R.id.by_album_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllSongsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            SortEntity(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album -> SortType.ALBUM
                R.id.by_duration -> SortType.DURATION
                R.id.by_date -> SortType.RECENTLY_ADDED
                else -> null
            } ?: return
            SortEntity(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllTracksSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllAlbumsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            SortEntity(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                else -> null
            } ?: return
            SortEntity(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllAlbumsSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllArtistsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            SortEntity(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album_artist -> SortType.ALBUM_ARTIST
                else -> null
            } ?: return
            SortEntity(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllArtistsSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

}