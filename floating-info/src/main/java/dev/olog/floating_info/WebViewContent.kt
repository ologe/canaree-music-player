package dev.olog.floating_info

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import dev.olog.floating_info.api.Content
import kotlin.properties.Delegates

abstract class WebViewContent(
        context: Context,
        @LayoutRes layoutRes: Int

) : Content {

    var item by Delegates.observable("", { _, _, new ->
        webView.loadUrl(getUrl(new))
    })

    val content : View = LayoutInflater.from(context).inflate(layoutRes, null)

    private val webView = content.findViewById<WebView>(R.id.webView)
    private val progressBar = content.findViewById<ProgressBar>(R.id.progressBar)
    private val back = content.findViewById<View>(R.id.navigateBack)
    private val next = content.findViewById<View>(R.id.navigateNext)

    init {
        webView.settings.javaScriptEnabled = true // enable yt content
        webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
            }

        }
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
    }

    override fun onHidden() {
        back.setOnClickListener(null)
        next.setOnClickListener(null)
    }

    protected abstract fun getUrl(item: String): String

}