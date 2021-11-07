package dev.olog.msc.theme

import dev.olog.feature.main.MainPrefs
import dev.olog.shared.android.theme.QuickAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class QuickActionListener @Inject constructor(
    appScope: CoroutineScope,
    mainPrefs: MainPrefs,
) : BaseThemeUpdater<QuickAction>(appScope, mainPrefs.quickAction) {

    private val _flow = MutableStateFlow(mainPrefs.quickAction.get())
    val flow: Flow<QuickAction> = _flow
    fun quickAction() = _flow.value

    override fun onPrefsChanged(value: QuickAction) {
        _flow.value = value
    }

}

