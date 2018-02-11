package dev.olog.msc.presentation.neural.network

import android.app.AlertDialog
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import dev.olog.msc.R
import dev.olog.msc.presentation.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.neural.network.image.chooser.NeuralNetworkImageChooser
import dev.olog.msc.presentation.neural.network.service.NeuralNetworkService
import dev.olog.msc.presentation.neural.network.style.chooser.NeuralNetworkStyleChooser
import dev.olog.msc.presentation.utils.images.NeuralImages
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_neural_network_result_chooser.view.*
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import javax.inject.Inject

private const val HIGHLIGHT_STYLIZE_ALL = "HIGHLIGHT_STYLIZE_ALL"

class NeuralNetworkFragment : BaseFragment() {

    @Inject lateinit var viewModel: NeuralNetworkActivityViewModel
    private var stylezedImageDisposable: Disposable? = null
    private var toastRef : WeakReference<Toast>? = null

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

        viewModel.observeImageLoadedSuccesfully
                .asLiveData()
                .subscribe(this, { pair ->
                    val (image, style) = pair

            stylezedImageDisposable.unsubscribe()
            stylezedImageDisposable = Single.create<Bitmap> { emitter ->

                val size = 768

                val bitmap = NeuralImages.stylizeTensorFlow(activity!!,
                        ImageUtils.getBitmapFromUriOrNull(activity!!, Uri.parse(image), size, size)!!, size = size)
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
                                .listener(object : RequestListener<Drawable>{
                                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                        return false
                                    }

                                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                        highlightStylizeAll()
                                        return false
                                    }
                                })
                                .into(view!!.preview)

                    }, Throwable::printStackTrace)

        })
    }

    private fun highlightStylizeAll(){
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
            return
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity!!)
        val hightlight = preferences.getBoolean(HIGHLIGHT_STYLIZE_ALL, true)
        if (!hightlight){
            return
        }

        val text = getString(R.string.neural_stylize_all_description)
        val tapTarget = TapTarget.forView(view!!.stylize, text)

        TapTargetView.showFor(activity, tapTarget, object : TapTargetView.Listener(){
            override fun onTargetLongClick(view: TapTargetView?) {
                super.onTargetLongClick(view)
                updateHighlightPrefs()
            }

            override fun onOuterCircleClick(view: TapTargetView?) {
                super.onOuterCircleClick(view)
                updateHighlightPrefs()
            }

            override fun onTargetCancel(view: TapTargetView?) {
                super.onTargetCancel(view)
                updateHighlightPrefs()
            }

            override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                super.onTargetDismissed(view, userInitiated)
                updateHighlightPrefs()
            }

            override fun onTargetClick(view: TapTargetView?) {
                super.onTargetClick(view)
                updateHighlightPrefs()
            }
        })
    }

    private fun updateHighlightPrefs(){
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
            return
        }
        PreferenceManager.getDefaultSharedPreferences(activity!!)
                .edit()
                .putBoolean(HIGHLIGHT_STYLIZE_ALL, false)
                .apply()
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
            if (viewModel.currentNeuralStyle.value != null){
                createNeuralStartServiceRequestDialog()
            } else {
                toastRef?.get()?.cancel() // delete previous
                toastRef = WeakReference(activity!!.toast(R.string.neural_stylize_all_missing_style))
            }
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

    private fun createNeuralStartServiceRequestDialog(){
        AlertDialog.Builder(activity)
                .setTitle(R.string.neural_stylize_all)
                .setMessage(R.string.neural_stylize_all_message)
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    val intent = Intent(activity, NeuralNetworkService::class.java)
                    intent.action = NeuralNetworkService.ACTION_START
                    intent.putExtra(NeuralNetworkService.EXTRA_STYLE, NeuralImages.getCurrentStyle())
                    ContextCompat.startForegroundService(activity!!, intent)
                    activity!!.onBackPressed()
                })
                .setNegativeButton(R.string.popup_negative_no, null)
                .makeDialog()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_neural_network_result_chooser
}