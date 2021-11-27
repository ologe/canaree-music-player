package dev.olog.feature.library.blacklist

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.BlacklistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.shared.launchUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BlacklistFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
            val blacklisted = blacklistGateway.getBlacklist()
                .map { it.path.lowercase() }
            val data = folderGateway.getAllBlacklistedIncluded()
                .map { it.toDisplayableItem(blacklisted) }
            _items.value = data
        }
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            mediaId = getMediaId(),
            title = this.title,
            path = this.path,
            isBlacklisted = blacklisted.contains(this.path.lowercase())
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
        notifyMediaStore()
        _events.emit(Event.Dismiss)
    }

    fun toggleBlacklisted(item: BlacklistModel) {
        _items.value = _items.value.map {
            if (item == it) it.copy(isBlacklisted = !it.isBlacklisted) else it
        }
    }

    private fun notifyMediaStore() {
        val contentResolver = context.contentResolver
        contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
        contentResolver.notifyChange(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, null)
    }

}