# Phase 1 plan: Open Qualifying (deadline 23 November 2026; today is 29 October 2026, so about 25 days)
Phase 1 asks for: report, source code and demo. Aim to submit by 21 November (2 days early). Dates are suggestions.
| Dates | Goal | Done when |
|---|---|---|
| 29 Oct – 2 Nov | Vision model | Tomato classes trained on PlantVillage (+ any real photos you have), honest held-out test accuracy, `tomato_mobilenet.pte` exported with ExecuTorch/XNNPACK |
| 3 – 9 Nov | Llama on the phone | Llama 3.2 1B-Instruct exported to `.pte`, runs through ExecuTorch on your Arm phone, offline; time-to-first-token and tokens/sec logged |
| 10 – 14 Nov | Integrate + benchmark | APK built, model wired into the app, airplane-mode run works, in-app benchmark numbers recorded (device, SoC, latency, memory, battery) |
| 15 – 19 Nov | Report + demo | Technical report filled (every TO MEASURE cell), final demo video recorded on the phone (docs/DEMO_VIDEO_SCRIPT.md) |
| 20 – 21 Nov | Submit | Source, APK/app, video, report uploaded; keep 22–23 Nov as buffer only |
Minimum a judge should see in Phase 1: (1) a real image model running on-device, (2) Llama via ExecuTorch running on-device, (3) measured numbers, (4) an offline demo. If time runs short, cut scope (tomato only, 3-4 classes, Tamil + English) rather than skipping these.
