/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import skyproc.SkyProcSave;

/**
 *
 * @author pc tech
 */
public class ASISSaveFile extends SkyProcSave {

    @Override
    protected void initSettings() {
        Add(GUISettings.INCREASEDSPAWNS_ON, true, true);
        Add(GUISettings.ISSPAWN0WEIGHT, 200.0, true);
        Add(GUISettings.ISSPAWN1WEIGHT, 150.0, true);
        Add(GUISettings.ISSPAWN2WEIGHT, 35.0, true);
        Add(GUISettings.ISSPAWN3WEIGHT, 10.0, true);
        Add(GUISettings.ISSPAWN4WEIGHT, 3.0, true);
        Add(GUISettings.ISSPAWN5WEIGHT, 2.0, true);
        Add(GUISettings.ISSPAWN6WEIGHT, 0.0, true);
        Add(GUISettings.ISSPAWN7WEIGHT, 0.0, true);
        Add(GUISettings.ISSPAWN8WEIGHT, 0.0, true);
        Add(GUISettings.ISSPAWN9WEIGHT, 0.0, true);
        Add(GUISettings.ISUSEINTERIORSPAWNS, true, true);
        Add(GUISettings.ISREDUCEDINTERIORSPAWNS, 2.0, true);

        Add(GUISettings.NPCPOTIONS_ON, true, true);
        Add(GUISettings.NUMBER_OF_POTIONS, 5, true);
        Add(GUISettings.CHANCE_PER_POTION, 10, true);

        Add(GUISettings.CUSTOMIZEDAI_ON, false, true);

        Add(GUISettings.AUTOMATICPERKS_ON, true, true);

        Add(GUISettings.AUTOMATICSPELLS_ON, true, true);

        Add(GUISettings.SPAWNRANDOMIZER_ON, false, true);
        Add(GUISettings.SRSPAWN0WEIGHT, 0, true);
        Add(GUISettings.SRSPAWN1WEIGHT, 0, true);
        Add(GUISettings.SRSPAWN2WEIGHT, 50, true);
        Add(GUISettings.SRSPAWN3WEIGHT, 25, true);
        Add(GUISettings.SRSPAWN4WEIGHT, 15, true);
        Add(GUISettings.SRSPAWN5WEIGHT, 3, true);
        Add(GUISettings.SRSPAWN6WEIGHT, 1, true);
        Add(GUISettings.SRSPAWN7WEIGHT, 0, true);
        Add(GUISettings.SRSPAWN8WEIGHT, 0, true);
        Add(GUISettings.SRSPAWN9WEIGHT, 0, true);
        Add(GUISettings.SRUSEINTERIORSPAWNS, false, true);
        Add(GUISettings.SRRANDOMSPAWNCHANCE, 3, true);
        Add(GUISettings.SRMULTIGROUPCHANCE, 10, true);
        Add(GUISettings.SRNUMMAXGROUPS, 3, true);

        Add(GUISettings.CUSTOMIZEDGMSTS_ON, false, true);

        Add(GUISettings.ASIS_SETTINGS, true, true);
        Add(GUISettings.NPCENCHANTFIX_ON, true, true);
    }

    @Override
    protected void initHelp() {

        helpInfo.put(GUISettings.INCREASEDSPAWNS_ON, "This feature will increase the spawns on all NPC's.");
        helpInfo.put(GUISettings.ISSPAWN0WEIGHT, "This is the corresponding chance that no extra spawns will occur.  "
                + "The higher the number, the higher the chance for no spawns.");
        helpInfo.put(GUISettings.ISSPAWN1WEIGHT, "This is the corresponding chance that 1 extra spawn will occur.  "
                + "The higher the number, the higher the chance for 1 extra spawn.");
        helpInfo.put(GUISettings.ISSPAWN2WEIGHT, "This is the corresponding chance that 2 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 2 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN3WEIGHT, "This is the corresponding chance that 3 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 3 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN4WEIGHT, "This is the corresponding chance that 4 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 4 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN5WEIGHT, "This is the corresponding chance that 5 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 5 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN6WEIGHT, "This is the corresponding chance that 6 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 6 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN7WEIGHT, "This is the corresponding chance that 7 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 7 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN8WEIGHT, "This is the corresponding chance that 8 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 8 extra spawns.");
        helpInfo.put(GUISettings.ISSPAWN9WEIGHT, "This is the corresponding chance that 9 extra spawns will occur.  "
                + "The higher the number, the higher the chance for 9 extra spawns.");
         helpInfo.put(GUISettings.ISUSEINTERIORSPAWNS, "This will enable increased spawns in areas marked as interiors");
        helpInfo.put(GUISettings.ISREDUCEDINTERIORSPAWNS, "This feature will reduce the chance of extra spawns in interiors"
                + " by a factor of the entered value. Only used if interior spawns are enabled.");

        helpInfo.put(GUISettings.NPCPOTIONS_ON, "This feature will add potions to NPC's.  Requires version 1.6.89, "
                + "and is currently undergoing some issues and thus may not work as intended.");
        helpInfo.put(GUISettings.NUMBER_OF_POTIONS, "This setting determines how many potions will be used in the potion chances.");
        helpInfo.put(GUISettings.CHANCE_PER_POTION, "This setting determines how likely it is a potion will be given to an NPC.");

        helpInfo.put(GUISettings.CUSTOMIZEDAI_ON, "This feature will modify decisions the AI makes to make them "
                + "more intelligent, with smarter and faster reactions with less restrictions.");

        helpInfo.put(GUISettings.AUTOMATICPERKS_ON, "This feature will algorithmically give perks to NPC's that "
                + "the player has access to, but they are not given.  This includes mod changed or added perks.");

        helpInfo.put(GUISettings.AUTOMATICSPELLS_ON, "This feature will algorithmically give spells to NPC's "
                + "that meet the skill requirements for a spell, determined by the perk that halves the spell cost. "
                + "So if an NPC has meets the requirements for the Adept Destruction perk, they will be given all the "
                + "Adept destruction magic spells.  This includes mod-added spells.");

        helpInfo.put(GUISettings.SPAWNRANDOMIZER_ON, "This feature implements a randomized spawner, which gives "
                + "chances to spawn a group, or groups, of enemies defined in the ini files.  This means you could have "
                + "a group of dremora spawn in the wilderness, for example, by attaching the group to a rabbit spawn."
                + "\n\nThis would be similar to some features of the older mod Wars in Skyrim, "
                + "albeit customizable due to SkyProc.\n\nWARNING: This feature is purely experimental and has been known to break games.  "
                + "Use at your own risk!");
        helpInfo.put(GUISettings.SRSPAWN0WEIGHT, "This is the corresponding chance that no extra spawns will occur.  "
                + "The higher the number, the higher the chance for no spawns.");
        helpInfo.put(GUISettings.SRSPAWN1WEIGHT, "This is the corresponding chance that a group will have 1 spawn.  "
                + "The higher the number, the higher the chance for 1 spawn in a group.");
        helpInfo.put(GUISettings.SRSPAWN2WEIGHT, "This is the corresponding chance that a group will have 2 spawns.  "
                + "The higher the number, the higher the chance for 2 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN3WEIGHT, "This is the corresponding chance that a group will have 3 spawns.  "
                + "The higher the number, the higher the chance for 3 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN4WEIGHT, "This is the corresponding chance that a group will have 4 spawns.  "
                + "The higher the number, the higher the chance for 4 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN5WEIGHT, "This is the corresponding chance that a group will have 5 spawns.  "
                + "The higher the number, the higher the chance for 5 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN6WEIGHT, "This is the corresponding chance that a group will have 6 spawns.  "
                + "The higher the number, the higher the chance for 6 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN7WEIGHT, "This is the corresponding chance that a group will have 7 spawns.  "
                + "The higher the number, the higher the chance for 7 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN8WEIGHT, "This is the corresponding chance that a group will have 8 spawns.  "
                + "The higher the number, the higher the chance for 8 spawns in a group.");
        helpInfo.put(GUISettings.SRSPAWN9WEIGHT, "This is the corresponding chance that a group will have 9 spawns.  "
                + "The higher the number, the higher the chance for 9 spawns in a group.");
        helpInfo.put(GUISettings.SRUSEINTERIORSPAWNS, "This determines if the random group spawns can happen indoors.");
        helpInfo.put(GUISettings.SRRANDOMSPAWNCHANCE, "This is the chance for a random group to spawn - it's per NPC, so watch when turned up.");
        helpInfo.put(GUISettings.SRMULTIGROUPCHANCE, "This setting determines the chance for multiple groups to appear.");
        helpInfo.put(GUISettings.SRNUMMAXGROUPS, "This setting determines the maximum amount of groups to be spawned.");

        helpInfo.put(GUISettings.CUSTOMIZEDGMSTS_ON, "This feature will allow customization of GMST's for Skyrim.  They are defined"
                + " in the ini file for it.  By default nothing is changed with this, as the GMST's will be vanilla GMST's, so unless you"
                + " modify the settings, it is recommended to just leave this off.");

        helpInfo.put(GUISettings.ASIS_SETTINGS, "This is the main configuration of ASIS.");
        helpInfo.put(GUISettings.NPCENCHANTFIX_ON, "Add perks to NPCs to allow use of all enchantments and potions");
    }

    public enum GUISettings {

        INCREASEDSPAWNS_ON,
        ISSPAWN0WEIGHT,
        ISSPAWN1WEIGHT,
        ISSPAWN2WEIGHT,
        ISSPAWN3WEIGHT,
        ISSPAWN4WEIGHT,
        ISSPAWN5WEIGHT,
        ISSPAWN6WEIGHT,
        ISSPAWN7WEIGHT,
        ISSPAWN8WEIGHT,
        ISSPAWN9WEIGHT,
        ISUSEINTERIORSPAWNS,
        ISREDUCEDINTERIORSPAWNS,
        NPCPOTIONS_ON,
        NUMBER_OF_POTIONS,
        CHANCE_PER_POTION,
        CUSTOMIZEDAI_ON,
        AUTOMATICPERKS_ON,
        AUTOMATICSPELLS_ON,
        SPAWNRANDOMIZER_ON,
        SRSPAWN0WEIGHT,
        SRSPAWN1WEIGHT,
        SRSPAWN2WEIGHT,
        SRSPAWN3WEIGHT,
        SRSPAWN4WEIGHT,
        SRSPAWN5WEIGHT,
        SRSPAWN6WEIGHT,
        SRSPAWN7WEIGHT,
        SRSPAWN8WEIGHT,
        SRSPAWN9WEIGHT,
        SRUSEINTERIORSPAWNS,
        SRRANDOMSPAWNCHANCE,
        SRMULTIGROUPCHANCE,
        SRNUMMAXGROUPS,
        CUSTOMIZEDGMSTS_ON,
        ASIS_SETTINGS,
        NPCENCHANTFIX_ON,
        OTHER_SETTINGS
    }
    
//    public enum Settings {
//        NO_BOSS,
//	IMPORT_AT_START,
//	OTHER_SETTINGS;
//    }
}