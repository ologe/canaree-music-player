package dev.olog.msc.floating.window.service.music.service

import dev.olog.msc.presentation.model.DisplayableItem

data class MusicServiceMetadata(
        val id: Long,
        val title: String,
        val artist: String,
        val image: DisplayableItem
)