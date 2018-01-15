package dev.olog.presentation.activity_preferences.neural_network

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.shared.unsubscribe
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_neural_network.view.*
import javax.inject.Inject

class NeuralNetworkFragment : BaseFragment() {

    companion object {
        const val TAG = "NeuralNetworkFragment"

        fun newInstance(): NeuralNetworkFragment {
            return NeuralNetworkFragment()
        }
    }

    @Inject lateinit var presenter: NeuralNetworkPresenter
    private var disposable: Disposable? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        disposable = presenter.getImagesAlbum.subscribe({
            val first = it[0]

            GlideApp.with(context!!)
                    .load(Uri.parse(first.image))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(300)
                    .priority(Priority.IMMEDIATE)
                    .into(view.cover)

        }, Throwable::printStackTrace)
    }

    override fun onResume() {
        super.onResume()
        view!!.addFilter.setOnClickListener {
            NeuralNetworkImageChoiser.newInstance().show(activity!!.supportFragmentManager,
                            NeuralNetworkImageChoiser.TAG)
        }
    }

    override fun onPause() {
        super.onPause()
        view!!.addFilter.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        disposable.unsubscribe()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_neural_network
}