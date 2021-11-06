package dev.olog.presentation.navigator

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import dagger.Lazy
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.PlaylistType
import dev.olog.feature.base.allowed
import dev.olog.feature.base.createBackStackTag
import dev.olog.feature.base.superCerealTransition
import dev.olog.feature.dialogs.popup.PopupMenuFactory
import dev.olog.feature.playlist.create.CreatePlaylistFragment
import dev.olog.feature.splash.SplashFragment
import dev.olog.shared.android.extensions.fragmentTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject

class NavigatorImpl @Inject internal constructor(
    activity: FragmentActivity,
    private val popupFactory: Lazy<PopupMenuFactory>,
) : DefaultLifecycleObserver, Navigator {

    private val activityRef = WeakReference(activity)

    override fun toFirstAccess() {
        val activity = activityRef.get() ?: return
        activity.fragmentTransaction {
            add(android.R.id.content, SplashFragment(), SplashFragment.TAG)
        }
    }

    override fun toDetailFragment(mediaId: MediaId) {
        TODO()
    }

    override fun toRelatedArtists(mediaId: MediaId) {
        TODO()
    }

    override fun toRecentlyAdded(mediaId: MediaId) {
        TODO()
    }

    override fun toOfflineLyrics() {
//        val activity = activityRef.get() ?: return TODO
//        if (!allowed()) {
//            return
//        }
//        activity.fragmentTransaction {
//            setReorderingAllowed(true)
//            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//            add(
//                android.R.id.content,
//                OfflineLyricsFragment.newInstance(),
//                OfflineLyricsFragment.TAG
//            )
//            addToBackStack(OfflineLyricsFragment.TAG)
//        }
    }

    override fun toChooseTracksForPlaylistFragment(type: PlaylistType) {
        val activity = activityRef.get() ?: return
        val newTag = createBackStackTag(CreatePlaylistFragment.TAG)
        superCerealTransition(
            activity,
            CreatePlaylistFragment.newInstance(type),
            newTag
        )
    }

    override fun toDialog(mediaId: MediaId, anchor: View) {
        if (allowed()) {
            GlobalScope.launch {
                val popup = popupFactory.get().create(anchor, mediaId)
                withContext(Dispatchers.Main) {
                    popup.show()
                }
            }
        }
    }

    override fun toMainPopup(anchor: View, category: MediaIdCategory?) {
        TODO()
    }


}
