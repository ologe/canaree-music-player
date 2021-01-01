package dev.olog.feature.edit

import android.app.ProgressDialog
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.bumptech.glide.Priority
import dev.olog.core.mediaid.MediaId
import dev.olog.feature.base.base.BaseBottomSheetFragment
import dev.olog.lib.image.provider.CoverUtils
import dev.olog.lib.image.provider.GlideApp

internal abstract class BaseEditItemFragment : BaseBottomSheetFragment() {

    private var progressDialog: ProgressDialog? = null

    protected fun loadImage(mediaId: MediaId) {
        val image = requireView().findViewById<ImageView>(R.id.cover)

        GlideApp.with(requireContext()).clear(image)

        GlideApp.with(requireContext())
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