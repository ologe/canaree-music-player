package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.Permissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val playingQueueGateway: PlayingQueueGateway
) : ViewModel() {

    private val hasPlayingQueueLiveData = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            playingQueueGateway.observeAll()
                .distinctUntilChanged()
                .map { it.isNotEmpty() }
                .debounce(500)
                .flowOn(Dispatchers.IO)
                .collect { hasPlayingQueueLiveData.value = it }
        }
    }

    fun observeHasPlayingQueue(): LiveData<Boolean> = hasPlayingQueueLiveData

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    fun canShowAds(): Boolean {
        return prefsGateway.canShowAds()
    }

}