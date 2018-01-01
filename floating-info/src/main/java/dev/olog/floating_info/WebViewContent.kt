package dev.olog.floating_info

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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

    val content = LayoutInflater.from(context).inflate(layoutRes, null)

    private val webView = content.findViewById<WebView>(R.id.webView)
    private val progressBar = content.findViewById<ProgressBar>(R.id.progressBar)
    private val back = content.findViewById<View>(R.id.back)

    init {
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = View.VISIBLE
            }

        }
        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }

        }
        back.setOnClickListener {
            webView.goBack()
        }
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
    }

    override fun onHidden() {
    }

    protected abstract fun getUrl(item: String): String

}