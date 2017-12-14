package dev.olog.presentation.activity_splash

import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import kotlinx.android.synthetic.main.fragment_splash_tutorial.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.view.*

class SplashFragmentTutorial : BaseFragment() {

    private var isFavorite = false

    override fun onStart() {
        super.onStart()
        view!!.favorite.setMinProgress(0f)
        view!!.favorite.toggleFavorite(false)
    }

    override fun onResume() {
        super.onResume()
        view!!.favorite.setMinProgress(.35f)
        view!!.cover.setOnClickListener {
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