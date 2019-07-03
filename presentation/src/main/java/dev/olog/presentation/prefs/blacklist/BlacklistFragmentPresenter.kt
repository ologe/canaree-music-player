package dev.olog.presentation.prefs.blacklist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.getMediaId
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.presentation.R
import dev.olog.presentation.model.BaseModel
import kotlinx.coroutines.*
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) : CoroutineScope by MainScope() {

    private val data = MutableLiveData<List<BlacklistModel>>()

    init {
        launch(Dispatchers.Default) {
            delay(100)
            val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase() }
            val folders = folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
            withContext(Dispatchers.Main) {
                data.value = folders
            }
        }
    }

    fun observeData(): LiveData<List<BlacklistModel>> = data

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            R.layout.dialog_blacklist_item,
            getMediaId(),
            this.title,
            this.path,
            blacklisted.contains(this.path.toLowerCase())
        )
    }

    fun saveBlacklisted(data: List<BlacklistModel>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { it.path }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
    }


}

data class BlacklistModel(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val path: String,
    var isBlacklisted: Boolean
) : BaseModel