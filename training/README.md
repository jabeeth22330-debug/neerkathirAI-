# Training and export
Data layout: `data/{train,val,test}/<class>/*.jpg`
Classes (tomato, start small): healthy, early_blight, late_blight, septoria, leaf_curl, other_unknown
- Public data: PlantVillage (tomato). Check the licence. Pests: IP102 subset (whitefly, fruit borer, mites) as a second model.
- Add REAL farm photos with expert-verified labels. PlantVillage images are lab-style; accuracy on them overstates field accuracy.
- Split by plant/field/session, never by photo.
- `other_unknown` must contain non-leaf photos, other crops and unseen diseases, so the model learns to refuse.
Run: `python train_export.py` -> prints TEST accuracy, writes `tomato_mobilenet.pte` and `classes.txt`.
Then: temperature-scale on the validation set and choose the confidence threshold (replace the 0.70 placeholder in MainActivity.kt).
Llama: export Llama 3.2 1B/3B-Instruct to `.pte` using the ExecuTorch Llama guide for your ExecuTorch version, quantised for the phone. Record time-to-first-token and tokens/sec.
