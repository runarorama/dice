package object dice {

  import mset._
  import MSet._
  import Realm._
  import spire.math._
  import spire.implicits._
  import cats.kernel._
  import cats.implicits._
  import scala.language.implicitConversions

  /**
   * A distribution is an mset with rational multiplicities, with the convention
   * that the size of the mset is exactly 1 or 0.
   */
  type Distribution[A] = RatBag[A]

  /** Roll dice and get a random result. */
  def roll(d: Dice): Int = d.roll

  /** An n-sided die */
  def d(sides: Int) =
    Dice(Range(1, sides + 1).foldLeft(empty.rep) {
      case (bag, n) => bag.insertN(n, Rational(1, sides))
    })

  /** A die that always yields n */
  def constant(n: Int) = Dice(empty.rep.insertN(n, Rational(1)))

  /** A degenerate die that yields nothing. */
  val empty = Dice(MSet.empty)

  /** A die that always rolls 0. */
  val zero = constant(0)

  /** Sum multiple rolls, dropping the lowest `k` results. */
  def dropKLowest(dice: Multiset[Dice], k: Natural): Dice =
    Dice(
      dice
        .traverse(_.rep)
        .map(_.toList.sorted.drop(k.toInt).sum))

  /**
    * Convert an integer to dice that always yield that integer.
    * Allows expressions like `4 d 6`
    */
  implicit def intToDice(n: Int): Dice = constant(n)
}
