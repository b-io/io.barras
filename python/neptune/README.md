# ♆ NEPTUNE

**NEPTUNE** is a modular suite of decoupled **Python libraries** designed to streamline and enhance
scientific and analytical development.

It includes focused packages for financial modeling, machine learning, statistics, visualization,
and core utilities — all built on top of **NumPy**, **Pandas**, **Matplotlib**, **Plotly**, **SciPy
**, **scikit-learn**, and **TensorFlow**.

---

## 🚀 Installation ########################################################################

Clone the repository and navigate to the NEPTUNE module:

```bash
git clone https://github.com/b-io/io.barras.git
cd io.barras/python/neptune/
```

---

## ⚙️ Environment Configuration ##########################################################

### 🐍 1. Install and Configure Poetry ######################

Install Poetry:

```bash
pip install poetry
```

Ensure Poetry uses your active Conda environment (or system Python):

```bash
python -m poetry config virtualenvs.create false --local
python -m poetry env info
```

### 📦 2. Install Project Dependencies ######################

Lock and install dependencies:

```bash
python -m poetry lock
python -m poetry install
```

View resolved packages:

```bash
python -m poetry show --tree
```

### 📤 3. Export Requirements (Optional) ####################

Install the export plugin:

```bash
python -m poetry self add poetry-plugin-export
```

Export grouped dependencies for use with `pip`:

```bash
python -m poetry export --without-hashes --with lint,test,docs --format=requirements.txt > requirements.txt
pip install -r requirements.txt
```

---

## 💡 Examples ############################################################################

> *"Talk is cheap. Show me the code."*  
> — Linus Torvalds

### 1. Utility Function: Apply Across Collections ##########

```python
from nutil.struct.util import apply

# Apply a lambda function to square values in a dictionary
data = {"a": 1, "b": 2, "c": 3}
squared = apply(lambda x: x ** 2, data)

print(squared)  # output: {'a': 1, 'b': 4, 'c': 9}
```

### 2. Math: Confidence Interval ###########################

```python
import pandas as pd
from nmath.stats.normal import NormalKDE

# Define a sample as a pandas Series
sample = pd.Series([100, 102, 98, 101, 99])

# Fit a NormalKDE distribution to the sample
normal_kde = NormalKDE(sample)

# Extract statistics and compute the 95% confidence interval
print(f"Estimated mean: {normal_kde.mean()}")
print(f"Estimated deviation: {normal_kde.std()}")
print(f"95% confidence interval: {normal_kde.interval(0.95)}")
```

### 3. Finance: Time Series Transformation #################

```python
from nfin.time_series import transform_series, Transformation
import pandas as pd

# Simulated price series
prices = pd.Series([100, 102, 105, 103, 108], index=pd.date_range("2024-01-01", periods=5))

# Compute log returns, grouped by month and using mean aggregation
log_returns = transform_series(prices, transf=Transformation.LOG_RETURNS)

print(log_returns)  # output: log return series
```

### 4. Learning: Text Classification #######################

```python
from nlearn.nlp import TextClassifier

# Train a simple text classifier
texts = ["Python is great for data science.", "Neural networks are a part of deep learning.",
        "Stocks and bonds are financial assets.", "Portfolio diversification reduces risk."]
labels = ["tech", "tech", "finance", "finance"]

classifier = TextClassifier()
classifier.train(texts, labels)

# Predict a new text sample
result = classifier.predict("Deep learning boosts AI performance.")
print(result)  # output: tech
```

### 5. GUI: Plot Multiple Time Series ######################

```python
import pandas as pd
from ngui.charts import plot_multi_series

# Generate multiple time series
dates = pd.date_range("2024-01-01", periods=5)
df = pd.DataFrame({"CPU": [10, 15, 12, 18, 14], "RAM": [30, 35, 32, 36, 31]}, index=dates)

# Plot both series on the same chart
plot_multi_series(df, title="System Metrics", show_legend=True, show_date=True)
```

---

## 📚 Library Structure ###################################################################

NEPTUNE is organized into the following modular subpackages:

| Package    | Description                                                             |
|------------|-------------------------------------------------------------------------|
| `nconnect` | Connectors to external systems (SQL query builder, I/O interfaces)      |
| `nfin`     | Time series and financial modeling tools (pricing, risk, forecasting)   |
| `nformat`  | Formatting functionalities for colors, images, and structured data      |
| `ngui`     | Charting, image display, and GUI rendering                              |
| `nlearn`   | Clustering, regression, and neural models for machine learning and NLP  |
| `nmath`    | Statistical distributions, sampling methods, and numerical computations |
| `nserve`   | Web-serving functionalities (HTTP servers, FastAPI helpers)             |
| `nutil`    | Configuration, constants, enums, and functional programming utilities   |

### 🧭 Exploring the API ####################################

- Browse the `source/<module>` directories for implementation details.
- Look for `test/` folders for usage examples and unit tests.
- All components are documented with structured docstrings for IDE and API documentation support.

---

## 📄 License #############################################################################

The libraries are released under the [MIT License](LICENSE).  
You are free to download, use, and share suggestions — contribute if you'd like to get involved.

[license]: <LICENSE>
