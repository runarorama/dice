package object dice {

  import mset._
  import MSet._
  import Realm._
  import spire.math._
  import spire.implicits._
  import cats.kernel._
  import cats.implicits._
  import scala.language.implicitConversions

  /** Roll dice and get a random result. */
  def roll(d: Dice): Int = d.roll

  /** An n-sided die */
  def d(sides: Int) = Dice(Multiset(Range(1, sides + 1): _*))

  /** A die that always yields n */
  def constant(n: Int) = Dice(Multiset(n))

  /** A degenerate die that yields nothing. */
  val empty = Dice(MSet.empty)

  /** A die that always rolls 0. */
  val zero = constant(0)

  def dropKLowest(dice: Multiset[Dice], k: Natural): Dice =
    Dice(
      dice
        .toList(x => x)
        .traverse(_.rep)
        .map(_.toList.sorted.drop(k.toInt).sum))

  /**
    * Convert an integer to dice that always yield that integer.
    * Allows expressions like `4 d 6`
    */
  implicit def intToDice(n: Int): Dice = constant(n)
}
