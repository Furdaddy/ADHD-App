package app.nextstep

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader

class MainActivity : ComponentActivity() {

    private lateinit var web: WebView
    private var askedForNotifications = false
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val assets = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        val background = ContextCompat.getColor(this, R.color.bg)
        web = WebView(this).apply {
            setBackgroundColor(background)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                    assets.shouldInterceptRequest(request.url)

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    if (request.url.host == APP_HOST) return false
                    runCatching { startActivity(Intent(Intent.ACTION_VIEW, request.url)) }
                    return true
                }
            }
            addJavascriptInterface(Bridge(), "NextStepAndroid")
        }

        val root = FrameLayout(this).apply {
            setBackgroundColor(background)
            addView(web, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        }
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        setContentView(root)

        // The page keeps its own state (including a running timer), so a fresh load is always safe.
        web.loadUrl("https://$APP_HOST/assets/index.html#${hashFrom(intent) ?: "today"}")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val hash = hashFrom(intent) ?: return
        web.evaluateJavascript("location.hash = '$hash';", null)
    }

    override fun onResume() {
        super.onResume()
        // Pick up changes made from the widget while the app was in the background.
        web.evaluateJavascript("window.nextStepResume && window.nextStepResume();", null)
    }

    private fun hashFrom(intent: Intent?): String? =
        intent?.getStringExtra(EXTRA_HASH)?.takeIf { it.matches(Regex("[a-z-]{1,20}")) }

    private fun askForNotifications() {
        if (askedForNotifications || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        askedForNotifications = true
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /** Called from the web UI (on a background thread). */
    inner class Bridge {
        @JavascriptInterface
        fun getState(): String = StateStore.read(this@MainActivity).orEmpty()

        @JavascriptInterface
        fun saveState(json: String) {
            StateStore.write(this@MainActivity, json)
            NextStepWidget.refresh(this@MainActivity)
        }

        @JavascriptInterface
        fun startTimer(endAt: Double, mins: Int, label: String) {
            FocusTimer.start(this@MainActivity, endAt.toLong(), mins, label)
            runOnUiThread {
                askForNotifications()
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        @JavascriptInterface
        fun cancelTimer() {
            FocusTimer.cancel(this@MainActivity)
            runOnUiThread { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
        }

        @JavascriptInterface
        fun timerEnded() {
            runOnUiThread { window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
        }
    }

    companion object {
        const val EXTRA_HASH = "hash"
        private const val APP_HOST = "appassets.androidplatform.net"

        fun openIntent(context: Context, hash: String, requestCode: Int): PendingIntent =
            PendingIntent.getActivity(
                context, requestCode,
                Intent(context, MainActivity::class.java)
                    .putExtra(EXTRA_HASH, hash)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
    }
}
