package dev.olog.presentation.dialog_sort

import android.content.Context
import android.support.annotation.MenuRes
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import dev.olog.domain.entity.SortType
import dev.olog.presentation.R
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Single
import io.reactivex.disposables.Disposable

class DetailSortDialog {

    fun show(context: Context, view: View, mediaId: MediaId, sortTypeSingle: Single<SortType>, updateUseCase: (SortType) -> Disposable) {
        sortTypeSingle.subscribe({ sortType ->

            val popup = PopupMenu(context, view, Gravity.BOTTOM)
            popup.inflate(getLayout(mediaId))

            setChecked(popup.menu, sortType)

            popup.setOnMenuItemClickListener { menu ->
                val newSortType = when (menu.itemId){
                    R.id.by_title -> SortType.TITLE
                    R.id.by_artist -> SortType.ARTIST
                    R.id.by_album -> SortType.ALBUM
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

        })


    }

    @MenuRes
    private fun getLayout(mediaId: MediaId) : Int{
        return when (mediaId.category){
            MediaIdCategory.PLAYLIST -> R.menu.sort_mode_playlist
            MediaIdCategory.ALBUM -> R.menu.sort_mode_album
            else -> R.menu.sort_mode
        }
    }

    private fun setChecked(menu: Menu, sortType: SortType){
        val item = when (sortType){
            SortType.TITLE -> R.id.by_title
            SortType.ALBUM -> R.id.by_album
            SortType.ARTIST -> R.id.by_artist
            SortType.DURATION -> R.id.by_duration
            SortType.RECENTLY_ADDED ->R.id.by_recently_added
            SortType.CUSTOM ->R.id.by_custom
            SortType.TRACK_NUMBER ->R.id.by_track_number
        }
        menu.findItem(item).isChecked = true
    }

}
