# Scholar-API automation

- Name: Jared Alexader Trujillo Ortiz
- NaoID: 3347
- Date: 30 September 2025

---

## 🎯 Project purpose
Automate the integration of profile and publication data for the Innovation Center's into the institution's research database using Google Scholar (via SerpApi). The automation replaces manual collection and entry, improves data freshness and centralizes documentation and version control on GitHub.

---

## 🔑 Key functionalities
- Query Google Scholar (via SerpApi) for a researcher’s publications, citations, and profile metadata.
- Map scholar JSON results to the institution DB schema.
- Extract data and stage it in-memory (Java), then export to integration-ready payloads.
- A Java integration program that writes the final records into the research database (via existing DB API).

---

## ❗ Why this matters
- Automates complex processes of browsing
- Eliminates repeated manual data entry and human error.
- Makes it easy to audit and trace changes (Git history + documentation).

---

Search screen
<img width="1918" height="1027" alt="Image" src="https://github.com/user-attachments/assets/4cf59f1a-0bca-464e-8c4a-df34ee89c921" />

Author detailed screen
<img width="1918" height="1015" alt="Image" src="https://github.com/user-attachments/assets/8146810a-f110-4fe1-af56-cc3e0700dcc6" />

Saved Articles
<img width="1917" height="872" alt="Image" src="https://github.com/user-attachments/assets/37d09e7f-1587-42ba-89c8-414e4c6ec2dc" />

