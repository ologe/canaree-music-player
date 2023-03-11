package dev.olog.feature.widget.api

interface FeatureWidgetNavigator {

    fun updateMetadata(
        audioId: Long,
        title: String,
        artist: String,
    )

    fun updateState(
        isPlaying: Boolean,
        bookmark: Long,
    )

    fun updateActions(
        showPrevious: Boolean,
        showNext: Boolean,
    )

}