@file:Suppress("DEPRECATION")

package dev.olog.msc.presentation.edit

import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Priority
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.act
import dev.olog.msc.utils.k.extension.ctx

abstract class BaseEditItemFragment : BaseFragment() {

    private var progressDialog: ProgressDialog? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        hideLoader()
    }

    protected fun setImage(model: DisplayableItem){
        val background = view!!.findViewById<ImageView>(R.id.backgroundCover)
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(ctx).clear(background)
        GlideApp.with(ctx).clear(image)

        val builder = GlideApp.with(ctx)
                .load(model)
                .placeholder(CoverUtils.getGradient(ctx, model.mediaId))
                .override(500)
                .priority(Priority.IMMEDIATE)

        builder.into(image)
        builder.into(background)
    }

    protected fun showLoader(@StringRes resId: Int){
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String){
        progressDialog = ProgressDialog.show(context, "", message, true)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)
        progressDialog?.setOnCancelListener {
            onLoaderCancelled()
            progressDialog?.setOnCancelListener(null)
        }
    }

    protected fun hideLoader(){
        progressDialog?.dismiss()
        progressDialog = null
    }

    abstract fun onLoaderCancelled()

}