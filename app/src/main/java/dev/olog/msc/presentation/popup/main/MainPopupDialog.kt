package dev.olog.msc.presentation.popup.main

import android.app.Activity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import javax.inject.Inject

class MainPopupDialog @Inject constructor(
        private val navigator: MainPopupNavigator,
        private val gateway: AppPreferencesGateway

){

    fun show(activity: Activity, anchor: View, category: MediaIdCategory){
        val popup = PopupMenu(activity, anchor, Gravity.BOTTOM or Gravity.END)
        val layoutId = when (category){
            MediaIdCategory.ALBUMS -> R.menu.main_albums
            MediaIdCategory.SONGS -> R.menu.main_songs
            else -> R.menu.main
        }
        popup.inflate(layoutId)

        val sortModel = when(category){
            MediaIdCategory.ALBUMS -> initializeAlbumSort(popup.menu)
            MediaIdCategory.SONGS -> initializeTracksSort(popup.menu)
            else -> null
        }
//        popup.addRotateAnimation(anchor)

        if (BuildConfig.DEBUG){
            popup.menu.add(Menu.NONE, -123, Menu.NONE, "configuration")
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.about -> navigator.toAboutActivity()
                R.id.equalizer -> navigator.toEqualizer()
                R.id.settings -> navigator.toSettingsActivity()
                R.id.sleepTimer -> navigator.toSleepTimer()
                -123 -> navigator.toDebugConfiguration()
                else -> {
                    if (category == MediaIdCategory.ALBUMS){
                        handleAllAlbumsSorting(it, sortModel!!)
                    }
                    if (category == MediaIdCategory.SONGS) {
                        handleAllSongsSorting(it, sortModel!!)
                    }
                }
            }

            true
        }
        popup.show()
    }

    private fun initializeTracksSort(menu: Menu): LibrarySortType {
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

    private fun initializeAlbumSort(menu: Menu): LibrarySortType {
        val sort = gateway.getAllAlbumsSortOrder()
        val item = when (sort.type){
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == SortArranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllSongsSorting(menuItem: MenuItem, sort: LibrarySortType){
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

    private fun handleAllAlbumsSorting(menuItem: MenuItem, sort: LibrarySortType){
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



}