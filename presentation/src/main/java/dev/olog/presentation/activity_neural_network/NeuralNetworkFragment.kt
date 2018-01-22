package dev.olog.presentation.activity_neural_network

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.activity_neural_network.image_chooser.NeuralNetworkImageChooser
import dev.olog.presentation.activity_neural_network.style_chooser.NeuralNetworkStyleChooser
import dev.olog.presentation.activity_preferences.neural_network.service.NeuralNetworkService
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.ImageUtils
import dev.olog.shared_android.neural.NeuralImages
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_neural_network_result_chooser.view.*
import javax.inject.Inject

class NeuralNetworkFragment : BaseFragment() {

    @Inject lateinit var viewModel: NeuralNetworkActivityViewModel
    private var stylezedImageDisposable: Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.currentNeuralImage.subscribe(this, {

            view!!.chooseImage.visibility = View.GONE

            GlideApp.with(this)
                    .load(Uri.parse(it))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(300)
                    .priority(Priority.IMMEDIATE)
                    .into(view!!.cover)

        })

        viewModel.currentNeuralStyle.subscribe(this, {

            val uri = NeuralImages.getThumbnail(it)

            view!!.chooseStyle.visibility = View.GONE

            GlideApp.with(this)
                    .load(uri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(300)
                    .priority(Priority.IMMEDIATE)
                    .into(view!!.style)
        })

        viewModel.currentNeuralStyle.subscribe(this, {

            stylezedImageDisposable.unsubscribe()
            stylezedImageDisposable = Single.create<Bitmap> { emitter ->

                val bitmap = NeuralImages.stylizeTensorFlow(activity!!,
                        ImageUtils.getBitmapFromUri(activity!!, viewModel.currentNeuralImage.value!!)!!)
                emitter.onSuccess(bitmap)

            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { view!!.progressBar.visibility = View.VISIBLE }
                    .doOnEvent {  _,_ -> view!!.progressBar.visibility = View.GONE }
                    .subscribe({ bitmap ->

                        GlideApp.with(this)
                                .load(bitmap)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .priority(Priority.IMMEDIATE)
                                .into(view!!.preview)

                    }, Throwable::printStackTrace)

        })
    }

    override fun onResume() {
        super.onResume()
        view!!.style.setOnClickListener {
            NeuralNetworkStyleChooser.newInstance().show(activity!!.supportFragmentManager,
                            NeuralNetworkStyleChooser.TAG)
        }
        view!!.cover.setOnClickListener {
            NeuralNetworkImageChooser.newInstance().show(activity!!.supportFragmentManager,
                            NeuralNetworkImageChooser.TAG)
        }
        view!!.stylize.setOnClickListener {
            val intent = Intent(activity, NeuralNetworkService::class.java)
            intent.action = NeuralNetworkService.ACTION_START
            intent.putExtra(NeuralNetworkService.EXTRA_STYLE, NeuralImages.getCurrentStyle())
            ContextCompat.startForegroundService(activity!!, intent)
            activity!!.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        view!!.style.setOnClickListener(null)
        view!!.cover.setOnClickListener(null)
        view!!.stylize.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        stylezedImageDisposable.unsubscribe()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_neural_network_result_chooser
}