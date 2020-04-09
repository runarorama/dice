package dice

import mset._
import MSet._
import Realm._
import spire.math._

/**
 * A value of type `Dice` is a probability distribution of integers.
 *
 * @see See the package object [[dice]] for constructors. For example:
 *
 * - {{{ d(6) }}} is a 6-sided die.
 * - {{{ 2 d 6 }}} is the sum of two 6-sided dice.
 */
case class Dice(rep: Multiset[Int]) {
  import Dice._

  /** Combine two dice rolls using a function of their results. */
  def convolve(d: Dice)(f: (Int, Int) => Int): Dice =
    Dice(rep.productBy(d.rep)(f))

  /** Add the results of two rolls. */
  def +(d: Dice): Dice = convolve(d)(_ + _)

  /** Add a constant value to the roll. */
  def +(n: Int): Dice = this.+(constant(n))

  /** Multiply the result of one roll by the result of another. */
  def *(d: Dice): Dice = convolve(d)(_ * _)

  /** Multiply the result of the roll by a constant. */
  def *(n: Int): Dice = this.*(constant(n))

  /** Divide the result of one roll by the result of another, rounding down. */
  def /(d: Dice): Dice = convolve(d)(_ / _)

  /** Divide the result of the roll by a constant, rounding down. */
  def /(n: Int): Dice = this./(constant(n))

  /** Subtract one die roll from another. */
  def -(d: Dice): Dice = convolve(d)(_ - _)

  /** Subtract a constant from the roll. */
  def -(n: Int): Dice = this.-(constant(n))

  /** Roll a number of dice determined by the result of this roll. */
  def d(sides: Dice): Dice = for {
    n <- this
    k <- sides
    r <- List.fill(n)(dice.d(k)) match {
      case h :: t => t.foldLeft(h)(_ + _)
      case _ => empty
    }
  } yield r

  /** Roll a number of dice determined by the result of this roll. */
  def d(n: Int): Dice = d(constant(n))

  /**
   * Repeat this roll a number of times determined by another roll, summing
   * the results.
   */
  def repeat(n: Dice): Dice = for {
    v <- n
    r <- Range(1, v).map(_ => this).foldLeft(this)(_ + _)
  } yield r

  /** Repeat this roll a number of times, summing the results. */
  def repeat(n: Int): Dice = repeat(constant(n))

  /**
   * The probability distribution of the dice. The multiplicity of each
   * value in the result will be between 0 and 1, representing 0% and 100%
   * probability, respectively.
   */
  def probabilities: RatBag[Int] = {
    val sz = rep.size
    rep.mapOccurs(Rational(_,sz))
  }

  /**
   * Reroll any values matching the predicate
   */
  def reroll(p: Int => Boolean) = for {
    v <- this
    r <- if (p(v)) this else constant(v)
  } yield r

  /**
   * Change the result of the roll.
   * For example, `r.map(x => if (x == 1) 2 else x)` treats all rolls of 1 as 2.
   */
  def map(f: Int => Int) = Dice(rep map f)

  /**
   * Roll again depending on the result, and use the new result.
   */
  def flatMap(f: Int => Dice) = Dice(rep.flatMap(x => f(x).rep))

  /**
   * Compare two die rolls. The resulting distribution is the probability
   * that the second roll is at least as high as the first one.
   */
  def <=(d: Dice): Multiset[Boolean] = rep.productBy(d.rep)(_ <= _)

  /**
   * Compare two die rolls. The resulting distribution is the probability
   * that the first roll is at least as high as the second one.
   */
  def >=(d: Dice): Multiset[Boolean] = rep.productBy(d.rep)(_ >= _)

  /**
   * Compare two die rolls. The resulting distribution is the probability
   * that the first roll is higher than the second one.
   */
  def >(d: Dice): Multiset[Boolean] = rep.productBy(d.rep)(_ > _)

  /**
   * Compare two die rolls. The resulting distribution is the probability
   * that the first roll is lower than the second one.
   */
  def <(d: Dice): Multiset[Boolean] = rep.productBy(d.rep)(_ > _)

  /** Take the lower of two rolls. */
  def min(d: Dice): Dice = convolve(d)(_ min _)

  /** Take the higher of two rolls. */
  def max(d: Dice): Dice = convolve(d)(_ max _)

  /** Roll the dice and get a random result. */
  def roll: Int = {
    val ran = Rational(scala.util.Random.nextDouble)
    def go(r: List[(Int, Rational)], acc: Rational): Int = r match {
      case (n, m) :: xs => if (acc <= m) n else go(xs, acc - m)
      case _ => sys.error("sample: probability was higher than 100%")
    }
    go(probabilities.occurList, ran)
  }
}

