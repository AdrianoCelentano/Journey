---
description: Resolve Auto-Assigned GitHub Issues
---

This workflow is triggered when you are asked to resolve a GitHub issue that has been assigned a specific label `enhancement`.
When applying this workflow, follow these exact steps:

1. **Understand Issue**: Read the GitHub issue details (title, description, comments) to fully understand the feature requirements using the `github-mcp-server`.
2. **Create Branch**: Create a new branch from `main` so the changes are not done on `main` and the PR will be on its own branch. Use git commands or the `github-mcp-server`.
3. **Implement Code**: Make the required code changes to the source code to implement the feature.
4. **Write Unit Tests**: Add unit tests for the changes you made. Run the testing suite by executing `./gradlew allTests`
5. **Static Analysis & Linting**: Run static analysis and linting tools configured in the project by executing `./gradlew staticAnalysis` to ensure no code style or quality warnings were introduced. Fix any errors if present.
6. **Create Pull Request**: Use the `github-mcp-server` to:
    - Commit your changes with a descriptive commit message.
    - Open a Pull Request on the repository explaining what was done, referencing the original issue.
7. **Update Documentation**: Create or update Markdown files inside the `/docs` folder of the main repository. Do NOT clone the wiki repository or push to it directly; synchronization to the wiki is handled automatically by a GitHub Action.
Rely on **Mermaid diagrams** for visualization of the logic. 
Ensure that you write:
    - A *domain-specific section* that explains the feature in plain English for non-technical stakeholders.
    - A *technical section* for developers, detailing the architectural changes.