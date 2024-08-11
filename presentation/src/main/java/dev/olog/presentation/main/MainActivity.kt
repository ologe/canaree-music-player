package dev.olog.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.appshortcuts.Shortcuts
import dev.olog.core.MediaId
import dev.olog.intents.AppConstants
import dev.olog.intents.Classes
import dev.olog.intents.FloatingWindowsConstants
import dev.olog.intents.MusicServiceAction
import dev.olog.presentation.FloatingWindowHelper
import dev.olog.presentation.R
import dev.olog.presentation.databinding.ActivityMainBinding
import dev.olog.presentation.interfaces.CanHandleOnBackPressed
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.interfaces.HasBottomNavigation
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.interfaces.OnPermissionChanged
import dev.olog.presentation.interfaces.Permission
import dev.olog.presentation.model.BottomNavigationPage
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.rateapp.RateAppDialog
import dev.olog.presentation.utils.collapse
import dev.olog.presentation.utils.expand
import dev.olog.presentation.utils.isExpanded
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.scrollhelper.ScrollType
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.getTopFragment
import dev.olog.shared.android.extensions.isTablet
import dev.olog.shared.android.extensions.setHeight
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.theme.isImmersiveMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MusicGlueActivity(),
    HasSlidingPanel,
    HasBottomNavigation,
    OnPermissionChanged {

    private val viewModel by viewModels<MainActivityViewModel>()
    @Inject
    lateinit var navigator: Navigator

    @Inject
    internal lateinit var presentationPrefs: PresentationPreferencesGateway

    @Suppress("unused")
    @Inject
    lateinit var statusBarColorBehavior: StatusBarColorBehavior
    @Suppress("unused")
    @Inject
    lateinit var rateAppDialog: RateAppDialog

    // todo generalize and improve
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.rootView)


        if (isImmersiveMode()){
            // workaround, on some device on immersive mode bottom navigation disappears
            binding.rootView.fitsSystemWindows = true
            binding.slidingPanel.fitsSystemWindows = true
            binding.bottomWrapper.fitsSystemWindows = true
        }

        hasPlayerAppearance().observePlayerAppearance()
            .onEach {
                if (it == PlayerAppearance.MINI) {
                    // TODO made a resource value
                    binding.slidingPanelFade.parallax = 0
                    // TODO height seems too much, update
                    //   also find a better way to do this
                    binding.slidingPanel.setHeight(dip(300))
                } else {
                    binding.slidingPanelFade.parallax = dip(20)
                    binding.slidingPanel.setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
            .launchIn(lifecycleScope)

        setupSlidingPanel()

        when {
            viewModel.isFirstAccess() -> {
                navigator.toFirstAccess()
                return
            }
            savedInstanceState == null -> navigateToLastPage()
        }

        intent?.let { handleIntent(it) }
    }

    override fun onPermissionGranted(permission: Permission) = when (permission){
        Permission.STORAGE -> {
            navigateToLastPage()
            connect()
        }
    }

    private fun setupSlidingPanel(){
        if (!isTablet) {
            val scrollHelper = ActivityScrollHelper(
                this, ScrollType.Full(
                    slidingPanel = binding.slidingPanel,
                    bottomNavigation = binding.bottomWrapper,
                    toolbarHeight = dimen(R.dimen.toolbar),
                    tabLayoutHeight = dimen(R.dimen.tab),
                    realSlidingPanelPeek = dimen(R.dimen.sliding_panel_peek)
                )
            )
            lifecycle.addObserver(scrollHelper)
        }
    }

    private fun navigateToLastPage(){
        binding.bottomNavigation.navigateToLastPage()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            FloatingWindowsConstants.ACTION_START_SERVICE -> {
                FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
            }
            Shortcuts.SEARCH -> binding.bottomNavigation.navigate(BottomNavigationPage.SEARCH)
            AppConstants.ACTION_CONTENT_VIEW -> getSlidingPanel().expand()
            MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH -> {
                val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
                serviceIntent.action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Shortcuts.DETAIL -> {
                lifecycleScope.launch {
                    delay(250)
                    val string = intent.getStringExtra(Shortcuts.DETAIL_EXTRA_ID)!!
                    val mediaId = MediaId.fromString(string)
                    navigator.toDetailFragment(mediaId)
                }
            }
            Intent.ACTION_VIEW -> {
                val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
                serviceIntent.action = MusicServiceAction.PLAY_URI.name
                serviceIntent.data = intent.data
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
        intent.action = null
        setIntent(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FloatingWindowHelper.REQUEST_CODE_HOVER_PERMISSION) {
            FloatingWindowHelper.startServiceIfHasOverlayPermission(this)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        try {
            val topFragment = supportFragmentManager.getTopFragment()
            if (topFragment is DrawsOnTop) {
                super.onBackPressed()
                return
            }
            if (getSlidingPanel().isExpanded()) {
                getSlidingPanel().collapse()
                return
            }
            if (topFragment is CanHandleOnBackPressed && topFragment.handleOnBackPressed()) {
                return
            }
            if (topFragment == null) {
                for (fragment in supportFragmentManager.fragments) {
                    if (fragment.isVisible && fragment is CanHandleOnBackPressed && fragment.handleOnBackPressed()) {
                        return
                    }
                }
            }


            super.onBackPressed()
        } catch (ex: IllegalStateException) {
            /*random fragment manager crashes */
            ex.printStackTrace()
        }

    }

    override fun getSlidingPanel(): MultiListenerBottomSheetBehavior<*> {
        return BottomSheetBehavior.from(binding.slidingPanel) as MultiListenerBottomSheetBehavior<*>
    }

    override fun navigate(page: BottomNavigationPage) {
        binding.bottomNavigation.navigate(page)
    }

    fun restoreUpperWidgetsTranslation(){
        findViewById<View>(R.id.toolbar)?.animate()?.translationY(0f)
        findViewById<View>(R.id.tabLayout)?.animate()?.translationY(0f)
    }
}