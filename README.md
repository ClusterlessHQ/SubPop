## SubPop

SubPop is a command line utility for finding the differences between one or more tabular datasets.

That is, it identifies the itemsets or patterns that occur in one class of data and not (or infrequently) in another
class of data.

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

SubPop will identify the item sets unique to each class. In this example the values are considered as a bag of values, so
the column is ignored.

| class |   |   |   | support |
|-------|---|---|---|---------|
| d1    | e | b |   | 0.5     |
| d1    | e | c | d | 0.5     |
| d2    | a | b |   | 0.5     |

Notice (e,b) only occurs in d1 twice, and in d2 zero times. 

The support is the ratio of occurrences with the size of the class population.

# Installation

__CLI is still under development__

# Usage

```text
Usage: subpop [-hvV] [--input-header] [--output-header]
              [--class-col=<classIndex>] [--input-delimiter=<delimiter>]
              [--min-ratio=<supportRatio>] [--min-support=<support>]
              [--output-delimiter=<delimiter>] [--class-value=<classValue>]...
              [--input=<inputs>]...
...
      --class-col=<classIndex>
                         class column name or index
      --class-value=<classValue>
                         class value
  -h, --help             Show this help message and exit.
      --input=<inputs>   input data
      --input-delimiter=<delimiter>
                         delimiter
      --input-header     has header
      --min-ratio=<supportRatio>
                         minimum support ratio
      --min-support=<support>
                         minimum support
      --output-delimiter=<delimiter>
                         delimiter
      --output-header    has header
  -v, --verbose          ...
  -V, --version          Print version information and exit.
```

# References

1. García‐Vico, A. M., Carmona, C. J., Martín, D., García‐Borroto, M. & Jesus, M. J. del. An overview of emerging
   pattern mining in supervised descriptive rule discovery: taxonomy, empirical study, trends, and prospects. Wiley
   Interdiscip. Rev.: Data Min. Knowl. Discov. 8, (2018).
2. Fan, H. & Ramamohanarao, K. Fast discovery and the generalization of strong jumping emerging patterns for building
   compact and accurate classifiers. IEEE Trans. Knowl. Data Eng. 18, 721–737 (2006).
