package app.models.enums;

import app.models.Monster;
import app.models.interfaces.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.function.Supplier;

public enum MonsterType {
    ACIDIC_SWAMP_OOZE("Acidic_Swamp_Ooze", 3, 2, 2, Rarity.COMMON),
    ARCHMAGE("Archmage", 4, 7, 6, Rarity.COMMON),
    BLOODFEN_RAPTOR("Bloodfen_Raptor", 3, 2, 2, Rarity.COMMON),
    BLUEGILL_WARRIOR("Acidic_Swamp_Ooze", 2, 1, 2, Rarity.COMMON),
    BOOTY_BAY_BODYGUARD("Booty_Bay_Bodyguard", 5, 4, 5, Rarity.COMMON),
    BOULDERFIST_OGRE("Boulderfist_Ogre", 6, 7, 6, Rarity.COMMON),
    CHILLWIND_YETI("Chillwind_Yeti", 4, 5, 4, Rarity.COMMON),
    CORE_HOUND("Core_Hound", 9, 5, 7, Rarity.COMMON),
    DALARAN_MAGE("Dalaran_Mage", 1, 4, 3, Rarity.COMMON),
    DARKSCALE_HEALER("Darkscale_Healer", 4, 5, 5, Rarity.COMMON),
    DRAGONLING_MECHANIC("Dragonling_Mechanic", 2, 4, 4, Rarity.COMMON),
    ELVEN_ARCHER("Elven_Archer", 1, 1, 1, Rarity.COMMON),
    FROSTWOLF_GRUNT("Frostwolf_Grunt", 2, 2, 2, Rarity.COMMON),
    FROSTWOLF_WARLORD("Frostwolf_Warlord", 4, 4, 5, Rarity.COMMON),
    GNOMISH_INVENTOR("Gnomish_Inventor", 2, 4, 4, Rarity.COMMON),
    GOLDShIRE_FOOTMAN("Goldshire_Footman", 1, 2, 1, Rarity.COMMON),
    GRIMSCALE_ORACLE("Grimscale_Oracle", 1, 1, 1, Rarity.COMMON),
    GURUBASHI_BERSERKER("Gurubashi_Berserker", 2, 7, 5, Rarity.COMMON),
    IRONFORGE_RIFLEMAN("Ironforge_Rifleman", 2, 2, 3, Rarity.COMMON),
    IRONFUR_GRIZZLY("Ironfur_Grizzly", 3, 3, 3, Rarity.COMMON),
    KOBOLD_GEOMANCER("Kobold_Geomancer", 2, 2, 2, Rarity.COMMON),
    LORD_OF_THE_ARENA("Lord_of_the_Arena", 6, 5, 6, Rarity.COMMON),
    MAGMA_RAGER("Magma_Rager", 5, 1, 3, Rarity.COMMON),
    MURLOC_RAID("Murloc_Raid", 2, 1, 1, Rarity.COMMON),
    MURLOC_TIDEHUNTER("Murloc_Tidehunter", 2, 1, 2, Rarity.COMMON),
    NIGHTBLADE("Nightblade", 4, 4, 5, Rarity.COMMON),
    NOVICE_ENGINEER("Novice_Engineer", 2, 1, 2, Rarity.COMMON),
    OASIS_SNAPJAW("Oasis_Snapjaw", 2, 7, 4, Rarity.COMMON),
    OGRE_MAGI("Ogre_Magi", 4, 4, 4, Rarity.COMMON),
    RAID_LEADER("Raid_Leader", 2, 2, 3, Rarity.COMMON),
    RAZORFEN_HUNTER("Razorfen_Hunter", 2, 3, 3, Rarity.COMMON),
    RIVER_CROCOLISK("River_Crocolisk", 2, 3, 2, Rarity.COMMON),
    SEN_JIN_SHIELDMASTA("Sen_jin_Shieldmasta", 3, 5, 4, Rarity.COMMON),
    SHATTERED_SUN_CLERIC("Shattered_Sun_Cleric", 3, 2, 3, Rarity.COMMON),
    SILVERBACK_PATRIARCH("Silverback_Patriarch", 1, 4, 3, Rarity.COMMON),
    STONETUSK_BOAR("Stonetusk_Boar", 1, 1, 1, Rarity.COMMON),
    STORMPIKE_COMMANDO("Stormpike_Commando", 4, 2, 5, Rarity.COMMON),
    STORMWIND_CHAMPION("Stormwind_Champion", 6, 6, 7, Rarity.COMMON),
    STORMWIND_KNIGHT("Stormwind_Knight", 2, 5, 4, Rarity.COMMON),
    VOODOO_DOCTOR("Voodoo_Doctor", 2, 1, 1, Rarity.COMMON),
    WAR_GOLEM("War_Golem", 7, 7, 7, Rarity.COMMON),
    WOLFRIDER("Wolfrider", 3, 1, 3, Rarity.COMMON);

    @Getter
    private String name;
    @Getter
    private int damage;
    @Getter
    private int health;
    @Getter
    private int mana;
    @Getter
    private Rarity rarity;

    MonsterType(String name, int damage, int health, int mana, Rarity rarity) {
        this.name = name;
        this.damage = damage;
        this.health = health;
        this.mana = mana;
        this.rarity = rarity;
    }
}
