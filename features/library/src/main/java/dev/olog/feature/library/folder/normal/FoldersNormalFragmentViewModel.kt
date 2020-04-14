package dev.olog.feature.library.folder.normal

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Folder
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.presentationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class FoldersNormalFragmentViewModel @Inject constructor(
    private val context: Context,
    private val folderGateway: FolderGateway,
    private val preferences: LibraryPreferences
) : ViewModel() {

    val data: Flow<List<DisplayableAlbum>>
        get() {
            return folderGateway.observeAll().map { list ->
                val span = getSpanCount()
                list.map { it.toTabDisplayableItem(span) }
            }
        }

    private fun Folder.toTabDisplayableItem(
        span: Int
    ): DisplayableAlbum {
        return DisplayableAlbum(
            type = if (span == 1) R.layout.item_folder_single_line else R.layout.item_folder,
            mediaId = presentationId,
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(context.resources, size)
        )
    }

    fun getSpanCount(): Int = preferences.getSpanCount(TabCategory.FOLDERS)

    fun observeSpanCount() = preferences
        .observeSpanCount(TabCategory.FOLDERS)
        .drop(1) // drop initial value, already used

}