package dev.olog.feature.detail

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId

interface FeatureDetailNavigator {

    fun toDetailFragment(activity: FragmentActivity, mediaId: MediaId)
    fun toRecentlyAdded(activity: FragmentActivity, mediaId: MediaId)
    fun toRelatedArtists(activity: FragmentActivity, mediaId: MediaId)

}