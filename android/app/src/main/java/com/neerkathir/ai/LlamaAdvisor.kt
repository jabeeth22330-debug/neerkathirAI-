package com.neerkathir.ai
import android.content.Context
import java.io.File

/**
 * On-device Llama advisor (Llama 3.2 1B/3B-Instruct) running through ExecuTorch.
 * The model only REWRITES approved advice from knowledge/knowledge_base.json. It never decides the diagnosis.
 *
 * Setup (see docs/POC_PLAN.md):
 *  1. Export the model to llama3_2.pte with the ExecuTorch Llama export guide for the ExecuTorch version you use.
 *  2. Copy llama3_2.pte and tokenizer.model to /sdcard/Android/data/com.neerkathir.ai/files/
 *  3. Uncomment the ExecuTorch lines below. Class/method names differ between versions: compare with that
 *     version's LlamaDemo sample app. NOT compiled or tested in this repo yet.
 */
object LlamaAdvisor {
  // private var llm: org.pytorch.executorch.extension.llm.LlmModule? = null
  private fun dir(ctx: Context) = ctx.getExternalFilesDir(null)
  fun installed(ctx: Context) = File(dir(ctx), "llama3_2.pte").exists() && File(dir(ctx), "tokenizer.model").exists()

  const val SYSTEM = "You are a careful agricultural assistant for smallholder farmers. " +
    "Use ONLY the approved advice given below. Explain it in simple words in the requested language, in at most 4 short sentences. " +
    "Never add pesticide names, doses or numbers that are not in the approved advice. " +
    "Always end by telling the farmer to confirm with the local agricultural officer."

  fun generate(ctx: Context, userPrompt: String): String {
    if (!installed(ctx)) return "Llama model not installed."
    val prompt = "<|begin_of_text|><|start_header_id|>system<|end_header_id|>\n\n$SYSTEM<|eot_id|>" +
      "<|start_header_id|>user<|end_header_id|>\n\n$userPrompt<|eot_id|><|start_header_id|>assistant<|end_header_id|>\n\n"
    // val sb = StringBuilder()
    // if (llm == null) llm = org.pytorch.executorch.extension.llm.LlmModule(
    //     File(dir(ctx), "llama3_2.pte").path, File(dir(ctx), "tokenizer.model").path, 0.2f)
    // llm!!.generate(prompt, 200, object : org.pytorch.executorch.extension.llm.LlmCallback {
    //     override fun onResult(r: String) { sb.append(r) }
    //     override fun onStats(s: String) { /* log time-to-first-token and tokens/sec for the report */ }
    // })
    // return sb.toString()
    return "Llama is not wired yet (see LlamaAdvisor.kt)."
  }
}
