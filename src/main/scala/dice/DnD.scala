package dice

import mset._
import spire.math._
import mset._
import MSet._

/** Various rolls for Dungeons & Dragons 5th edition */
object DnD {

  /** Roll for a single ability score: 4d6, dropping the lowest die. */
  def abilityScore: Dice =
    dropKLowest(MSet(d(6) -> Natural(4)), Natural(1))

  /** Generate all six ability scores. */
  def abilityScores: Distribution[Multiset[Int]] =
    MSet(abilityScore -> Natural(6)).traverse(_.rep)

  /** Generate ability scores, rolling straight 3d6, old school style. */
  def oldSchoolAbilityScores: Distribution[Multiset[Int]] =
    MSet(3.d(6) -> Natural(6)).traverse(_.rep)

  /**
    * An attack roll against armor class `ac` with attack bonus `bonus` and
    * damage dice `damage`. Returns the probability distribution of the damage.
    * A roll of 1 is an automatic miss, 20 is a critical hit (all damage dice
    * is rolled twice).
    *
    * `minCrit` is the minimum number to roll naturally on the d20 to score
    * a critical hit.
    */
  def attack(ac: Int, bonus: Int, damage: Dice, crit: Int = 20): Dice =
    for {
      roll <- d(20)
      dmg <- if (roll >= crit) damage + damage
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
  def savingThrow(bonus: Int, dc: Int, fail: Dice, success: Dice): Dice =
    for {
      roll <- d(20) + bonus
      dmg <- if (roll >= dc) success else fail
    } yield dmg

  /**
    * Attack using the Great Weapon Master feat. Takes a -5 penalty to the
    * attack, for a +10 bonus to damage and attacks again if the first attack
    * is a critical hit.
    */
  def greatWeaponMaster(ac: Int,
                        bonus: Int,
                        damage: Dice,
                        minCrit: Int = 20): Dice = {
    val gwmAttack = attack(ac, bonus - 5, damage, minCrit)
    for {
      roll <- d(20)
      dmg <- if (roll == 20) damage + damage + gwmAttack
      else if (roll == 1) constant(0)
      else if (roll + bonus - 5 >= ac) damage + 10
      else constant(0)
    } yield dmg
  }

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
                    damageDice,
                    damageDice / 2)
      }

    /** The total damage done to all targets of a Steel Wind Strike spell. */
    def steelWindStrike(spellAttackBonus: Int,
                        armorClasses: Multiset[Int]): Dice =
      armorClasses
        .map(attack(_, spellAttackBonus, 6 d 6))
        .toList
        .foldLeft(constant(0))(_ + _)
  }

  object Potions {

    /** The number of hit points healed by a Potion of Healing */
    val healing = (2 d 4) + 2

    /** The number of hit points healed by a Potion of Greater Healing */
    val greaterHealing = (4 d 4) + 4

    /** The number of hit points healed by a Potion of Superior Healing */
    val superiorHealing = (8 d 4) + 8

    /** The number of hit points healed by a Potion of Supreme Healing */
    val supremeHealing = (10 d 4) + 20
  }
}
