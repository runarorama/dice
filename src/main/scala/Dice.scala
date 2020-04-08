package dice

import mset._
import MSet._
import Realm._
import spire.math._

case class Dice(rep: Multiset[Int]) {
  import Dice._

  /** Add the results of two rolls. */
  def +(d: Dice): Dice = Dice(rep.productBy(d.rep)(_ + _))

  /** Add a constant value to the roll. */
  def +(n: Int): Dice = this.+(constant(n))

  /** Multiply the result of one roll by the result of another. */
  def *(d: Dice): Dice = Dice(rep.productBy(d.rep)(_ * _))

  /** Multiply the result of the roll by a constant. */
  def *(n: Int): Dice = this.*(constant(n))

  /** Divide the result of one roll by the result of another. */
  def /(d: Dice): Dice = Dice(rep.productBy(d.rep)(_ / _))

  /** Divide the result of the roll by a constant */
  def /(n: Int): Dice = this./(constant(n))

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

  /** Roll the dice. */
  def sample: Int = {
    val ran = Rational(scala.util.Random.nextDouble)
    def go(r: List[(Int, Rational)], acc: Rational): Int = r match {
      case (n, m) :: xs => if (acc <= m) n else go(xs, acc - m)
      case _ => sys.error("sample: probability was higher than 100%")
    }
    go(probabilities.occurList, ran)
  }
}

object Dice {
  /** An n-sided die */
  def d(sides: Int) = Dice(Multiset(Range(1, sides + 1): _*))

  /** A die that always yields n */
  def constant(n: Int) = Dice(Multiset(n))

  /** A degenerate die that yields nothing. */
  val empty = Dice(MSet.empty)

  /** Allows expressions like `4 d 6` */
  implicit class DiceExp(dice: Int) {
    def d(sides: Int) = {
      val die = Dice.d(sides)
      List.fill(dice)(die) match {
        case h :: t => t.foldLeft(h)(_ + _)
        case _ => empty
      }
    }
  }
}

