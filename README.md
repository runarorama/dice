# dice
A Scala library for rolling dice and analyzing probability distributions

Examples:

* `3 d 6` represents a roll of three 6-sided dice.
* `(1 d 20) + 5` is a 20-sided die with 5 added to the result.
* `(2 d 8) * (1 d 2)` is two 8-sided dice which are multiplied by 2 if a coin comes up heads.
* `(8 d 6) / 2` is half the result of eight six-sided dice.
* `(1 d 6) reroll (_ < 2)` is a six-sided die, rerolling if it's 1, but keeping the reroll.
* `(1 d 6) reroll (_ < 2) repeat 8` is 8d6 rerolling any 1s.
