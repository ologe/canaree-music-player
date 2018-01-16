package dev.olog.presentation.activity_preferences.neural_network

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.activity_preferences.neural_network.style_chooser.NeuralNetworkImageChooser
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_neural_network.view.*
import javax.inject.Inject

class NeuralNetworkFragment : BaseFragment() {

    companion object {
        const val TAG = "NeuralNetworkFragment"

        fun newInstance(): NeuralNetworkFragment {
            return NeuralNetworkFragment()
        }
    }

    @Inject lateinit var viewModel: NeuralNetworkFragmentViewModel
    private var disposable: Disposable? = null
    private var currentImage: String? = null
    private var stylezedImageDisposable : Disposable? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentNeuralStyle.subscribe(this, {

            val uri = NeuralImages.getThumbnail(it)

            view!!.addFilter.visibility = View.GONE

            GlideApp.with(context!!)
                    .load(uri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(300)
                    .priority(Priority.IMMEDIATE)
                    .into(view!!.style)

            stylezedImageDisposable.unsubscribe()
            stylezedImageDisposable = Single.create<Bitmap> { emitter ->

                val bitmap = NeuralImages.stylizeTensorFlow(activity!!, ImageUtils.getBitmapFromUri(activity!!, currentImage)!!)
                emitter.onSuccess(bitmap)

            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { view!!.progressBar.visibility = View.VISIBLE }
                    .doOnEvent { _,_ ->view!!.progressBar.visibility = View.GONE }
                    .subscribe({ bitmap ->

                        GlideApp.with(activity!!)
                                .load(bitmap)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .priority(Priority.IMMEDIATE)
                                .into(view!!.previewNeuralImage)

                    }, Throwable::printStackTrace)


        })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        disposable = viewModel.getImagesAlbum.subscribe({
            val first = it[0]

            currentImage = first.image

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
        view!!.style.setOnClickListener {
            NeuralNetworkImageChooser.newInstance().show(activity!!.supportFragmentManager,
                            NeuralNetworkImageChooser.TAG)
        }
    }

    override fun onPause() {
        super.onPause()
        view!!.style.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        stylezedImageDisposable.unsubscribe()
        disposable.unsubscribe()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_neural_network
}