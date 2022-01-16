package dev.olog.feature.library.blacklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.folder.Folder
import dev.olog.core.blacklist.BlacklistGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.shared.launchUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BlacklistFragmentViewModel @Inject constructor(
    folderGateway: FolderGateway,
    private val blacklistGateway: BlacklistGateway,
) : ViewModel() {

    sealed class Event {
        object Dismiss : Event()
        object BlacklistAllError : Event()
    }

    private val _events = MutableSharedFlow<Event>()
    val events: Flow<Event> = _events

    private val _items = MutableStateFlow(emptyList<BlacklistModel>())
    val items: Flow<List<BlacklistModel>> = _items

    init {
        viewModelScope.launch {
            val blacklisted = blacklistGateway.observeBlacklist().first()
                .map { it.path.lowercase() } // TODO why lowercase?
            val data = folderGateway.getAllBlacklistedIncluded()
                .map { it.toDisplayableItem(blacklisted) }
            _items.value = data
        }
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            uri = uri,
            title = this.title,
            path = this.directory,
            isBlacklisted = blacklisted.contains(this.directory.lowercase())
        )
    }

    fun saveBlacklisted(data: List<BlacklistModel>) = viewModelScope.launchUnit {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { File(it.path) }
            .distinctBy { it.path }
        blacklistGateway.setBlacklist(blacklisted)
    }

    fun onSaveClick() = viewModelScope.launchUnit {
        val allIsBlacklisted = _items.value.all { it.isBlacklisted }
        if (allIsBlacklisted) {
            _events.emit(Event.BlacklistAllError)
            return@launchUnit
        }
        val toBlacklist = _items.value.filter { it.isBlacklisted }
            .map { it.path }
            .distinct()
            .map(::File)

        blacklistGateway.setBlacklist(toBlacklist)
        _events.emit(Event.Dismiss)
    }

    fun toggleBlacklisted(item: BlacklistModel) {
        _items.value = _items.value.map {
            if (item == it) it.copy(isBlacklisted = !it.isBlacklisted) else it
        }
    }

}