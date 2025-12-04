# CODING STYLE #########################################################################################################

## 1) Principles & Conventions ###########################################################

- Documentation must **match code exactly** — no hallucinations.
- Code and docs must be **auditor-grade**: rigorous and review-ready.
- Prefer being **consistently reasonable** over occasionally perfect.
- If introducing a pattern (e.g., a cache key format), **document it** and use it everywhere.
- Common and util files are imported **down → up**, then available globally.
- Keep **categories as separate enums**.

## 2) Banners & Headers ##################################################################

### Unified banner format (Python & Markdown) ##############

- Use hash banners to mark sections and make them easy to scan.
- A banner line starts with optional spaces, then one or more `#`, then a space, a title, a space, then trailing `#`.
- The total line width must be **30**, **60**, **90**, or **120** characters.
- In code and Markdown, use the same alignment rules and widths.

### Levels & widths ########################################

- Level 1 (120):
  `# SECTION ##############################################################################################################`
- Level 2 (90):  `## SUBSECTION ############################################################################`
- Level 3 (60):  `### SUBSUBSECTION ##########################################`
- Level 4 (30):  `#### SUBSUBSUBSECTION ########`

## 3) Language & Wording #################################################################

- Use **articles** naturally and consistently: “the cache”, “a JSON file”, “an ID”.
- Prefer **“specified”** over **“given”**.
- **Quoting rule:**
    - In **docstrings/comments**, show **string values** as **double-quoted inside backticks**: `"error"`, `"warning"`,
      `"--flag"`.
    - In **runtime log/CLI messages**, surround dynamic identifiers, paths, and literal tokens with **single quotes**:
      e.g., `Missing 'API_SECRET' environment variable.`
    - Use **backticks** for code identifiers (modules, classes, functions, variables, types, env var names), without
      additional quotes.
- **No trailing spaces.**

## 4) Docstrings & Comments ##############################################################

- **Functions/methods:** begin with an **s-verb** in third-person singular: “Scans…”, “Selects…”, “Serves…”, “Saves…”.
- **Classes:** begin with **an article + noun**: “A parser for …”, “The client for …”.
- **Sections:** Google style only — `Args:`, `Returns:`, `Raises:` (no underlines).
- **Formatting:**
    - One-line: `"""Short summary."""`
    - Multi-line: opening `"""` on its own line → summary → blank line → sections; closing `"""` on its own line.
- Use backticks for code identifiers in docstrings and comments (types, names, file names, flags, QIDs).
- When documenting string choices/values, use double-quoted strings inside backticks (e.g.,
  `severity: The severity string ("error" or "warning").`).
- Keep terminology consistent within a file/module (e.g., “cache”, “payload”, “entity JSON”).
- Prefer **why** over **what** in inline comments; the code should show *what*.
- **Line comments (own line):** Start uppercase, include an article when natural (“the…”, “a…”), and do not end with a
  period.
- **Inline comments (after code):** Short noun phrases that start lowercase, include an article when naming a noun, and
  do not end with a period.

## 5) Type Hints, Style & Structure ######################################################

- Follow **PEP 8**: `lower_snake_case` for functions/variables, `UpperCamelCase` for classes, `UPPER_SNAKE_CASE` for
  constants.
- Use **type hints everywhere**; return **concrete, parameterized** types (e.g., `List[str]`, `Dict[str, Any]`).
- In signatures, prefer `typing` collection aliases (`List`, `Dict`, `Set`, `Tuple`, etc.) and always parameterize when
  possible.
- Apply strong typing when helpful (`TypeVar`, `Type[T]`, `Iterator[Tuple[...]]`).
- Keep functions **small and focused** (single responsibility).
- Prefer **dataclasses** for simple data carriers.
- Organize class sections by **inheritance hierarchy** or **alphabetical**.
- Maintain consistent **banner sections** and concise **docstrings**.

## 6) Logging & Errors ###################################################################

- **Levels:** `info` for progress; `warning` for recoverable issues; `error` when an action fails; `debug` for noisy
  internals.
- Include **context** in messages (identifiers, counts, sizes, HTTP status).
- **Raise specific exceptions** and document them in `Raises:`.
- For CLIs, validate early and `sys.exit(...)` with clear messages (use single quotes around dynamic names/paths).

## 7) Files, Paths & Atomic Writes #######################################################

- Never assume directories exist; create with `mkdir(parents=True, exist_ok=True)`.
- Prefer **atomic replace**: write to a temp file in the same directory, then `os.replace`.
- Resolve and normalize user-supplied paths; do not fail on intended outputs that do not yet exist.
- For caches, **create the file if missing** (e.g., write `{}` for JSON) rather than failing.
- In long-running jobs that write frequently, **flush and attempt `fsync`**; treat `fsync` failures as **non-fatal**.

## 8) Networking & External APIs #########################################################

- Use a `requests.Session` or equivalent.
- Timeouts are mandatory; set sensible defaults and allow override.
- Retry with backoff on transient errors (e.g., `429`/`5xx`); do not retry on non-transient `4xx`.
- Keep secrets in environment variables; never hard-code tokens.
- Cache responses when appropriate (memory → disk) to reduce traffic and improve resilience.

## 9) Data Handling ######################################################################

- Validate inputs and schemas (required columns, expected types).
- When enriching data, preserve unknown columns and row order unless explicitly stated.
- When deduplicating sequences, preserve order unless a set is required by spec.
- Prefer pure functions for transformations (no I/O, no global mutation).

## 10) CLI Conventions ###################################################################

- Provide `--help` with clear descriptions and defaults in help text.
- Use explicit, descriptive flags; separate required from optional arguments.
- Print a concise **start summary** (inputs, outputs, key options) and a **result line** at finish.

## 11) Imports ###########################################################################

- Prefer explicit imports; avoid `*` imports.

## 12) Enums & Metaclasses ###############################################################

- Validate subclassing in `__new__` where appropriate.
- Enforce member value types; raise clear `TypeError`s.
- Provide ergonomic helpers: `from_name`, `from_value`, `names`, `values`.
- Favor O(1) lookups (`__members__`, `_value2member_map_`) and document complexity.
- Uniform errors:
  ```
  "'{x}' is not a valid {name|value} for '{cls.__name__}'"
  ```

## 13) Performance & Reliability #########################################################

- Avoid unnecessary re-parsing/re-requests; use layered caching (memory → disk → network).
- Stream large files where possible (chunked reads/writes).
- Persist progress periodically in long-running jobs (e.g., every `N` rows).
- Guard critical sections with `try`/`except` and log meaningful context.

## 14) Security & Privacy ################################################################

- Never log secrets or raw PII; redact sensitive fields in logs.
- Validate/escape user input that affects file paths, shells, or SQL.
- Pin dependencies and track known vulnerabilities.

## 15) Testing ###########################################################################

- Write unit tests for pure helpers and edge cases.
- Use fixtures for sample payloads/responses.
- Mock network calls in tests; do not hit real services.
- Test failure paths (timeouts, bad inputs, empty files).

## 16) Versioning & Git Hygiene ##########################################################

- Commit small, atomic changes with descriptive messages (imperative mood: “Add…”, “Fix…”).
- Keep branches focused; rebase or squash before merging.
- Update `CHANGELOG.md` and bump versions when applicable.

## 17) Documentation & Examples ##########################################################

- Provide a README with a quick-start guide, examples, and common pitfalls.
- Include usage examples for CLIs and public APIs.
- Keep docstrings in sync with behavior (especially `Raises:` and defaults).

## 18) Configuration & Defaults ##########################################################

- Keep secrets in environment variables; allow flags/config files for behavior.
- Choose safe, conservative defaults; make advanced options opt-in.
- Surface key parameters as CLI flags or config entries.
