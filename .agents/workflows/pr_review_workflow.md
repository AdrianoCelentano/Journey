---
description: Work on an existing Pull Request based on comments
---

This workflow is triggered when you are asked to work on an existing Pull Request (PR) by addressing the review comments.
When applying this workflow, follow these exact steps:

1. **Read Comments**: Read the comments on the Pull Request using the `github-mcp-server` to fully understand what needs to be changed.
2. **Create Temporary Clone**: Create a temporary clone of the repo so it won't have any branch conflicts with other agents working in this repo in parallel. Check out the branch of the Pull Request in this temporary clone.
3. **Implement Feedback**: Make the required code changes to the source code to address the PR comments.
4. **Write Unit Tests**: Add unit tests for the changes you made. Run the testing suite by executing `./gradlew allTests`
5. **Static Analysis & Linting**: Run static analysis and linting tools configured in the project by executing `./gradlew staticAnalysis` to ensure no code style or quality warnings were introduced. Fix any errors if present.
6. **Update Documentation**: If necessary, create or update Markdown files inside the `/docs` folder of the main repository. synchronization to the wiki is handled automatically by a GitHub Action.
Rely on **Mermaid diagrams** for visualization of the logic. 
Ensure that you write:
    - A *domain-specific section* that explains the feature in plain English for non-technical stakeholders.
    - A *technical section* for developers, detailing the architectural changes.
7. **Push Changes**: Push your updated commits (code and documentation) up to the existing branch of the PR using Git (`git push`) so that the pull request updates automatically.
