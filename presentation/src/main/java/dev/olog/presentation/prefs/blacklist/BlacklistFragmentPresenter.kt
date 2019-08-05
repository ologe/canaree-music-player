package dev.olog.presentation.prefs.blacklist

import android.os.Environment
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.presentation.R
import dev.olog.presentation.model.BaseModel
import dev.olog.shared.lazyFast
import java.util.*
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) {

    val data : List<BlacklistModel> by lazyFast {
        val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase(Locale.getDefault()) }
        folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
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

    fun saveBlacklisted(data: List<BlacklistModel>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { it.path }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
    }


}

class BlacklistModel(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val path: String,
    @JvmField
    var isBlacklisted: Boolean
) : BaseModel {

    companion object {
        @Suppress("DEPRECATION")
        @JvmStatic
        private val defaultStorageDir = Environment.getExternalStorageDirectory().path ?: "/storage/emulated/0/"
    }

    // show the path without "/storage/emulated/0"
    @JvmField
    val displayablePath = path.substring(defaultStorageDir.length)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BlacklistModel

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (path != other.path) return false
        if (isBlacklisted != other.isBlacklisted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + isBlacklisted.hashCode()
        return result
    }


}