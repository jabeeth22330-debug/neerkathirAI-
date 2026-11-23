# PS1 requirement check (be honest in the report)
Phase 1 (23 Nov 2026) needs: report, source code, demo. Phase 2 (5 Feb 2027) needs: source code, application, demonstration video, technical report. See PHASE1_PLAN.md and POC_PLAN.md.
| Requirement | Status | Next step |
|---|---|---|
| Detect diseases | Partial: rule-based tomato screening works; vision pipeline written | Train + validate model |
| Detect pests | Partial: 3 pest entries in knowledge base | IP102 subset model + UI branch |
| Classify severity | Partial: visible-damage % (low/moderate/high) | Validate thresholds with expert-labelled images |
| Treatment/intervention (pesticides, fertilisers) | Partial: tomato only, sourced product names, no doses shown | CIB&RC check + expert sign-off; build packs for other crops |
| Region-specific advisory | Planned (`region_notes` = TODO_EXPERT) | Agri officer / university input |
| Multiple local languages | Partial: Tamil + English | Add one more (e.g. Hindi/Telugu) via string packs |
| Fertiliser/irrigation advice | Partial: KB entries | Show in UI |
| Offline | Designed (no INTERNET permission) | Prove with airplane-mode video |
| Llama via ExecuTorch | NOT DONE: hook written, not compiled | Export, run, benchmark |
| Hailo | Not used | Optional; document as a separate pipeline if added |
## Deliverables
- [ ] Source code (this repo, with licences and dataset attributions)
- [ ] Application (APK built from `android/`)
- [ ] Demonstration video (script in docs/)
- [ ] Technical report (draft provided; fill every "TO MEASURE" cell)
