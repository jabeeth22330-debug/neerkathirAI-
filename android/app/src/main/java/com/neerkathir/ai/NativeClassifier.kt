package com.neerkathir.ai
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Debug
import android.os.BatteryManager
import android.content.Context
import android.util.Base64
import org.json.JSONObject
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import org.pytorch.executorch.Tensor
import java.io.File
import kotlin.math.exp

/**
 * Runs tomato_mobilenet.pte (exported by training/train_export.py) with ExecuTorch on the Arm CPU (XNNPACK).
 * Put tomato_mobilenet.pte and classes.txt in /sdcard/Android/data/com.neerkathir.ai/files/
 * NOTE: ExecuTorch class names can differ between versions; check the version's Android docs if the build fails.
 */
class NativeClassifier(private val ctx: Context) {
  private val dir: File get() = ctx.getExternalFilesDir(null)!!
  private var module: Module? = null
  private val mean = floatArrayOf(.485f, .456f, .406f); private val std = floatArrayOf(.229f, .224f, .225f)

  fun available() = File(dir, "tomato_mobilenet.pte").exists() && File(dir, "classes.txt").exists()
  private fun mod(): Module { if (module == null) module = Module.load(File(dir, "tomato_mobilenet.pte").path); return module!! }
  private fun classes() = File(dir, "classes.txt").readLines().filter { it.isNotBlank() }

  private fun toTensor(bm: Bitmap): Tensor {
    val b = Bitmap.createScaledBitmap(bm, 224, 224, true); val px = IntArray(224 * 224); b.getPixels(px, 0, 224, 0, 0, 224, 224)
    val f = FloatArray(3 * 224 * 224)
    for (i in px.indices) { val c = px[i]
      f[i] = (((c shr 16) and 255) / 255f - mean[0]) / std[0]
      f[224 * 224 + i] = (((c shr 8) and 255) / 255f - mean[1]) / std[1]
      f[2 * 224 * 224 + i] = ((c and 255) / 255f - mean[2]) / std[2] }
    return Tensor.fromBlob(f, longArrayOf(1, 3, 224, 224))
  }
  private fun run(t: Tensor): FloatArray = mod().forward(EValue.from(t))[0].toTensor().dataAsFloatArray

  fun classify(b64: String): String = try {
    val bytes = Base64.decode(b64, Base64.DEFAULT); val bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val t0 = System.nanoTime(); val out = run(toTensor(bm)); val ms = (System.nanoTime() - t0) / 1_000_000
    val mx = out.max(); val e = out.map { exp((it - mx).toDouble()) }; val sum = e.sum()
    val i = e.indices.maxByOrNull { e[it] }!!
    JSONObject().put("ok", true).put("label", classes()[i]).put("conf", e[i] / sum).put("ms", ms).toString()
  } catch (ex: Throwable) { JSONObject().put("ok", false).put("error", ex.toString()).toString() }

  fun deviceInfo(): JSONObject = JSONObject().put("device", "${Build.MANUFACTURER} ${Build.MODEL}")
    .put("soc", if (Build.VERSION.SDK_INT >= 31) "${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}" else "n/a (Android < 12)")
    .put("abi", Build.SUPPORTED_ABIS.joinToString()).put("cpu_cores", Runtime.getRuntime().availableProcessors())
    .put("android", Build.VERSION.RELEASE)

  fun benchmark(n: Int): String {
    val o = deviceInfo(); val bm = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
    val bat = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    o.put("battery_before_pct", bat.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
    if (!available()) return o.put("vision_model", "NOT INSTALLED (copy .pte + classes.txt)").toString()
    val t0 = System.nanoTime(); mod(); o.put("model_load_ms", (System.nanoTime() - t0) / 1_000_000)
    val ts = (1..n).map { val s = System.nanoTime(); run(toTensor(bm)); (System.nanoTime() - s) / 1_000_000 }.sorted()
    o.put("vision_median_ms", ts[ts.size / 2]).put("vision_p95_ms", ts[(ts.size * 0.95).toInt().coerceAtMost(ts.size - 1)])
    o.put("native_heap_mb", Debug.getNativeHeapAllocatedSize() / 1048576).put("model_file_mb", File(dir, "tomato_mobilenet.pte").length() / 1048576.0)
    o.put("battery_after_pct", bat.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
    return o.toString()
  }
}
