package dev.olog.presentation.splash

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.widgets.StoppingViewPager
import dev.olog.presentation.widgets.SwipeableView
import dev.olog.image.provider.CoverUtils
import dev.olog.presentation.R
import dev.olog.shared.extensions.ctx
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.disposables.Disposable

class SplashTutorialFragment : Fragment(), SwipeableView.SwipeListener {

    private var progressive = 0

    private lateinit var cover : ImageView
    private lateinit var nowPlaying: TextView
    private lateinit var coverWrapper: View
    private lateinit var swipeableView : SwipeableView
    private lateinit var viewPager : StoppingViewPager

    private var touchDisposable : Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nowPlaying = view.findViewById(R.id.nowPlaying)
        viewPager = activity!!.findViewById(R.id.viewPager)
        cover = view.findViewById(R.id.cover)
        swipeableView = view.findViewById(R.id.swipeableView)
        coverWrapper = view.findViewById(R.id.coverWrapper)

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
        coverWrapper.isActivated = activated
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
        GlideApp.with(ctx)
                .asBitmap()
                .load(R.drawable.phone_black)
                .priority(Priority.IMMEDIATE)
                .into(object : SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        view.findViewById<ImageView>(R.id.phoneImage).setImageBitmap(resource)
                    }
                })
    }

    private fun loadImage(view: ImageView, position: Int){
        GlideApp.with(ctx).clear(view)

        GlideApp.with(ctx)
                .load(Uri.EMPTY)
                .centerCrop()
                .placeholder(CoverUtils.getGradient(ctx, position))
                .priority(Priority.IMMEDIATE)
                .override(400)
                .into(view)
    }
}