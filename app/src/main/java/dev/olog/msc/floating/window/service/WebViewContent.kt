package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import dev.olog.msc.R
import dev.olog.msc.floating.window.service.api.Content
import dev.olog.msc.utils.k.extension.setGone
import dev.olog.msc.utils.k.extension.setVisible
import kotlin.properties.Delegates

abstract class WebViewContent(
        lifecycle: Lifecycle,
        context: Context,
        @LayoutRes layoutRes: Int

) : Content, DefaultLifecycleObserver {

    var item by Delegates.observable("", { _, _, new ->
        webView.clearHistory()
        webView.stopLoading()
        webView.loadUrl(getUrl(new))
    })

    val content : View = LayoutInflater.from(context).inflate(layoutRes, null)

    private val webView = content.findViewById<WebView>(R.id.webView)
    private val progressBar = content.findViewById<ProgressBar>(R.id.progressBar)
    private val back = content.findViewById<View>(R.id.navigateBack)
    private val next = content.findViewById<View>(R.id.navigateNext)
    private val refresh = content.findViewById<View>(R.id.refresh)
    private val error = content.findViewById<View>(R.id.error)

    init {
        lifecycle.addObserver(this)
        webView.settings.javaScriptEnabled = true // enable yt content
        webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                this@WebViewContent.error.setVisible()
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                this@WebViewContent.error.setGone()
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        webView.destroy()
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        back.setOnClickListener {
            if (webView.canGoBack()) { webView.goBack() }
        }
        next.setOnClickListener {
            if (webView.canGoForward()) { webView.goForward() }
        }
        refresh.setOnClickListener { webView.reload() }
    }

    override fun onHidden() {
        back.setOnClickListener(null)
        next.setOnClickListener(null)
        refresh.setOnClickListener(null)
    }

    protected abstract fun getUrl(item: String): String

}