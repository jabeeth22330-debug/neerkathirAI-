# Test phone: Vivo Y22 (4 GB RAM, 128 GB storage)
Specs from retailer listings (confirm with the in-app benchmark): MediaTek Helio G70, 12 nm, 2 x Cortex-A75 + 6 x Cortex-A55, Mali-G52 MC2, Android 12.
- Build: arm64-v8a, minSdk 26 is fine. Android 12 lets the benchmark read the SoC model.
- No NPU in the listings I found: plan CPU-only (ExecuTorch + XNNPACK) for vision and Llama.
- Memory (4 GB, but Android and Funtouch use much of it): keep the APK small, load ONE model at a time, release the vision model before loading Llama, keep Llama context short (about 512 tokens) and answers to 4 sentences or fewer.
- Llama: start with Llama 3.2 1B-Instruct, quantised. Do not try 3B on this phone. Check the model file size before copying it (storage is fine, RAM is the limit).
- Expect slow generation. Measure time to first token and tokens per second over 5+ runs, phone on charger and cool. If too slow, shorten the prompt and answer, and fall back to the stored Tamil advice.
- Before benchmarking: close other apps, Battery saver OFF, airplane mode ON, screen on, same room temperature. Record: model sizes, load time, median/p95 vision latency, Llama TTFT and tokens/s, peak memory, battery drop per 50 scans.
- Tamil voice: Settings > General management > Text-to-speech: install Tamil voice data once while online.
- Screen recording for the demo: use the built-in recorder; show airplane mode first.
