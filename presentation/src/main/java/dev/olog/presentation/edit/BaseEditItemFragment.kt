@file:Suppress("DEPRECATION")

package dev.olog.presentation.edit

import android.app.ProgressDialog
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.bumptech.glide.Priority
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation.base.bottomsheet.BaseBottomSheetFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseEditItemFragment : BaseBottomSheetFragment(),
    CoroutineScope by MainScope() {

    companion object {
        private val TAG = "P:${BaseEditItemFragment::class.java.simpleName}"
    }

    private var progressDialog: ProgressDialog? = null

    protected fun loadImage(mediaId: MediaId) {
        val image = view!!.findViewById<ImageView>(R.id.cover)

        GlideApp.with(this).clear(image)

        GlideApp.with(this)
            .load(mediaId)
            .placeholder(CoverUtils.getGradient(requireContext(), mediaId))
            .override(500)
            .priority(Priority.IMMEDIATE)
            .into(image)
    }

    protected fun showLoader(@StringRes resId: Int) {
        showLoader(getString(resId))
    }

    protected fun showLoader(message: String, dismissable: Boolean = true) {
        progressDialog = ProgressDialog.show(context, "", message, true).apply {
            setCancelable(dismissable)
            setCanceledOnTouchOutside(dismissable)
            setOnCancelListener {
                onLoaderCancelled()
                setOnCancelListener(null)
            }
        }
    }

    protected fun hideLoader() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        hideLoader()
    }

    abstract fun onLoaderCancelled()

}