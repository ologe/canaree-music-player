package dev.olog.msc.presentation.popup.main

import android.app.Activity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.navigator.Navigator
import dev.olog.msc.pro.IBilling
import javax.inject.Inject


private const val DEBUG_ID = -123
private const val SAVE_AS_PLAYLIST_ID = -12345

class MainPopupDialog @Inject constructor(
    private val billing: IBilling,
    private val activityNavigator: Navigator,
    private val navigator: MainPopupNavigator,
    private val gateway: SortPreferences

){

    fun show(activity: Activity, anchor: View, category: MediaIdCategory?){
        val popup = PopupMenu(activity, anchor, Gravity.BOTTOM or Gravity.END)
        val layoutId = when (category){
            MediaIdCategory.ALBUMS -> R.menu.main_albums
            MediaIdCategory.SONGS -> R.menu.main_songs
            MediaIdCategory.ARTISTS -> R.menu.main_artists
            else -> R.menu.main
        }
        popup.inflate(layoutId)

        if (billing.isOnlyPremium()){
            popup.menu.removeItem(R.id.premium)
        }

        val sortModel = when(category){
            MediaIdCategory.ALBUMS -> initializeAlbumSort(popup.menu)
            MediaIdCategory.SONGS -> initializeTracksSort(popup.menu)
            MediaIdCategory.ARTISTS -> initializeArtistSort(popup.menu)
            else -> null
        }

        if (BuildConfig.DEBUG){
            popup.menu.add(Menu.NONE, DEBUG_ID, Menu.NONE, "configuration")
        }

        if (category == MediaIdCategory.PLAYING_QUEUE){
            popup.menu.add(Menu.NONE, SAVE_AS_PLAYLIST_ID, Menu.NONE, activity.getString(R.string.save_as_playlist))
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.premium -> billing.purchasePremium()
                R.id.about -> navigator.toAboutActivity()
                R.id.equalizer -> navigator.toEqualizer()
                R.id.settings -> navigator.toSettingsActivity()
                R.id.sleepTimer -> navigator.toSleepTimer()
                R.id.share -> activityNavigator.toShareApp()
                DEBUG_ID -> navigator.toDebugConfiguration()
                SAVE_AS_PLAYLIST_ID -> activityNavigator.toCreatePlaylistDialog(MediaId.playingQueueId, -1, "")
                else -> {
                    when (category){
                        MediaIdCategory.ALBUMS -> handleAllAlbumsSorting(it, sortModel!!)
                        MediaIdCategory.SONGS -> handleAllSongsSorting(it, sortModel!!)
                        MediaIdCategory.ARTISTS -> handleAllArtistsSorting(it, sortModel!!)
                    }
                }
            }

            true
        }
        popup.show()
    }

    private fun initializeTracksSort(menu: Menu): SortEntity {
        val sort = gateway.getAllTracksSortOrder()
        val item = when (sort.type){
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            SortType.ARTIST -> R.id.by_artist
            SortType.DURATION -> R.id.by_duration
            SortType.RECENTLY_ADDED ->R.id.by_date
            else -> throw IllegalStateException("invalid for tracks ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeAlbumSort(menu: Menu): SortEntity {
        val sort = gateway.getAllAlbumsSortOrder()
        val item = when (sort.type){
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
        val sort = gateway.getAllArtistsSortOrder()
        val item = when (sort.type){
            SortType.ARTIST -> R.id.by_artist
            SortType.ALBUM_ARTIST -> R.id.by_album_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllSongsSorting(menuItem: MenuItem, sort: SortEntity){
        var model = sort

        model = if (menuItem.itemId == R.id.arranging){
            val isAscending = !menuItem.isChecked
            val newArranging = if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            model.copy(arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId){
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album -> SortType.ALBUM
                R.id.by_duration -> SortType.DURATION
                R.id.by_date -> SortType.RECENTLY_ADDED
                else -> null
            } ?: return
            model.copy(type = newSortType)
        }

        gateway.setAllTracksSortOrder(model)
    }

    private fun handleAllAlbumsSorting(menuItem: MenuItem, sort: SortEntity){
        var model = sort

        model = if (menuItem.itemId == R.id.arranging){
            val isAscending = !menuItem.isChecked
            val newArranging = if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            model.copy(arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId){
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                else -> null
            } ?: return
            model.copy(type = newSortType)
        }

        gateway.setAllAlbumsSortOrder(model)
    }

    private fun handleAllArtistsSorting(menuItem: MenuItem, sort: SortEntity){
        var model = sort

        model = if (menuItem.itemId == R.id.arranging){
            val isAscending = !menuItem.isChecked
            val newArranging = if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            model.copy(arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId){
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album_artist -> SortType.ALBUM_ARTIST
                else -> null
            } ?: return
            model.copy(type = newSortType)
        }

        gateway.setAllArtistsSortOrder(model)
    }

}