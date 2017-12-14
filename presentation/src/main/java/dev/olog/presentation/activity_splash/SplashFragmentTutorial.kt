package dev.olog.presentation.activity_splash

import android.os.Bundle
import android.view.View
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.fragment_splash_tutorial.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.view.*
import javax.inject.Inject

class SplashFragmentTutorial : BaseFragment() {

    private var isFavorite = false

    @Inject lateinit var presenter: SplashFragmentPresenter
    @Inject lateinit var navigator: Navigator

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        presenter.subscribeToStoragePermission(view.container)
                .asLiveData()
                .subscribe(this, { navigator.toMainActivity() })

        view.favorite.progress = 0f
    }

    override fun onResume() {
        super.onResume()
        view!!.coverLayout.setOnClickListener {
            val newState = !view!!.coverLayout.isActivated
            view!!.coverLayout.isActivated = newState
            view!!.nowPlaying.isActivated = newState
        }
        view!!.favorite.setOnClickListener {
            isFavorite = !isFavorite
            view!!.favorite.animateFavorite(isFavorite)
        }
    }

    override fun onPause() {
        super.onPause()
        view!!.coverLayout.setOnClickListener(null)
        view!!.favorite.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_splash_tutorial
}