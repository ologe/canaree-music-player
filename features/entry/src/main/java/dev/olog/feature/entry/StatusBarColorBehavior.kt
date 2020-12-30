package dev.olog.feature.entry

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.feature.base.CanChangeStatusBarColor
import dev.olog.shared.widgets.extension.removeLightStatusBar
import dev.olog.shared.widgets.extension.setLightStatusBar
import dev.olog.shared.android.slidingPanel
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.android.utils.isMarshmallow
import javax.inject.Inject

internal class StatusBarColorBehavior @Inject constructor(
    private val activity: FragmentActivity,
) : FragmentManager.OnBackStackChangedListener {

    private val observer = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            slidingPanel.addBottomSheetCallback(slidingPanelListener)
            activity.supportFragmentManager.addOnBackStackChangedListener(this@StatusBarColorBehavior)
        }

        override fun onPause(owner: LifecycleOwner) {
            slidingPanel.removeBottomSheetCallback(slidingPanelListener)
            activity.supportFragmentManager.removeOnBackStackChangedListener(this@StatusBarColorBehavior)
        }
    }

    init {
        if (isMarshmallow()) {
            activity.lifecycle.addObserver(observer)
        }
    }

    private val slidingPanel: BottomSheetBehavior<*>
        get() = activity.slidingPanel

    override fun onBackStackChanged() {
        val fragment = searchFragmentWithLightStatusBar(activity)
        if (fragment == null){
            activity.window.setLightStatusBar()
        } else {
            if (slidingPanel.state == BottomSheetBehavior.STATE_EXPANDED){
                activity.window.setLightStatusBar()
            } else {
                fragment.adjustStatusBarColor()
            }
        }
    }

    private fun searchFragmentWithLightStatusBar(activity: FragmentActivity): CanChangeStatusBarColor? {
        val fm = activity.supportFragmentManager
        val backStackEntryCount = fm.backStackEntryCount - 1
        if (backStackEntryCount > -1) {
            val entry = fm.getBackStackEntryAt(backStackEntryCount)
            val fragment = fm.findFragmentByTag(entry.name)
            if (fragment is CanChangeStatusBarColor) {
                return fragment
            }
        }
        return null
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }

        @SuppressLint("SwitchIntDef")
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    val playerAppearanceAmbient = activity.playerAppearanceAmbient
                    if (playerAppearanceAmbient.isFullscreen() || playerAppearanceAmbient.isBigImage()) {
                        activity.window.removeLightStatusBar()
                    } else {
                        activity.window.setLightStatusBar()
                    }
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    searchFragmentWithLightStatusBar(activity)?.adjustStatusBarColor() ?: activity.window.setLightStatusBar()
                }
            }
        }
    }

}