package com.gigigo.webview

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*


class WebViewActivity : AppCompatActivity() {

    companion object {
        @JvmStatic lateinit var instance: WebViewActivity
        @JvmStatic val BUNDLE_URL = "BUNGLE_URL"
        @JvmStatic val BUNGLE_PROGRESS_DRAWABLE = "BUNGLE_PROGRESS_DRAWABLE"
    }

    init {
        instance = this
    }

    public

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webview_client.setInitialScale(1)
        webview_client.settings.javaScriptEnabled = true
        webview_client.settings.loadWithOverviewMode = true
        webview_client.settings.useWideViewPort = true
        webview_client.webChromeClient = getDefaultChromeWebClient()
        webview_client.webViewClient = getDefaultWebClient()
        webview_client.loadUrl(
                if (intent?.extras != null)
                    intent?.extras?.getString(BUNDLE_URL, "https://www.google.com.mx")
                else "https://www.google.com.mx")

        progress_bar.progressDrawable = if (intent?.extras != null && intent.extras.containsKey(BUNGLE_PROGRESS_DRAWABLE))
            ContextCompat.getDrawable(this, intent.extras.getInt(BUNGLE_PROGRESS_DRAWABLE))
        else ContextCompat.getDrawable(this, R.drawable.custom_progress)
    }

    override fun onBackPressed() {
        if (webview_client.canGoBack())
            webview_client.goBack()
        else
            super.onBackPressed()
    }

    fun getDefaultChromeWebClient() : WebChromeClient{
        return object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progress_bar.progress = newProgress
            }
        }
    }

    fun getDefaultWebClient() : WebViewClient{
        return object : WebViewClient(){

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return false
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if(progress_bar.visibility == View.GONE)
                    progress_bar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if(progress_bar.visibility == View.VISIBLE)
                    progress_bar.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_share -> {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type="text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, webview_client.url)
                startActivity(Intent.createChooser(shareIntent,getString(R.string.action_share)))
                true
            }
            R.id.action_refresh -> {
                webview_client.reload()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
