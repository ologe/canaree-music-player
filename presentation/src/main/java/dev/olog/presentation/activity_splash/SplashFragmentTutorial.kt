package dev.olog.presentation.activity_splash

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.widgets.SwipeableImageView
import dev.olog.shared_android.CoverUtils

import kotlinx.android.synthetic.main.fragment_splash_tutorial.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.view.*

class SplashFragmentTutorial : BaseFragment() {

    private var isFavorite = false

    private var progressive = 0

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        loadImage(view.cover, progressive)
    }

    override fun onStart() {
        super.onStart()
        view!!.favorite.setMinProgress(0f)
        view!!.favorite.toggleFavorite(false)
    }

    override fun onResume() {
        super.onResume()
        view!!.favorite.setOnClickListener {
            view!!.favorite.setMinProgress(.35f)
            isFavorite = !isFavorite
            view!!.favorite.animateFavorite(isFavorite)
        }

        view!!.cover.setOnSwipeListener(object : SwipeableImageView.SwipeListener {

            override fun onSwipedLeft() {
                loadNextImage()

            }

            override fun onSwipedRight() {
                loadPreviousImage()
            }

            override fun onClick() {
                val newState = !view!!.coverLayout.isActivated
                view!!.coverLayout.isActivated = newState
                view!!.nowPlaying.isActivated = newState
            }
        })

        view!!.fakeNext.setOnClickListener { loadNextImage() }
        view!!.fakePrevious.setOnClickListener { loadPreviousImage() }
    }

    override fun onPause() {
        super.onPause()
        view!!.coverLayout.setOnClickListener(null)
        view!!.favorite.setOnClickListener(null)
        view!!.cover.setOnSwipeListener(null)
        view!!.fakeNext.setOnClickListener(null)
        view!!.fakePrevious.setOnClickListener(null)
    }

    private fun loadNextImage(){
        progressive++
        loadImage(view!!.cover, progressive)
    }

    private fun loadPreviousImage(){
        progressive--
        loadImage(view!!.cover, progressive)
    }

    private fun loadImage(view: ImageView, position: Int){
        GlideApp.with(context)
                .load(Uri.EMPTY)
                .centerCrop()
                .placeholder(CoverUtils.getGradient(context!!, position = position))
                .into(view)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_splash_tutorial
}