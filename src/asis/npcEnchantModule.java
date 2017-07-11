/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import ASIS_Controller.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.ini4j.Config;
import org.ini4j.Ini;
import skyproc.*;

/**
 *
 * @author David Tynan
 */
public class npcEnchantModule extends Framework_Module_Abstract {

    private final String thisIniName = "NPCEnchantFix.ini";
    private final String perksSectionName = "PERKS";
    private final Ini thisIni;

    public npcEnchantModule() throws IOException {
        name = "NPC Enchantment Fix";
        thisIni = new Ini();
        Config c = thisIni.getConfig();
        c.setEmptyOption(true);
        c.setEmptySection(true);
        thisIni.load(new File(thisIniName));

        RecordSelector npcsToFix = new RecordSelectorBuilder().type(GRUP_TYPE.NPC_).RecordValidator(ASIS.validNPCs_NoSpellsPerks).build();
        npcsToFix.addInclusion(new SelectionSet(null, new Matcher.MatchAll()));
        this.addRecordSelector("npcsToFix", npcsToFix);
    }

    @Override
    public void runModuleChanges() {
        Mod merger = Controller.getAllRecords();
        Mod patch = SPGlobal.getGlobalPatch();

        HashMap<FormID, Integer> perksList = new HashMap<>();

        Ini.Section perkSection = thisIni.get(perksSectionName);
        for (Entry<String, String> perkEntry : perkSection.entrySet()) {
            PERK thePerk = (PERK) merger.getMajor(perkEntry.getKey(), GRUP_TYPE.PERK);
            if (thePerk != null) {
                Integer level;
                try {
                    level = Integer.parseInt(perkEntry.getValue());
                } catch (NumberFormatException numberFormatException) {
                    level = 1;
                }
                perksList.put(thePerk.getForm(), level);
            }
        }

//        PERK alchemyBoosts = (PERK) merger.getMajor("AlchemySkillBoosts", GRUP_TYPE.PERK);
//        if (alchemyBoosts == null){
//            throw new NullPointerException("Could not find Perk AlchemySkillBoosts");
//        }
//        FormID alchForm = alchemyBoosts.getForm();
//        PERK skillBoosts = (PERK) merger.getMajor("PerkSkillBoosts", GRUP_TYPE.PERK);
//        if (skillBoosts == null){
//            throw new NullPointerException("Could not find Perk PerkSkillBoosts");
//        }
//        FormID skillForm = skillBoosts.getForm();
        for (MajorRecord r : getRecordSelector("npcsToFix").getValidRecords()) {
            NPC_ n = (NPC_) r;
            if (n != null) {
                Set<FormID> perksToAdd = new HashSet<>(perksList.keySet());
                ArrayList<SubFormInt> perkList = n.getPerks();
                for (SubFormInt sub : perkList) {
                    if (perksToAdd.contains(sub.getForm())) {
                        perksToAdd.remove(sub.getForm());
                    }
                }
                if (!perksToAdd.isEmpty()) {
                    for (FormID perk : perksToAdd) {
                        n.addPerk(perk, perksList.get(perk));
                    }
                    ASIS.npcsToWrite.add(n.getForm());
                    patch.addRecord(n);
                }
            }
        }
    }

}
