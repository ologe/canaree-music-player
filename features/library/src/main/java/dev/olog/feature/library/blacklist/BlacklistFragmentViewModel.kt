package dev.olog.feature.library.blacklist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.entity.track.Folder
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.prefs.BlacklistPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

internal class BlacklistFragmentViewModel @ViewModelInject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
): ViewModel() {

    private val _data = MutableStateFlow<List<BlacklistFragmentModel>>(emptyList())
    val data: Flow<List<BlacklistFragmentModel>>
        get() = _data

    init {
        viewModelScope.launch {
            val blacklisted = appPreferencesUseCase.getBlackList()
                .map { it.toLowerCase(Locale.getDefault()) }
            val result = folderGateway.getAllBlacklistedIncluded()
                .map { it.toDisplayableItem(blacklisted) }

            _data.value = result
        }
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistFragmentModel {
        return BlacklistFragmentModel(
            getMediaId(),
            this.title,
            this.path,
            blacklisted.contains(this.path.toLowerCase(Locale.getDefault()))
        )
    }

    fun saveBlacklisted(data: List<BlacklistFragmentModel>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { it.path }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
    }


}

