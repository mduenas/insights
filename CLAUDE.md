# CLAUDE.md - insights

Insights - personal analytics and data insights app. Kotlin Multiplatform targeting Android and iOS.

## Memory

Use the **SimpleMem** MCP (`simplemem` tool) to store and retrieve project memory across sessions.
- Store: decisions, settings, file structure, user preferences, TODOs, constraints, recurring patterns
- Retrieve: query at the start of each session and before making significant decisions
- Tools: `memory_add`, `memory_query`, `memory_retrieve`, `memory_stats`, `memory_clear`
- Only save information that will be helpful across sessions.

