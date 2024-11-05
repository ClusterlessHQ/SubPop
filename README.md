# SubPop

SubPop is a command line utility for finding the differences between one or more tabular datasets.

That is, it identifies the itemsets or patterns that occur in one class of data and not (or infrequently) in another
class of data.

## Overview

Consider the two classes (d1 and d2) below.

| class |   |   |   |   |
|-------|---|---|---|---|
| d1    | a | c | d | e |
| d1    | a |   |   |   |
| d1    | b | e |   |   |
| d1    | b | c | d | e |
| d2    | a | b |   |   |
| d2    | c | e |   |   |
| d2    | a | b | c | d |
| d2    | d | e |   |   |

SubPop will identify the item sets unique to each class. In this example the values are considered as a bag of values,
so the column is ignored.

| class |   |   |   | support |
|-------|---|---|---|---------|
| d1    | e | b |   | 0.5     |
| d1    | e | c | d | 0.5     |
| d2    | a | b |   | 0.5     |

Notice (e,b) only occurs in d1 twice, and in d2 zero times.

The support is the ratio of occurrences with the size of the class population.

## Installation

All SubPop releases are available via [Homebrew](https://brew.sh):

```shell
brew tap clusterlesshq/tap
brew install subpop
subpop --version
```

Available on Docker Hub:

- https://hub.docker.com/r/clusterless/subpop/tags

And, you can download the latest releases directly from GitHub:

- https://github.com/ClusterlessHQ/subpop/releases

## Usage

By default, SubPop will track values unique to columns.

Using the mushroom dataset, we see the first pattern found in the dataset below:

| class  | cap-shape | cap-surface | cap-color | bruises | odor | gill-attachment | gill-spacing | gill-size | gill-color | stalk-shape | stalk-root | stalk-surface-above-ring | stalk-surface-below-ring | stalk-color-above-ring | stalk-color-below-ring | veil-type | veil-color | ring-number | ring-type | spore-print-color | population | habitat | support   |
|--------|-----------|-------------|-----------|---------|------|-----------------|--------------|-----------|------------|-------------|------------|--------------------------|--------------------------|------------------------|------------------------|-----------|------------|-------------|-----------|-------------------|------------|---------|-----------|
| EDIBLE |           |             |           | BRUISES |      | FREE            |              |           |            | TAPERING    | BULBOUS    | SMOOTH                   | SMOOTH                   |                        |                        | PARTIAL   | WHITE      | ONE         | PENDANT   |                   |            | WOODS   | 0.4064171 |

Here we see the values that make up the pattern for the given class and the coverage (support) that class has over the
records within that class.

To see the full results:

```shell
./gradlew installDist
./build/install/subpop/bin/subpop --input src/test/resources/data/mushrooms.csv --input-header --min-ratio .4 --class-value EDIBLE
# or
cat src/test/resources/data/mushrooms.csv | ./build/install/subpop/bin/subpop --input-header --min-ratio .4 --class-value EDIBLE 
```

## CLI Options

```text
Usage: subpop [-hVv] [--input-header] [--output-header]
              [--class-col=<classIndex>] [--input-delimiter=<delimiter>]
              [--min-ratio=<supportRatio>] [--min-support=<support>]
              [--output-delimiter=<delimiter>] [--class-value=<classValue>]...
              [--input=<inputs>]...
a tool for diffing datasets
  -v, --verbose          Specify multiple -v options to increase verbosity.
                         For example, `-v -v -v` or `-vvv`
      --input=<inputs>   input data
      --input-header     has header
      --input-delimiter=<delimiter>
                         delimiter
      --output-header    has header
      --output-delimiter=<delimiter>
                         delimiter
      --class-col=<classIndex>
                         class column name or index
      --class-value=<classValue>
                         class value
      --min-support=<support>
                         minimum support
      --min-ratio=<supportRatio>
                         minimum support ratio
  -h, --help             Show this help message and exit.
  -V, --version          Print version information and exit.
```

## References

1. García‐Vico, A. M., Carmona, C. J., Martín, D., García‐Borroto, M. & Jesus, M. J. del. An overview of emerging
   pattern mining in supervised descriptive rule discovery: taxonomy, empirical study, trends, and prospects. Wiley
   Interdiscip. Rev.: Data Min. Knowl. Discov. 8, (2018).
2. Fan, H. & Ramamohanarao, K. Fast discovery and the generalization of strong jumping emerging patterns for building
   compact and accurate classifiers. IEEE Trans. Knowl. Data Eng. 18, 721–737 (2006).
