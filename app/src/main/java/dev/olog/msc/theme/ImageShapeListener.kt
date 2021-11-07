package dev.olog.msc.theme

import dev.olog.feature.main.MainPrefs
import dev.olog.shared.android.theme.ImageShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class ImageShapeListener @Inject constructor(
    appScope: CoroutineScope,
    mainPrefs: MainPrefs,
) : BaseThemeUpdater<ImageShape>(appScope, mainPrefs.imageShape) {

    private val _flow = MutableStateFlow(mainPrefs.imageShape.get())
    val flow: Flow<ImageShape> = _flow
    fun imageShape() = _flow.value

    override fun onPrefsChanged(value: ImageShape) {
        _flow.value = value
    }

}