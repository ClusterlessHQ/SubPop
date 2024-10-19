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
