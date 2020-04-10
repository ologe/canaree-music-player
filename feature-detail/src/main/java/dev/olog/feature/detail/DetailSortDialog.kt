package dev.olog.feature.detail

import android.view.Menu
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import dev.olog.domain.entity.sort.SortType
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory

class DetailSortDialog {

    fun show(
        view: View,
        mediaId: PresentationId.Category,
        sortType: SortType,
        updateUseCase: (SortType) -> Unit
    ) {
        val context = view.context
        val popup = PopupMenu(context, view)
        popup.inflate(getLayout(mediaId))

        setChecked(popup.menu, sortType)

        popup.setOnMenuItemClickListener { menu ->
            val newSortType = when (menu.itemId){
                R.id.by_title -> SortType.TITLE
                R.id.by_artist -> SortType.ARTIST
                R.id.by_album -> SortType.ALBUM
                R.id.by_album_artist -> SortType.ALBUM_ARTIST
                R.id.by_duration -> SortType.DURATION
                R.id.by_recently_added -> SortType.RECENTLY_ADDED
                R.id.by_custom -> SortType.CUSTOM
                R.id.by_track_number -> SortType.TRACK_NUMBER
                else -> throw IllegalArgumentException("sort type not exist")
            }

            updateUseCase(newSortType)

            true
        }

        popup.show()
    }

    @MenuRes
    private fun getLayout(mediaId: PresentationId.Category) : Int{
        return when (mediaId.category){
            PresentationIdCategory.PLAYLISTS -> R.menu.sort_mode_playlist
            PresentationIdCategory.ALBUMS -> R.menu.sort_mode_album
            PresentationIdCategory.ARTISTS -> R.menu.sort_mode_artist
            PresentationIdCategory.FOLDERS -> R.menu.sort_mode_folder
            else -> R.menu.sort_mode
        }
    }

    private fun setChecked(menu: Menu, sortType: SortType){
        val item = when (sortType){
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            SortType.ARTIST -> R.id.by_artist
            SortType.ALBUM_ARTIST -> R.id.by_album_artist
            SortType.DURATION -> R.id.by_duration
            SortType.RECENTLY_ADDED ->R.id.by_recently_added
            SortType.CUSTOM -> R.id.by_custom
            SortType.TRACK_NUMBER ->R.id.by_track_number
        }
        menu.findItem(item).isChecked = true
    }

}
