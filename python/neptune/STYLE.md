# Python Code Style Guidelines

This document defines the project's coding standards to ensure consistent, readable, and
maintainable code.

---

## 🛠️ Code Formatting and Tooling

### Automated Formatters (run in order)

1. **autoflake** – Remove unused imports and variables:
   ```bash
   autoflake --in-place --remove-all-unused-imports --remove-unused-variables <file>
   ```

2. **isort** – Organize imports:
   ```toml
   [tool.isort]
   profile = "black"
   line_length = 100
   multi_line_output = 3
   include_trailing_comma = true
   force_grid_wrap = 0
   use_parentheses = true
   ensure_newline_before_comments = true
   ```

3. **black** – Auto-format code to style standards:
   ```toml
   [tool.black]
   line-length = 100
   target-version = ["py39"]
   include = "\\.pyi?$"
   ```

### Quality Assurance Tools

- **mypy** – Static type checking:
  ```toml
  [tool.mypy]
  python_version = "3.9"
  disallow_untyped_defs = true
  disallow_incomplete_defs = true
  check_untyped_defs = true
  disallow_untyped_decorators = true
  no_implicit_optional = true
  warn_redundant_casts = true
  warn_unused_ignores = true
  warn_return_any = true
  warn_unreachable = true
  ```

- **flake8** – Linting:
  ```toml
  [flake8]
  max-line-length = 100
  extend-ignore = E203, W503
  max-complexity = 10
  ```

---

## 🧱 Code Structure

### File Layout

- **Top-level order**:
    1. Imports
    2. Constants
    3. Classes
    4. Functions

- **Import order**:
    1. Standard library
    2. Third-party libraries
    3. Local modules

  Example:
  ```python
  import os
  import numpy as np
  from myapp.core.module import some_function
  ```

### Sectioning and Organization

- Use **100-character section separators** with descriptive labels for major sections:
  ```python
  ####################################################################################################
  # CONSTANTS
  ####################################################################################################
  ```

- Within classes, group related methods under **50-character separators** (e.g., Accessors,
  Helpers).

- Place `__init__()` at the top of the class; factory methods like `from_*` should follow.

- Use descriptive module headers in large files.

---

## ✨ Formatting Standards

- **Max line length**: 100 characters
- **Indentation**: 4 spaces (no tabs)
- **Blank lines**:
    - Two between top-level functions/classes
    - One between logical blocks in functions

- **Method size**: Keep under 15–20 logical lines. Aim for single-responsibility.

- **Variable naming**:
    - `snake_case` for functions/variables
    - `UPPER_CASE` for constants
    - Prefix internal methods/variables with `_`

- **Imports**:
    - Prefer `from module import function` over `import module`
    - Avoid wildcard imports

- **Strings**: Prefer `f-strings`:
  ```python
  name = "Alice"
  print(f"Hello, {name}!")
  ```

---

## 📚 Documentation

### Docstrings

- Use **Google-style** docstrings.
- Start with a **present-tense verb** ("Calculates", "Returns", "Raises").
- Always include:
    - Purpose summary
    - `Args`
    - `Returns`
    - `Raises` (if applicable)
    - `Example` (for complex behavior)

  Example:
  ```python
  def get_average_duration(series, per=DAY) -> float:
      """
      Calculates the average duration between elements in a time series.

      Args:
          series (pd.Series): The input time series.
          per (int): Unit of time to divide by. Defaults to DAY.

      Returns:
          float: The average duration.
      """
  ```

### Private Functions

- Include docstrings for private functions **only if** their logic is non-obvious or essential.

---

## 🧠 Naming Conventions

| Entity    | Convention                         | Example                   |
|-----------|------------------------------------|---------------------------|
| Variables | `snake_case`                       | `user_count`, `is_active` |
| Functions | `snake_case`                       | `calculate_average()`     |
| Classes   | `PascalCase`                       | `TimeSeriesProcessor`     |
| Enums     | `PascalCase` + `UPPERCASE` members | `HttpMethod.GET`          |
| Constants | `UPPER_CASE`                       | `MAX_CONNECTIONS`         |
| Files     | `snake_case.py`                    | `time_series_utils.py`    |
| Packages  | lowercase                          | `utils`, `models`         |

### Class Naming

- Use **noun phrases**: `DataLoader`, `HttpClient`
- Exception classes end with `Error` or `Exception`
- Interfaces/abstracts: Use adjectives or capabilities: `Serializable`, `Cacheable`

---

## 🧪 Testing

- Cover **all public methods** and edge cases.
- Use descriptive function names:
  ```python
  def test_validate_user_raises_on_empty_input():
  ```

- Test both **happy paths** and **failure cases**.
- Include helpful assertion messages:
  ```python
  assert result == expected, "Expected value mismatch."
  ```

---

## ⚠️ Exception Handling

- Raise **specific exceptions** with meaningful messages:
  ```python
  if value < 0:
      raise ValueError("Value must be non-negative.")
  ```

- Log with full stack trace:
  ```python
  try:
      run()
  except Exception as e:
      logger.error("Unexpected error occurred", exc_info=True)
  ```

---

## 📋 Logging and Debugging

- Use the `logging` module, not `print()`.
- Apply appropriate logging levels (`DEBUG`, `INFO`, `WARNING`, etc.):
  ```python
  import logging

  logger = logging.getLogger(__name__)
  logger.info("Starting process")
  logger.debug(f"Transforming {len(data)} records")
  ```

---

## 🤖 Tooling Summary

| Tool        | Purpose                                 |
|-------------|-----------------------------------------|
| `autoflake` | Removal of unused imports and variables |
| `black`     | Automatic code formatting               |
| `flake8`    | Style and quality checks                |
| `isort`     | Import sorting and grouping             |
| `mypy`      | Static type checking                    |

> 💡 Automate tools via pre-commit hooks or CI pipelines.

---

## ✅ Example: Well-Structured Class

See [`examples/style_utility.py`](examples/style_utility.py) for a complete example of structure,
naming, docstrings, and conventions in practice.

---

## 🧭 Final Notes

- Be **consistent** even over perfection.
- Code should be **readable first**, efficient second.
- Use this style guide as a **living document** — update it as the codebase evolves.