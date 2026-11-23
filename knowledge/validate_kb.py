"""Run: python validate_kb.py   Checks knowledge_base.json before use in the app.
Rules: required fields in every language; NO dose-like text in farmer-facing text (actions, notes);
every product has kind, name, source url + date, status; doses are stored only in dose_in_source and never shown (show_dose must be false)."""
import json, re, sys
kb = json.load(open("knowledge_base.json", encoding="utf-8")); LANGS = kb["languages"]
DOSE = re.compile(r"\b\d+(\.\d+)?\s?(ml|g|gm|kg|l|lit|litre|liter|ppm|%|mg)\b", re.I)
bad = todo = chem = 0
def err(i, m):
    global bad; print(f"[{i}] {m}"); bad += 1
for e in kb["entries"]:
    i = e["id"]
    for f in ("name", "symptoms", "refer_when"):
        for l in LANGS:
            if not e[f].get(l): err(i, f"missing {f}.{l}")
    for l in LANGS:
        if not e["actions"].get(l): err(i, f"no actions in {l}")
        for a in e["actions"].get(l, []) + [e["refer_when"].get(l, "")]:
            if DOSE.search(a): err(i, f"dose-like text in {l}: {a}")
    for p in e.get("products", []):
        if p["kind"] not in ("biological", "chemical", "fertiliser"): err(i, f"bad product kind {p['kind']}")
        s = p.get("source", {})
        if not (s.get("url") and s.get("updated")): err(i, f"product {p['name']} lacks source url/date")
        if p.get("show_dose"): err(i, f"product {p['name']} would show a dose")
        for l in LANGS:
            if DOSE.search(p["note"].get(l, "")): err(i, f"dose-like text in note of {p['name']}")
        if p["kind"] == "chemical":
            chem += 1
            if "registration_check" in p["status"]: todo += 1
    if e["region_notes"] == "TODO_EXPERT": todo += 1
    if not e["sources"]: todo += 1
print(f"{len(kb['entries'])} entries, {sum(len(e.get('products', [])) for e in kb['entries'])} products ({chem} chemical), {bad} errors, {todo} items still need expert / registration check")
sys.exit(1 if bad else 0)
