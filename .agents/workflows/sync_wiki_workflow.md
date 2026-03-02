---
description: Update and Structure the Repository Wiki
---

This workflow is triggered when you are asked to update, structure, and merge newly created documentation into the repository's wiki.

When applying this workflow, follow these exact steps:

1. **Scan for New Documentation**: Look for Markdown files in the main repository (e.g., inside the `/docs` directory) that have the prefix `new_doc_` (e.g., `new_doc_feature_name.md`). These are documentations from recently resolved issues.
2. **Clone Wiki Repository**: Clone the project's wiki repository into a separate directory outside the main project to avoid conflicts:
   `git clone https://github.com/AdrianoCelentano/Journey.wiki.git`
3. **Read and Analyze Content**: Open and read the content of each `new_doc_` file to understand the feature it describes.
4. **Structure by Feature in the Wiki**: The Wiki must be strictly organized by **Features**. 
   - For each `new_doc_` file, determine which core Feature it represents.
   - Look for the existing master document for that Feature inside the **cloned wiki repository**.
   - If a master document for the Feature does not exist, create a new one inside the wiki repository.
5. **Enforce Rigid Separation**: Inside each Feature's master documentation in the wiki, ensure a strict separation between:
    - **1. Domain-Specific Documentation**: Business rules, user flows, use cases, "what" and "why".
    - **2. Technical Documentation**: Architecture, API endpoints, database schema changes, "how", and Mermaid diagrams.
    Copy and integrate the content from the `new_doc_` file into these specific sections. Do not mix domain and technical details.
6. **Push to Wiki**: After organizing and formatting the files in the wiki repository, commit the changes with a clear message and **push** them to `https://github.com/AdrianoCelentano/Journey.wiki.git`.
7. **Clean Up Main Repository**: Go back to the main repository, **delete** the original `new_doc_` files that you processed, and commit and push the deletion so the main repository stays clean.
