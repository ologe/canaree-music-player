package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.ApplicationContext
import dev.olog.shared.android.Permissions
import javax.inject.Inject

internal class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {

    private val _currentPlaying = MutableLiveData<MediaId>()

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

    fun setCurrentPlaying(mediaId: MediaId) {
        _currentPlaying.value = mediaId
    }

    val observeCurrentPlaying: LiveData<MediaId> = _currentPlaying

}