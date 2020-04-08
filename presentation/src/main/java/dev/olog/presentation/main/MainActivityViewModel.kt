package dev.olog.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.presentation.PresentationId
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.Permissions
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

internal class MainActivityViewModel @Inject constructor(
    private val context: Context,
    private val presentationPrefs: PresentationPreferencesGateway
) : ViewModel() {

    fun isFirstAccess(): Boolean {
        val canReadStorage = Permissions.canReadStorage(context)
        val isFirstAccess = presentationPrefs.isFirstAccess()
        return !canReadStorage || isFirstAccess
    }

}