# NEERKATHIR AI – Android + Village Kiosk

## A. Farmer app (Android phone)
1. Open this folder in Android Studio -> Build APK. Install on an arm64 phone.
2. UI = app/src/main/assets/index.html (same as the web prototype). The app declares NO INTERNET permission.
3. Install Tamil TTS voice data once (Settings > Text-to-speech) while online.
4. Test: Airplane mode ON, run the full scan.

## B. Farmers WITHOUT phones: "Crop Doctor" kiosk (same APK)
Hardware: Arm Android tablet/phone on a stand + ring light + speaker + optional Bluetooth thermal printer.
1. Install the APK. Launch pinned: adb shell am start -n com.neerkathir.ai/.MainActivity --ez kiosk true
   (permanent kiosk: provision as device owner with dpm set-device-owner).
2. Operator (village volunteer / co-op staff) photographs the farmer's leaf; Settings -> Kiosk mode ON.
3. Farmer hears the Tamil result and receives the printed Crop Passport. The farmer keeps the ID (NK-xxxxxx); the operator finds it in Field memory for follow-up.
4. Later: ESC/POS Bluetooth printing and a big-button keypad.

## C. Real AI (next)
- training/train_export.py -> tomato_mobilenet.pte (XNNPACK, Arm). Wire into analyse() via a Bridge method.
- Llama 3.2 1B/3B -> .pte via ExecuTorch -> LlamaAdvisor.kt (check the LlamaDemo app for your version's API).
- Benchmark on your device (size, latency, RAM, battery) and put the numbers in your submission.
- Have an agricultural expert review every advisory before real use.

## D. What changed in v2
- NativeClassifier.kt: runs tomato_mobilenet.pte via ExecuTorch on the Arm CPU; the UI uses it automatically when the model files are installed.
- About tab -> "Run benchmark": records device, SoC, ABI, model load time, median/p95 latency, memory, battery. Use these numbers in your slides.
- Copy to /sdcard/Android/data/com.neerkathir.ai/files/ : tomato_mobilenet.pte, classes.txt (and llama3_2.pte + tokenizer.model).
- NOT compiled here: build in Android Studio and fix any ExecuTorch API name differences for your version.
- v3: farmer registration/login (PBKDF2 150k + AES-GCM, lockout, auto-logout). Android app: add android:allowBackup="false" is already advisable; see manifest.
