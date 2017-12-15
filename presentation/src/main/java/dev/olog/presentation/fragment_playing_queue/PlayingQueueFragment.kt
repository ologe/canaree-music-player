package dev.olog.presentation.fragment_playing_queue

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewAnimationUtils
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.utils.extension.subscribe
import kotlinx.android.synthetic.main.fragment_playing_queue.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import javax.inject.Inject

class PlayingQueueFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayingQueueFragment"
        private const val ANIMATION_DONE = "$TAG.ANIMATION_DONE"
    }

    @Inject lateinit var adapter: PlayingQueueFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager
    @Inject lateinit var viewModel : PlayingQueueFragmentViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.data.subscribe(this, adapter::updateDataSet)
        viewModel.metadata.subscribe(this, {})
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)

        if (savedInstanceState == null){
            view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                    v?.removeOnLayoutChangeListener(this)
                    startCircularReveal()
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ANIMATION_DONE, true)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_playing_queue

    private fun startCircularReveal(){
        val background = view!!.root
        val searchView = activity!!.playingQueue
        val cx = (searchView.x + searchView.width / 2).toInt()
        val cy = (searchView.y + searchView.height / 2).toInt()
        val width = background.width
        val height = background.height
        val finalRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(background, cx, cy, 0f, finalRadius)
        anim.start()

        val valueAnimator = ValueAnimator()
        valueAnimator.setIntValues(0xfff0f0f0.toInt(), Color.WHITE)
        valueAnimator.setEvaluator(ArgbEvaluator())
        valueAnimator.addUpdateListener { background.setBackgroundColor(it.animatedValue as Int) }
        valueAnimator.start()
    }
}