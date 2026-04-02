<!-- CODEGRAPH_START -->
## CodeGraph

CodeGraph builds a semantic knowledge graph of codebases for faster, smarter code exploration.

### If `.codegraph/` exists in the project

**Use codegraph MCP tools for faster exploration.** These tools provide instant lookups via the code graph instead of scanning files:

| Tool | Use For |
|------|---------|
| `codegraph_search` | Find symbols by name (functions, classes, types) |
| `codegraph_context` | Get relevant code context for a task |
| `codegraph_callers` | Find what calls a function |
| `codegraph_callees` | Find what a function calls |
| `codegraph_impact` | See what's affected by changing a symbol |
| `codegraph_node` | Get details + source code for a symbol |

**For quick lookups:**
- Use `codegraph_search` instead of grep for finding symbols
- Use `codegraph_callers`/`codegraph_callees` to trace code flow
- Use `codegraph_impact` before making changes to see what's affected

### If `.codegraph/` does NOT exist

At the start of a session, suggest initializing CodeGraph:

"I notice this project doesn't have CodeGraph initialized. You can run `codegraph init -i` to build a code knowledge graph for faster exploration."
<!-- CODEGRAPH_END -->
