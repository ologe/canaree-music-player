package dev.olog.presentation.dialog_entry

import io.reactivex.Completable

data class DialogModel(
        val viewType: Int,
        val mediaId: String,
        val title: String,
        val subtitle: String? = null,
        val image: String? = null,
        val canViewAlbum: Boolean = false,
        val canViewArtist: Boolean = false,
        val useCase: Completable?
)