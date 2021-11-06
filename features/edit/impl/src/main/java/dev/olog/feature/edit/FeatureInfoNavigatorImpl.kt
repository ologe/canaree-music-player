package dev.olog.feature.edit

import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.feature.base.allowed
import dev.olog.feature.edit.author.EditArtistFragment
import dev.olog.feature.edit.collection.EditAlbumFragment
import dev.olog.feature.edit.track.EditTrackFragment
import javax.inject.Inject

class FeatureInfoNavigatorImpl @Inject constructor(
    private val editItemDialogFactory: EditItemDialogFactory
) : FeatureInfoNavigator {

    override fun toEditInfoFragment(activity: FragmentActivity, mediaId: MediaId) {
        if (allowed()) {
            when {
                mediaId.isLeaf -> {
                    editItemDialogFactory.toEditTrack(mediaId) {
                        val instance = EditTrackFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditTrackFragment.TAG)
                    }
                }
                mediaId.isAlbum || mediaId.isPodcastAlbum -> {
                    editItemDialogFactory.toEditAlbum(mediaId) {
                        val instance = EditAlbumFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditAlbumFragment.TAG)
                    }
                }
                mediaId.isArtist || mediaId.isPodcastArtist -> {
                    editItemDialogFactory.toEditArtist(mediaId) {
                        val instance = EditArtistFragment.newInstance(mediaId)
                        instance.show(activity.supportFragmentManager, EditArtistFragment.TAG)
                    }
                }
                else -> throw IllegalArgumentException("invalid media id $mediaId")
            }
        }
    }
}