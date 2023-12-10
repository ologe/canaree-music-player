package dev.olog.presentation.splash

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.widgets.StoppingViewPager
import dev.olog.presentation.widgets.swipeableview.SwipeableView
import androidx.lifecycle.asLiveData
import dev.olog.presentation.databinding.FragmentSplashTutorialBinding
import dev.olog.shared.android.extensions.ctx
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.viewBinding

class SplashTutorialFragment : Fragment(R.layout.fragment_splash_tutorial),
    SwipeableView.SwipeListener {

    private var progressive = 0

    private lateinit var viewPager : StoppingViewPager
    private val binding by viewBinding(FragmentSplashTutorialBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = parentFragment!!.view!!.findViewById(R.id.viewPager)

        binding.swipeableView.isTouching()
            .asLiveData()
            .subscribe(this) {
                viewPager.isSwipeEnabled = !it
            }

        loadPhoneImage(view)
        loadImage(progressive)
    }

    override fun onResume() {
        super.onResume()
        binding.swipeableView.setOnSwipeListener(this)
    }

    override fun onPause() {
        super.onPause()
        binding.swipeableView.setOnSwipeListener(null)
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
        val newState = !binding.cover.isActivated
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
        binding.coverWrapper.isActivated = activated
        binding.nowPlaying.isActivated = activated
    }

    private fun loadNextImage(){
        loadImage(++progressive)
    }

    private fun loadPreviousImage(){
        loadImage(--progressive)
    }

    private fun loadPhoneImage(view: View){
        GlideApp.with(requireContext())
                .asBitmap()
                .load(R.drawable.phone_black)
                .priority(Priority.IMMEDIATE)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        view.findViewById<ImageView>(R.id.phoneImage).setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    private fun loadImage(position: Int){
        GlideApp.with(ctx).clear(binding.cover)

        GlideApp.with(ctx)
                .load(Uri.EMPTY)
                .centerCrop()
                .placeholder(CoverUtils.getGradient(ctx, position))
                .into(binding.cover)
    }
}