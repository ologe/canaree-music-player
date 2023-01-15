package dev.olog.presentation.prefs.blacklist

import android.os.Environment
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.BlacklistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.BaseModel
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistGateway
) {

    val data : Flow<List<BlacklistModel>> = flow {
        val blacklisted = appPreferencesUseCase.getBlacklist().map { it.toLowerCase(Locale.getDefault()) }
        val items = folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
        emit(items)
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            R.layout.dialog_blacklist_item,
            getMediaId(),
            this.title,
            this.path,
            blacklisted.contains(this.path.toLowerCase(Locale.getDefault()))
        )
    }

    suspend fun saveBlacklisted(data: List<BlacklistModel>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { it.path }
        appPreferencesUseCase.setBlacklist(blacklisted)
    }


}

data class BlacklistModel(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val path: String,
    var isBlacklisted: Boolean
) : BaseModel {

    companion object {
        @Suppress("DEPRECATION")
        @JvmStatic
        private val defaultStorageDir = Environment.getExternalStorageDirectory().path ?: "/storage/emulated/0/"
    }

    // show the path without "/storage/emulated/0"
    val displayablePath : String
        get() {
            return try {
                path.substring(defaultStorageDir.length)
            } catch (ex: StringIndexOutOfBoundsException){
                ex.printStackTrace()
                path
            }
        }

}