package dev.olog.msc.presentation.splash.tutorial

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.widget.StoppingViewPager
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable

class SplashTutorialFragment : BaseFragment(), SwipeableView.SwipeListener {

    private var progressive = 0

    private lateinit var cover : ImageView
    private lateinit var nowPlaying: TextView
    private lateinit var swipeableView : SwipeableView
    private lateinit var viewPager : StoppingViewPager

    private var touchDisposable : Disposable? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        nowPlaying = view.findViewById(R.id.nowPlaying)
        viewPager = activity!!.findViewById(R.id.viewPager)
        cover = view.findViewById(R.id.cover)
        swipeableView = view.findViewById(R.id.swipeableView)

        loadPhoneImage(view)
        loadImage(cover, progressive)
    }

    override fun onResume() {
        super.onResume()
        swipeableView.setOnSwipeListener(this)
        touchDisposable = swipeableView.isTouching()
                .subscribe({ viewPager.isSwipeEnabled = !it }, Throwable::printStackTrace)
    }

    override fun onPause() {
        super.onPause()
        swipeableView.setOnSwipeListener(null)
        touchDisposable.unsubscribe()
    }

    override fun onSwipedLeft() {
        loadNextImage()
        setActivated(true)
    }

    override fun onSwipedRight() {
        loadPreviousImage()
        setActivated(true)
    }

    override fun onClick() {
        val newState = !cover.isActivated
        setActivated(newState)
    }

    override fun onLeftEdgeClick() {
        loadPreviousImage()
        setActivated(true)
    }

    override fun onRightEdgeClick() {
        loadNextImage()
        setActivated(true)
    }

    private fun setActivated(activated: Boolean){
        cover.isActivated = activated
        nowPlaying.isActivated = activated
    }

    private fun loadNextImage(){
        progressive++
        loadImage(cover, progressive)
    }

    private fun loadPreviousImage(){
        progressive--
        loadImage(cover, progressive)
    }

    private fun loadPhoneImage(view: View){
        GlideApp.with(context!!)
                .load(R.drawable.phone_black)
                .priority(Priority.IMMEDIATE)
                .into(view.findViewById(R.id.phoneImage))
    }

    private fun loadImage(view: ImageView, position: Int){
        GlideApp.with(context!!).clear(view)

        GlideApp.with(context!!)
                .load(Uri.EMPTY)
                .centerCrop()
                .placeholder(CoverUtils.getGradient(context!!, position))
                .priority(Priority.IMMEDIATE)
                .override(400)
                .into(view)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_splash_tutorial
}