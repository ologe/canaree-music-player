package dev.olog.service.floating

import android.app.Service
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.olog.service.floating.api.Content
import dev.olog.shared.android.extensions.lifecycle
import kotlin.properties.Delegates

abstract class WebViewContent(
    service: Service,
    @LayoutRes layoutRes: Int
) : Content(), DefaultLifecycleObserver {

    var item by Delegates.observable("", { _, _, new ->
        webView.clearHistory()
        webView.stopLoading()
        webView.loadUrl(getUrl(new))
    })

    val content: View = LayoutInflater.from(service).inflate(layoutRes, null)

    private val webView = content.findViewById<WebView>(R.id.webView)
    private val progressBar = content.findViewById<ProgressBar>(R.id.progressBar)
    private val back = content.findViewById<View>(R.id.navigateBack)
    private val next = content.findViewById<View>(R.id.navigateNext)
    private val refresh = content.findViewById<View>(R.id.refresh)

    init {
        service.lifecycle.addObserver(this)
        webView.settings.javaScriptEnabled = true // enable yt content
        try {
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar.progress = newProgress
                    progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
                }
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            // chrome may not be installed
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        webView.destroy()
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()
        back.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
        next.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
            }
        }
        refresh.setOnClickListener { webView.reload() }
    }

    override fun onHidden() {
        super.onHidden()
        back.setOnClickListener(null)
        next.setOnClickListener(null)
        refresh.setOnClickListener(null)
    }

    protected abstract fun getUrl(item: String): String

}