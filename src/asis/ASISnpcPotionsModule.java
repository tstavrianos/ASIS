/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import ASIS_Controller.*;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import org.ini4j.Config;
import org.ini4j.Ini;
import skyproc.*;
import skyproc.gui.*;

public class ASISnpcPotionsModule extends Framework_Module_Abstract {

    private final String patchSettings = "PatchSettings";
    private final String NPCInclusions = "NPCInclusions";
    private final String NPCExclusions = "NPCExclusions";
    private final String modExclusions = "ModExclusions";
    private final String hpPotions = "HP_Potions";
    private final String mpPotions = "MP_Potions";
    private final String spPotions = "SP_Potions";

    private final Ini thisIni;
    private final String thisIniName = "NPCPotions.ini";

    public ASISnpcPotionsModule() throws IOException {
        name = "NPC Potions";
        thisIni = new Ini();
        Config c = thisIni.getConfig();
        c.setEmptyOption(true);
        c.setEmptySection(true);
        thisIni.load(new File(thisIniName));

        RecordSelector npcsToGetPotions = new RecordSelectorBuilder().type(GRUP_TYPE.NPC_).RecordValidator(ASIS.validNPCs_NoScripts).build();
        npcsToGetPotions.addInclusion(new SelectionSet(thisIni.get(NPCInclusions), new Matcher.EDIDContains()));
        npcsToGetPotions.addExclusion(new SelectionSet(thisIni.get(NPCExclusions), new Matcher.EDIDContains()));
        npcsToGetPotions.addExclusion(new SelectionSet(thisIni.get(modExclusions), new Matcher.ModContains()));
        npcsToGetPotions.addExclusion(new SelectionSet(ASIS.getAsisIni().get("MODEXCLUSIONS"), new Matcher.ModContains()));
        this.addRecordSelector("npcsToGetPotions", npcsToGetPotions);
    }

    @Override
    public void runModuleChanges() {
        Mod merger = Controller.getAllRecords();
        Mod patch = SPGlobal.getGlobalPatch();

        String scriptName = "NPCPotions";

        FormID hpotions = null;
        Ini.Section hpPotionSection = thisIni.get(hpPotions);
        for (String e : hpPotionSection.keySet()) {
//            FormID tempHPForm = new FormID(e.getKey(), e.getValue());
            LVLI hpPotionsList = (LVLI) merger.getMajor(e, GRUP_TYPE.LVLI);
            if (hpPotionsList != null) {
                hpotions = hpPotionsList.getForm();
            }
        }
//        FormID hpotions = merger.getMajor("LItemPotionRestoreHealth", GRUP_TYPE.LVLI).getForm();
        FormID mpotions = null;
        Ini.Section mpPotionSection = thisIni.get(mpPotions);
        for (String e : mpPotionSection.keySet()) {
//            FormID tempMPForm = new FormID(e.getKey(), e.getValue());
            LVLI mpPotionsList = (LVLI) merger.getMajor(e, GRUP_TYPE.LVLI);
            if (mpPotionsList != null) {
                mpotions = mpPotionsList.getForm();
            }
        }
//        FormID mpotions = merger.getMajor("LItemPotionRestoreMagicka", GRUP_TYPE.LVLI).getForm();
        FormID spotions = null;
        Ini.Section spPotionSection = thisIni.get(spPotions);
        for (String e : spPotionSection.keySet()) {
//            FormID tempSPForm = new FormID(e.getKey(), e.getValue());
            LVLI spPotionsList = (LVLI) merger.getMajor(e, GRUP_TYPE.LVLI);
            if (spPotionsList != null) {
                spotions = spPotionsList.getForm();
            }
        }
//        FormID spotions = merger.getMajor("LItemPotionRestoreStamina", GRUP_TYPE.LVLI).getForm();
        if (hpotions == null || mpotions == null || spotions == null) {
            String error = "The following leveled list(s) for potions was not found: ";
            if (hpotions == null) {
                error = error + " Health Potions";
            }
            if (mpotions == null) {
                error = error + " Magicka Potions";
            }
            if (spotions == null) {
                error = error + " Stamina Potions";
            }
            System.err.println(error);
            SPGlobal.logError("NPC Potions Error", error);
            SPGlobal.flush();
            JOptionPane.showMessageDialog(null, "Something terrible happened running " + name
                    + ": " + error);
            System.exit(0);
        }

        int numItems = ASIS.save.getInt(ASISSaveFile.GUISettings.NUMBER_OF_POTIONS);
        int numChance = ASIS.save.getInt(ASISSaveFile.GUISettings.CHANCE_PER_POTION);

        GLOB NPCPotionsnumItemsGLOB = (GLOB) merger.getMajor("ASISNPCPotionsnumItems", GRUP_TYPE.GLOB);
        NPCPotionsnumItemsGLOB.setValue((float) numItems);
        SPGlobal.getGlobalPatch().addRecord(NPCPotionsnumItemsGLOB);
        FormID NPCPotionsnumItems = NPCPotionsnumItemsGLOB.getForm();
        GLOB NPCPotionsnumChanceGLOB = (GLOB) merger.getMajor("ASISNPCPotionsnumChance", GRUP_TYPE.GLOB);
        NPCPotionsnumChanceGLOB.setValue((float) numChance);
        SPGlobal.getGlobalPatch().addRecord(NPCPotionsnumChanceGLOB);
        FormID NPCPotionsnumChance = NPCPotionsnumChanceGLOB.getForm();

        SPProgressBarPlug.setStatus("NPC Potions: Adding Potions");
        SPProgressBarPlug.incrementBar();
        for (MajorRecord r : getRecordSelector("npcsToGetPotions").getValidRecords()) {
            NPC_ n = (NPC_) r;
            if (n != null) {
                ScriptRef script = new ScriptRef(scriptName);
                script.setProperty("useNPCPotions", true);
                script.setProperty("ASISNPCPotionsnumItems", NPCPotionsnumItems);
                script.setProperty("ASISNPCPotionsnumChance", NPCPotionsnumChance);
                script.setProperty("potionsList1", hpotions);
                if (n.getEDID().toUpperCase().contains("MISSILE") || n.getEDID().toUpperCase().contains("MELEE")) {
                    script.setProperty("potionsList2", spotions);
                } else {
                    script.setProperty("potionsList2", mpotions);
                }
                n.getScriptPackage().addScript(script);

                ASIS.npcsToWrite.add(n.getForm());
                patch.addRecord(n);
            }
        }
    }

}
