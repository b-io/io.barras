# 1. ENVIRONMENT ###################################################################################

PYTHON := poetry run
SRC := nfin ngui nlearn nmath nutil
TESTS := tests

# 2. FORMATTING ####################################################################################

.PHONY: format
format:  ## Format code using Black and isort
	$(PYTHON) isort $(SRC)
	$(PYTHON) black $(SRC)

.PHONY: autoflake
autoflake:  ## Remove unused imports and variables
	$(PYTHON) autoflake --recursive --in-place --expand-star-imports --ignore-init-module-imports --remove-all-unused-imports --remove-unused-variables $(SRC)

# 3. LINTING #######################################################################################

.PHONY: lint
lint: format autoflake flake8 mypy  ## Run all linting checks

.PHONY: flake8
flake8:  ## Run Flake8 with plugins
	$(PYTHON) flake8 $(SRC)

.PHONY: mypy
mypy:  ## Run static type checks
	$(PYTHON) mypy $(SRC)

# 4. TESTING #######################################################################################

.PHONY: test
test:  ## Run all unit tests
	$(PYTHON) pytest -m "not integration_test" --disable-warnings

.PHONY: test-all
test-all:  ## Run full test suite including integration
	$(PYTHON) pytest --disable-warnings

.PHONY: coverage
coverage:  ## Run tests and generate coverage report
	$(PYTHON) coverage run -m pytest
	$(PYTHON) coverage report -m
	$(PYTHON) coverage html

# 5. DOCUMENTATION #################################################################################

.PHONY: docs
docs:  ## Build Sphinx documentation
	cd docs && $(PYTHON) make html

# 6. CLEANING ######################################################################################

.PHONY: clean
clean:  ## Remove cache and build artifacts
	rm -rf .pytest_cache .mypy_cache dist build htmlcov
	find . -type d -name "__pycache__" -exec rm -rf {} +

# 7. HELP ##########################################################################################

.PHONY: help
help:  ## Show this help message
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
	awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'
