---
description: Resolve Auto-Assigned GitHub Issues
---

This workflow is triggered when you are asked to resolve a GitHub issue that has been assigned a specific label `enhancement`.
When applying this workflow, follow these exact steps:

1. **Understand Issue**: Read the GitHub issue details (title, description, comments) to fully understand the feature requirements using the `github-mcp-server`.
2. **Implement Code**: Make the required code changes to the source code to implement the feature.
3. **Write Unit Tests**: Add unit tests for the changes you made. Run the testing suite by executing ./gradlew allTests`
4. **Static Analysis & Linting**: Run static analysis and linting tools configured in the project by executing `./gradlew staticAnalysis` to ensure no code style or quality warnings were introduced. Fix any errors if present.
5. **Create Pull Request**: Use the `github-mcp-server` to:
    - Create a new branch.
    - Commit your changes with a descriptive commit message.
    - Open a Pull Request on the repository explaining what was done, referencing the original issue.
6. **Update Documentation (Wiki)**: Update the project's documentation on Github. Rely on **Mermaid diagrams** for visualization of the logic. 
Ensure that you write:
    - A *domain-specific section* that explains the feature in plain English for non-technical stakeholders.
    - A *technical section* for developers, detailing the architectural changes.