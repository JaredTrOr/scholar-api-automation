# Scholar-API automation

- Name: Jared Alexader Trujillo Ortiz
- NaoID: 3347
- Date: 21 September 2025

---

## 🎯 Project purpose
Automate the integration of profile and publication data for the Innovation Center's into the institution's research database using Google Scholar (via SerpApi). The automation replaces manual collection and entry, improves data freshness and reproducibility, and centralizes documentation and version control on GitHub.

---

## 🔑 Key functionalities (high level)
- Query Google Scholar (via SerpApi) for a researcher’s publications, citations, and profile metadata.
- Map scholar JSON results to the institution DB schema.
- Extract data and stage it in-memory (Java), then export to integration-ready payloads.
- A Java integration program that writes the final records into the research database (via existing DB API).

---

## ❗ Why this matters (project relevance)
- Automates complex processes of browsing
- Eliminates repeated manual data entry and human error.
- Makes it easy to audit and trace changes (Git history + documentation).

---
