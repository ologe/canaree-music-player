package dev.olog.feature.library.dialog

import android.content.Context
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.Sort
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.prefs.SortPreferencesGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.prefs.LibraryPreferencesGateway
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.feature.library.tab.model.toTabCategory
import dev.olog.navigation.Navigator
import dev.olog.shared.android.extensions.findActivity
import me.saket.cascade.CascadePopupMenu
import timber.log.Timber
import javax.inject.Inject

internal class MainPopupDialog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navigator: Navigator,
    private val gateway: SortPreferencesGateway,
    private val presentationPrefs: LibraryPreferencesGateway

) {

    companion object {
        private const val SAVE_AS_PLAYLIST_ID = -12345
    }

    /**
     * @param category null when from search
     *                 [MediaId.playingQueueId] when from playing queue
     *                 valid category when form tab
     */
    fun show(anchor: View, category: MediaIdCategory) {
        val popup = CascadePopupMenu(anchor.context, anchor)
        val layoutId = when (category) {
            MediaIdCategory.ALBUMS -> R.menu.main_albums
            MediaIdCategory.SONGS -> R.menu.main_songs
            MediaIdCategory.ARTISTS -> R.menu.main_artists
            else -> R.menu.main
        }
        popup.inflate(layoutId)

//        if (category == MediaIdCategory.PLAYING_QUEUE){ TODO
//            popup.menu.removeItem(R.id.gridSize)
//        }

        val sortModel = when (category) {
            MediaIdCategory.ALBUMS -> initializeAlbumSort(popup.menu)
            MediaIdCategory.SONGS -> initializeTracksSort(popup.menu)
            MediaIdCategory.ARTISTS -> initializeArtistSort(popup.menu)
            else -> null
        }

//        if (category == MediaIdCategory.PLAYING_QUEUE) { TODO
//            popup.menu.add(
//                Menu.NONE,
//                SAVE_AS_PLAYLIST_ID, Menu.NONE, anchor.context.getString(R.string.save_as_playlist)
//            )
//        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> navigator.toAbout()
                R.id.equalizer -> navigator.toEqualizer()
                R.id.settings -> navigator.toSettings()
                R.id.sleepTimer -> navigator.toSleepTimer()
                SAVE_AS_PLAYLIST_ID -> navigator.toCreatePlaylist()
                R.id.gridSize1 -> updateSpanCount(anchor, category.toTabCategory(), 1)
                R.id.gridSize2 -> updateSpanCount(anchor, category.toTabCategory(), 2)
                R.id.gridSize3 -> updateSpanCount(anchor, category.toTabCategory(), 3)
                R.id.gridSize4 -> updateSpanCount(anchor, category.toTabCategory(), 4)
                else -> {
                    when (category) {
                        MediaIdCategory.ALBUMS -> handleAllAlbumsSorting(it, sortModel!!)
                        MediaIdCategory.SONGS -> handleAllSongsSorting(it, sortModel!!)
                        MediaIdCategory.ARTISTS -> handleAllArtistsSorting(it, sortModel!!)
                        else -> Timber.w("not handled $category")
                    }
                }
            }

            true
        }
        popup.show()
    }

    private fun updateSpanCount(view: View, category: TabFragmentCategory, spanCount: Int){
        val current = presentationPrefs.getSpanCount(category)
        presentationPrefs.setSpanCount(category, spanCount)
        if (current == 1 && spanCount > 1 || current > 1 && spanCount == 1){
            (view.findActivity()).recreate()
        }
    }

    private fun initializeTracksSort(menu: Menu): Sort {
        val sort = gateway.getAllTracksSort()
        val item = when (sort.type) {
            Sort.Type.TITLE -> R.id.by_title
            Sort.Type.ALBUM -> R.id.by_album
            Sort.Type.ARTIST -> R.id.by_artist
            Sort.Type.DURATION -> R.id.by_duration
            Sort.Type.RECENTLY_ADDED -> R.id.by_date
            else -> throw IllegalStateException("invalid for tracks ${sort.type}")
        }
        val ascending = sort.arranging == Sort.Arranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeAlbumSort(menu: Menu): Sort {
        val sort = gateway.getAllAlbumsSort()
        val item = when (sort.type) {
            Sort.Type.TITLE -> R.id.by_title
            Sort.Type.ARTIST -> R.id.by_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == Sort.Arranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun initializeArtistSort(menu: Menu): Sort {
        val sort = gateway.getAllArtistsSort()
        val item = when (sort.type) {
            Sort.Type.ARTIST -> R.id.by_artist
            Sort.Type.ALBUM_ARTIST -> R.id.by_album_artist
            else -> throw IllegalStateException("invalid for albums ${sort.type}")
        }
        val ascending = sort.arranging == Sort.Arranging.ASCENDING
        menu.findItem(item).isChecked = true
        menu.findItem(R.id.arranging).isChecked = ascending

        return sort
    }

    private fun handleAllSongsSorting(menuItem: MenuItem, sort: Sort) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) Sort.Arranging.ASCENDING else Sort.Arranging.DESCENDING
            Sort(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> Sort.Type.TITLE
                R.id.by_artist -> Sort.Type.ARTIST
                R.id.by_album -> Sort.Type.ALBUM
                R.id.by_duration -> Sort.Type.DURATION
                R.id.by_date -> Sort.Type.RECENTLY_ADDED
                else -> null
            } ?: return
            Sort(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllTracksSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllAlbumsSorting(menuItem: MenuItem, sort: Sort) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) Sort.Arranging.ASCENDING else Sort.Arranging.DESCENDING
            Sort(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_title -> Sort.Type.TITLE
                R.id.by_artist -> Sort.Type.ARTIST
                else -> null
            } ?: return
            Sort(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllAlbumsSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

    private fun handleAllArtistsSorting(menuItem: MenuItem, sort: Sort) {
        var model = sort

        model = if (menuItem.itemId == R.id.arranging) {
            val isAscending = !menuItem.isChecked
            val newArranging =
                if (isAscending) Sort.Arranging.ASCENDING else Sort.Arranging.DESCENDING
            Sort(type = model.type, arranging = newArranging)
        } else {
            val newSortType = when (menuItem.itemId) {
                R.id.by_artist -> Sort.Type.ARTIST
                R.id.by_album_artist -> Sort.Type.ALBUM_ARTIST
                else -> null
            } ?: return
            Sort(type = newSortType, arranging = model.arranging)
        }

        gateway.setAllArtistsSort(model)
        context.contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
    }

}