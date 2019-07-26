package dev.olog.presentation.popup.main

import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import dev.olog.presentation.BuildConfig
import dev.olog.presentation.R
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.pro.IBilling
import javax.inject.Inject

class MainPopupDialog @Inject constructor(
    private val billing: IBilling,
    private val popupNavigator: MainPopupNavigator,
    private val gateway: SortPreferences

) {

    companion object {
        private const val SAVE_AS_PLAYLIST_ID = -12345
    }

    fun show(anchor: View, navigator: Navigator, category: MediaIdCategory?) {
        val popup = PopupMenu(anchor.context, anchor, Gravity.BOTTOM or Gravity.END)
        val layoutId = when (category) {
            MediaIdCategory.ALBUMS -> R.menu.main_albums
            MediaIdCategory.SONGS -> R.menu.main_songs
            MediaIdCategory.ARTISTS -> R.menu.main_artists
            else -> R.menu.main
        }
        popup.inflate(layoutId)

        if (billing.getBillingsState().isPremiumStrict()) {
            popup.menu.removeItem(R.id.premium)
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
                SAVE_AS_PLAYLIST_ID, Menu.NONE, anchor.context.getString(R.string.save_as_playlist)
            )
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.premium -> billing.purchasePremium()
                R.id.about -> popupNavigator.toAboutActivity()
                R.id.equalizer -> popupNavigator.toEqualizer()
                R.id.settings -> popupNavigator.toSettingsActivity()
                R.id.sleepTimer -> popupNavigator.toSleepTimer()
                SAVE_AS_PLAYLIST_ID -> navigator.toCreatePlaylistDialog(
                    MediaId.playingQueueId,
                    -1,
                    ""
                )
                else -> {
                    when (category) {
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
            model.copy(arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album -> SortType.ALBUM
                R.id.by_duration -> SortType.DURATION
                R.id.by_date -> SortType.RECENTLY_ADDED
                else -> null
            } ?: return
            model.copy(type = newSortType)
        }

        gateway.setAllTracksSort(model)
    }

    private fun handleAllAlbumsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            model.copy(arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                else -> null
            } ?: return
            model.copy(type = newSortType)
        }

        gateway.setAllAlbumsSort(model)
    }

    private fun handleAllArtistsSorting(menuItem: MenuItem, sort: SortEntity) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) SortArranging.ASCENDING else SortArranging.DESCENDING
            model.copy(arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album_artist -> SortType.ALBUM_ARTIST
                else -> null
            } ?: return
            model.copy(type = newSortType)
        }

        gateway.setAllArtistsSort(model)
    }

}