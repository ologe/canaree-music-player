package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.presentation.R
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.android.coroutine.viewScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class VolumeChangerView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    init {
        setImageResource(R.drawable.vd_volume_up)
    }

    @Inject
    lateinit var musicPrefs: MusicPreferencesGateway

    private var job by autoDisposeJob()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode){
            startObserving()
        }
    }

    private fun startObserving() {
        job = musicPrefs.observeVolume()
            .flowOn(Dispatchers.Default)
            .onEach(this::updateImage)
            .launchIn(viewScope)
    }

    private fun updateImage(volume: Int) {
        val drawable = when (volume) {
            0 -> R.drawable.vd_volume_mute
            in 1..60 -> R.drawable.vd_volume_down
            else -> R.drawable.vd_volume_up
        }
        setImageResource(drawable)
    }

}