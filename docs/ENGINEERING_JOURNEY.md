# Applied AI on SoC: the engineering journey (what the 2nd edition rewards)
| Stage | What we do | Status | Evidence to show |
|---|---|---|---|
| AI algorithm development | MobileNetV3-small classifier, visible-damage estimate, uncertainty class | Code written; model not trained | Train log, held-out test accuracy |
| Optimisation | Quantisation (vision and Llama), small input size, lazy-load Llama, short generation | Planned | Size and speed before vs after |
| System integration | Tiered pipeline: quality gate, vision, knowledge base, Llama, voice, local storage | Prototype works; Kotlin ExecuTorch code untested | Architecture diagram, working app |
| Edge deployment | ExecuTorch .pte on Arm CPU (XNNPACK), Android APK, no INTERNET permission | Not yet run on a phone | Airplane-mode video |
| Benchmarking | In-app benchmark: device, SoC, load time, median/p95 latency, memory, battery, Llama TTFT and tokens/sec | Button written; no numbers yet | Filled benchmark table |
| Validation | Split by plant/field, real farm photos, calibration, expert review of advice | Plan only | Confusion matrix, calibration plot |
| Product engineering | Farmer login/privacy, kiosk mode, Crop Passport, safety rules, knowledge-base validator | Mostly done in prototype | Demo + checklist |
Judges will weigh measured SoC behaviour (speed, memory, power) more than extra features.
