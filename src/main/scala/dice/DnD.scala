package dice

import mset._
import spire.math._
import mset._
import MSet._

/** Various rolls for Dungeons & Dragons 5th edition */
object DnD {

  /** Roll for a single ability score: 4d6, dropping the lowest die. */
  def abilityScore: Dice =
    dropKLowest(Multiset().insertN(d(6), Natural(4)), Natural(1))

  /** Generate all six ability scores. */
  def abilityScores: Distribution[Multiset[Int]] =
    Multiset[Dice]().insertN(abilityScore, Natural(6)).traverse(_.rep)

  /** Generate ability scores, rolling straight 3d6, old school style. */
  def oldSchoolAbilityScores: Distribution[Multiset[Int]] =
    Multiset[Dice]().insertN(3 d 6, Natural(6)).traverse(_.rep)

  /**
    * An attack roll against armor class `ac` with attack bonus `bonus` and
    * damage dice `damage`. Returns the probability distribution of the damage.
    * A roll of 1 is an automatic miss, 20 is a critical hit (all damage dice
    * is rolled twice).
    */
  def attack(ac: Int, bonus: Int, damage: Dice): Dice =
    for {
      roll <- d(20)
      dmg <- if (roll == 20) damage + damage
      else if (roll == 1) constant(0)
      else if (roll + bonus >= ac) damage
      else constant(0)
    } yield dmg

  /**
    * A saving throw against difficulty class `dc`, with saving throw bonus
    * `bonus`. Rolls `fail` dice on a failed save and `success` dice on a
    * successful save. Gives the combined probability distribution for the
    * results of those rolls.
    */
  def savingThrow(bonus: Int, dc: Int, fail: Dice, success: Dice) =
    for {
      roll <- d(20) + bonus
      dmg <- if (roll >= dc) success else fail
    } yield dmg

  /** Rolls for various spells. */
  object Spells {

    /** The damage distribution of a Fireball spell. */
    def fireball(targetDexSaveBonus: Int,
                 casterSpellSaveDC: Int,
                 spellLevel: Int): Dice =
      // Fireball needs a spell slot of level 3 through 9
      if (spellLevel < 3 || spellLevel > 9) dice.empty
      else {
        val damageDice = (8 d 6) + (1 d 6).repeat(spellLevel - 3)
        savingThrow(targetDexSaveBonus,
                    casterSpellSaveDC,
                    damageDice / 2,
                    damageDice)
      }
  }
}
