package com.neerkathir.ai
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/** Offline WebView shell. No INTERNET permission is declared, so the app cannot go online. */
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
  private lateinit var web: WebView
  private lateinit var tts: TextToSpeech
  private var filePathCb: ValueCallback<Array<Uri>>? = null

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(b: Bundle?) {
    super.onCreate(b)
    requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
    tts = TextToSpeech(this, this)
    web = WebView(this).also { setContentView(it) }
    web.settings.apply { javaScriptEnabled = true; domStorageEnabled = true
      allowFileAccess = true; allowContentAccess = true; blockNetworkLoads = true }
    web.addJavascriptInterface(Bridge(), "Android")
    web.webChromeClient = object : WebChromeClient() {
      override fun onShowFileChooser(w: WebView, cb: ValueCallback<Array<Uri>>, p: FileChooserParams): Boolean {
        filePathCb = cb
        val pick = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
        val chooser = Intent.createChooser(Intent(MediaStore.ACTION_IMAGE_CAPTURE), "Photo")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pick))
        startActivityForResult(chooser, 7)
        return true
      }
    }
    web.loadUrl("file:///android_asset/index.html")
    // Kiosk mode for the shared village machine (pins the app). Full lock needs device-owner provisioning.
    if (intent.getBooleanExtra("kiosk", false)) startLockTask()
  }
  @Deprecated("Deprecated in Java") override fun onActivityResult(rc: Int, res: Int, d: Intent?) {
    if (rc == 7) { filePathCb?.onReceiveValue(
      if (res == Activity.RESULT_OK && d?.data != null) arrayOf(d.data!!) else null); filePathCb = null }
    else super.onActivityResult(rc, res, d)
  }
  override fun onInit(s: Int) { tts.language = Locale("ta", "IN") }  // Tamil voice data must be installed on device
  inner class Bridge {
    @JavascriptInterface fun speak(text: String, lang: String) {
      tts.language = if (lang == "ta") Locale("ta", "IN") else Locale("en", "IN")
      tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nk")
    }
    private val clf by lazy { NativeClassifier(this@MainActivity) }
    @JavascriptInterface fun hasModel(): Boolean = clf.available()
    @JavascriptInterface fun threshold(): Double = 0.70   // PLACEHOLDER: set from validation/calibration
    @JavascriptInterface fun classify(b64: String): String = clf.classify(b64)
    @JavascriptInterface fun benchmark(n: Int): String = clf.benchmark(n)
    /** Hook for the Llama/ExecuTorch advisory (see LlamaAdvisor.kt). */
    @JavascriptInterface fun hasLlama(): Boolean = LlamaAdvisor.installed(this@MainActivity)
    /** Async: runs Llama on a background thread so the UI never freezes, then calls window.onLlama(text). */
    @JavascriptInterface fun askLlama(prompt: String) {
      Thread {
        val r = LlamaAdvisor.generate(this@MainActivity, prompt)
        web.post { web.evaluateJavascript("onLlama(" + org.json.JSONObject.quote(r) + ")", null) }
      }.start()
    }
  }
}
