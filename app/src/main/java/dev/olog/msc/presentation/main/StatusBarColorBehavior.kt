package dev.olog.msc.presentation.main

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.v4.app.FragmentManager
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.removeLightStatusBar
import dev.olog.msc.utils.k.extension.setLightStatusBar
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class StatusBarColorBehavior @Inject constructor(
    private val activity: MainActivity

) : DefaultLifecycleObserver,
        SlidingUpPanelLayout.PanelSlideListener,
        FragmentManager.OnBackStackChangedListener {

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!isMarshmallow()){
            return
        }

        activity.addPanelSlideListener(this)
        activity.supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        if (!isMarshmallow()){
            return
        }

        activity.removePanelSlideListener(this)
        activity.supportFragmentManager.removeOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        if (!isMarshmallow()){
            return
        }

        val fragment = searchForDetailFragmentOnPortraitMode()
        if (fragment == null){
            activity.window.setLightStatusBar()
        } else {
            if (activity.slidingPanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED){
                activity.window.setLightStatusBar()
            } else {
                fragment.adjustStatusBarColor()
            }
        }
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onPanelStateChanged(panel: View, previousState: SlidingUpPanelLayout.PanelState, newState: SlidingUpPanelLayout.PanelState) {
        if (!isMarshmallow()){
            return
        }

        when (newState){
            SlidingUpPanelLayout.PanelState.EXPANDED -> {
                if (AppTheme.isFullscreen() || AppTheme.isBigImage()){
                    activity.window.removeLightStatusBar()
                } else {
                    activity.window.setLightStatusBar()
                }
            }
            SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                searchForDetailFragmentOnPortraitMode()?.adjustStatusBarColor()
                    ?: activity.window.setLightStatusBar()
            }
        }
    }

    private fun searchForDetailFragmentOnPortraitMode(): DetailFragment? {
        if (activity.isPortrait){
            val fm = activity.supportFragmentManager
            val backStackEntryCount = fm.backStackEntryCount - 1
            if (backStackEntryCount > -1){
                val entry = fm.getBackStackEntryAt(backStackEntryCount)
                val fragment = fm.findFragmentByTag(entry.name)
                if (fragment is DetailFragment){
                    return fragment
                }
            }
        }
        return null
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
    }

}