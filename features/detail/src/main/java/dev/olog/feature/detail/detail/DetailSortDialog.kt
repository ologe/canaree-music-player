package dev.olog.feature.detail.detail

import android.view.Menu
import android.view.View
import androidx.annotation.MenuRes
import dev.olog.domain.entity.Sort
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.feature.detail.R
import me.saket.cascade.CascadePopupMenu

class DetailSortDialog {

    fun show(view: View, mediaId: MediaId, sortType: Sort.Type, updateUseCase: (Sort.Type) -> Unit) {
        val context = view.context
        val popup = CascadePopupMenu(context, view)
        popup.inflate(getLayout(mediaId))

        setChecked(popup.menu, sortType)

        popup.setOnMenuItemClickListener { menu ->
            val newSortType = when (menu.itemId){
                R.id.by_title -> Sort.Type.TITLE
                R.id.by_artist -> Sort.Type.ARTIST
                R.id.by_album -> Sort.Type.ALBUM
                R.id.by_album_artist -> Sort.Type.ALBUM_ARTIST
                R.id.by_duration -> Sort.Type.DURATION
                R.id.by_recently_added -> Sort.Type.RECENTLY_ADDED
                R.id.by_custom -> Sort.Type.CUSTOM
                R.id.by_track_number -> Sort.Type.TRACK_NUMBER
                else -> throw IllegalArgumentException("sort type not exist")
            }

            updateUseCase(newSortType)

            true
        }

        popup.show()
    }

    @MenuRes
    private fun getLayout(mediaId: MediaId) : Int{
        return when (mediaId.category){
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> R.menu.sort_mode_playlist
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> R.menu.sort_mode_album
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> R.menu.sort_mode_artist
            MediaIdCategory.FOLDERS -> R.menu.sort_mode_folder
            else -> R.menu.sort_mode
        }
    }

    private fun setChecked(menu: Menu, sortType: Sort.Type){
        val item = when (sortType){
            Sort.Type.TITLE -> R.id.by_title
            Sort.Type.ALBUM -> R.id.by_album
            Sort.Type.ARTIST -> R.id.by_artist
            Sort.Type.ALBUM_ARTIST -> R.id.by_album_artist
            Sort.Type.DURATION -> R.id.by_duration
            Sort.Type.RECENTLY_ADDED ->R.id.by_recently_added
            Sort.Type.CUSTOM -> R.id.by_custom
            Sort.Type.TRACK_NUMBER ->R.id.by_track_number
        }
        menu.findItem(item).isChecked = true
    }

}
