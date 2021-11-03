package dev.olog.feature.base

import android.view.View
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import javax.inject.Inject


class Navigator @Inject constructor() {

    fun toDetailFragment(mediaId: MediaId) {
        TODO("implement")
    }

    fun toDialog(mediaId: MediaId, view: View) {
        TODO("implement")
    }

    fun toChooseTracksForPlaylistFragment(type: PlaylistType) {
        TODO("implement")
    }

    fun toMainPopup(view: View, category: MediaIdCategory?) {
        TODO("implement")
    }

}