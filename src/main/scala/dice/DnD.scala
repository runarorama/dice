package dice

import mset._
import spire.math._
import mset._
import MSet._

object DnD {

  /** Roll for a single ability score: 4d6, dropping the lowest die. */
  def abilityScore: Dice =
    dropKLowest(Multiset[Dice]().insertN(d(6), Natural(4)), Natural(1))

  /** Generate all six ability scores. */
  def abilityScores: Multiset[Multiset[Int]] =
    Multiset[Dice]().insertN(abilityScore, Natural(6)).traverse(_.rep)

  def oldSchoolAbilityScores: Multiset[Multiset[Int]] =
    Multiset[Dice]().insertN(3 d 6, Natural(6)).traverse(_.rep)

  def attackRoll(ac: Int, bonus: Int, damage: Dice): Dice = for {
    roll <- d(20) + bonus
    dmg <- if (roll >= ac) damage else constant(0)
  } yield dmg
}
