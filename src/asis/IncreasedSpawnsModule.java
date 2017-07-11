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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JOptionPane;
import org.ini4j.Config;
import org.ini4j.Ini;
import skyproc.*;
import skyproc.gui.*;

/**
 *
 * @author David Tynan
 */
public class IncreasedSpawnsModule extends Framework_Module_Abstract {

    private final int MAX_SPAWNS_DEFAULT = 9;
    private final int REDUCED_SPAWNS_DEFAULT = 1;
    private final int INCREASED_SPAWNS_DEFAULT = 1;

    private final String thisIniName = "IncreasedSpawns.ini";
    private final String patchSettings = "PatchSettings";
    private final String NPCInclusions = "NPCInclusions";
    private final String NPCExclusions = "NPCExclusions";
    private final String NPCMaxSpawnSettings = "NPCMaxSpawnSettings";
    private final String NPCReducedSpawnSettings = "NPCReducedSpawnSettings";
    private final String NPCIncreasedSpawnSettings = "NPCIncreasedSpawnSettings";
    private final String CellExclusions = "CellExclusions";
    private final String FactionExclusions = "FactionExclusions";
    private final String modExclusions = "ModExclusions";
    private final String KeywordExclusions = "KeywordExclusions";
    
    private final Ini thisIni;
    
    public IncreasedSpawnsModule() throws IOException {
        name = "Increased Spawns";
        thisIni = new Ini();
        Config c = thisIni.getConfig();
        c.setEmptyOption(true);
        c.setEmptySection(true);
        thisIni.load(new File(thisIniName));
        
        RecordSelector npcsToGetIncreasedSpawns = new RecordSelectorBuilder().type(GRUP_TYPE.NPC_).RecordValidator(ASIS.validNPCs_NoScripts).build();
        npcsToGetIncreasedSpawns.addInclusion(new SelectionSet(thisIni.get(NPCInclusions), new Matcher.EDIDContains()) );
        npcsToGetIncreasedSpawns.addExclusion(new SelectionSet(thisIni.get(NPCExclusions), new Matcher.EDIDContains()) );
        npcsToGetIncreasedSpawns.addExclusion(new SelectionSet(thisIni.get(modExclusions), new Matcher.ModContains()) );
        npcsToGetIncreasedSpawns.addExclusion(new SelectionSet(ASIS.getAsisIni().get("MODEXCLUSIONS"), new Matcher.ModContains()) );
        npcsToGetIncreasedSpawns.addExclusion(new SelectionSet(thisIni.get(KeywordExclusions), new Matcher.HasKYWD()) );
        
        this.addRecordSelector("npcsToGetIncreasedSpawns", npcsToGetIncreasedSpawns);
    }

    @Override
    public void runModuleChanges() {
        try {
            Mod merger = Controller.getAllRecords();
            Mod patch = SPGlobal.getGlobalPatch();

            float[] spawnweight = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

            spawnweight[0] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN0WEIGHT));
//            spawnweight[0] = ASIS.save.getFloat(ASISSaveFile.GUISettings.ISSPAWN0WEIGHT);
            spawnweight[1] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN1WEIGHT));
            spawnweight[2] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN2WEIGHT));
            spawnweight[3] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN3WEIGHT));
            spawnweight[4] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN4WEIGHT));
            spawnweight[5] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN5WEIGHT));
            spawnweight[6] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN6WEIGHT));
            spawnweight[7] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN7WEIGHT));
            spawnweight[8] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN8WEIGHT));
            spawnweight[9] = Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISSPAWN9WEIGHT));

            String scriptName = "increasedSpawns";

            FLST listOfValidNPCs = new FLST("ASIS_ListOfValidNPCsIncreasedSpawns");
            FLST listOfInvalidCells = new FLST("ASIS_listOfInvalidCellsIncreasedSpawns");
            FormID uniqueFaction = new FormID("000800", SPDatabase.getMod(new ModListing("ASIS-Dependency", false)).getInfo());

            GLOB intSpawnsEnabledGLOB = (GLOB) merger.getMajor("ASISIntSpawns", GRUP_TYPE.GLOB);
            intSpawnsEnabledGLOB.setValue(ASIS.save.getBool(ASISSaveFile.GUISettings.ISUSEINTERIORSPAWNS));
            FormID intSpawnsEnabledForm = intSpawnsEnabledGLOB.getForm();
            patch.addRecord(intSpawnsEnabledGLOB);

            GLOB intSpawnsReducedGLOB = new GLOB("ASISIntSpawnsReduceFactor", GLOB.GLOBType.Float);//(GLOB) merger.getMajor("ASISIntSpawnsReduceFactor", GRUP_TYPE.GLOB);
            intSpawnsReducedGLOB.setValue(Float.parseFloat(ASIS.save.getStr(ASISSaveFile.GUISettings.ISREDUCEDINTERIORSPAWNS)));
            FormID intSpawnsReducedForm = intSpawnsReducedGLOB.getForm();
            patch.addRecord(intSpawnsReducedGLOB);

            FormID[] globals = new FormID[10];
            for (int i = 0; i < 10; i++) {
                GLOB SpawnWeightGLOB = (GLOB) merger.getMajor("ASISSpawnWeight" + i, GRUP_TYPE.GLOB);
                SpawnWeightGLOB.setValue(spawnweight[i]);
                globals[i] = SpawnWeightGLOB.getForm();
                patch.addRecord(SpawnWeightGLOB);
            }

            SPProgressBarPlug.setStatus("Increased Spawns: Adding Spawns/Duplicating NPCs");
            SPProgressBarPlug.incrementBar();

//            Ini tempIni = new Ini();
//            Config c = tempIni.getConfig();
//            c.setEmptyOption(true);
//            c.setEmptySection(true);
//            tempIni.load(new File("IncreasedSpawns.ini"));
            ArrayList<ModListing> plugins = SPImporter.getActiveModList();
            Ini.Section cellExculsionsSection = thisIni.get(CellExclusions);
            for (Entry<String, String> e : cellExculsionsSection.entrySet()) {
                try {
                    ModListing master = new ModListing(e.getValue());
                    boolean active = false;
                    for (ModListing ml : plugins) {
                        if (ml.equals(master)) {
                            active = true;
                            break;
                        }
                    }
                    if (active) {
                        FormID f = new FormID(e.getKey(), e.getValue());
                        listOfInvalidCells.addFormEntry(f);
                    }
                } catch (Exception ex) {
                    String error = "Error reading IncreasedSpawns.ini [CellExclusions]."
                            + " Please make sure the ini file is up to date and correctly formated.";
                    SPGlobal.logError("ASIS Increased Spawns", error);
                    SPGlobal.logError("ASIS Increased Spawns", ex.getMessage());
                    SPGlobal.flush();
                    JOptionPane.showMessageDialog(null, error);
                    System.exit(0);
                }
            }

            Ini.Section factionExculsionsSection = thisIni.get(FactionExclusions);
            Set<FormID> excludeFactionsSet = new HashSet<>();
            for (Entry<String, String> e : factionExculsionsSection.entrySet()) {
                try {
                    FACT fa = (FACT) merger.getMajor(e.getKey(), GRUP_TYPE.FACT);
                    FormID f = fa.getForm();
                    excludeFactionsSet.add(f);
                } catch (NullPointerException ex) {
                    String error = "Error reading IncreasedSpawns.ini [FactionExclusions], " + e.getKey()
                            + " could not be resolved to a faction. Ignoring.";
//                    JOptionPane.showMessageDialog(null, error);
                    SPGlobal.logError("Error - " + name, error);
                }
            }

            Map<String, Integer> maxSpawnSet = new HashMap<>();
            IniSectionToMap(thisIni, NPCMaxSpawnSettings, maxSpawnSet);

            Map<String, Integer> reducedSpawnSet = new HashMap<>();
            IniSectionToMap(thisIni, NPCReducedSpawnSettings, reducedSpawnSet);

            Map<String, Integer> increasedSpawnSet = new HashMap<>();
            IniSectionToMap(thisIni, NPCIncreasedSpawnSettings, increasedSpawnSet);

            // Changed Floats to Doubles
            float maxNumRandom = 0.0f;
            for (float currentNum : spawnweight) {
                maxNumRandom += currentNum;
            }
            FormID maxNumForm = (new GLOB("ASISMaxNum", GLOB.GLOBType.Float, maxNumRandom, true).getForm());

            for (MajorRecord r : getRecordSelector("npcsToGetIncreasedSpawns").getValidRecords()) {
                NPC_ n = (NPC_) r;
                if (n != null) {
                    String edid = n.getEDID();

                    listOfValidNPCs.addFormEntry(n.getForm());

                    String edidLower = edid.toLowerCase();

                    int max = MAX_SPAWNS_DEFAULT;
                    for (Map.Entry<String, Integer> e : maxSpawnSet.entrySet()) {
                        if (edidLower.contains(e.getKey())) {
                            max = e.getValue();
                            break;
                        }
                    }

                    float reduced = REDUCED_SPAWNS_DEFAULT;
                    for (Map.Entry<String, Integer> e : reducedSpawnSet.entrySet()) {
                        if (edidLower.contains(e.getKey())) {
                            reduced = e.getValue();
                            reduced = 1.0f / reduced;
                            break;
                        }
                    }

                    float increased = INCREASED_SPAWNS_DEFAULT;
                    for (Map.Entry<String, Integer> e : increasedSpawnSet.entrySet()) {
                        if (edidLower.contains(e.getKey())) {
                            increased = e.getValue();
                            break;
                        }
                    }

                    FormID maxSpawnsForm = (new GLOB("ASISMaxNumSpawns" + edid, GLOB.GLOBType.Float, max, true)).getForm();
                    float numMult = reduced * increased;
                    FormID multForm = (new GLOB("ASISSpawnMult" + edid, GLOB.GLOBType.Float, numMult, true)).getForm();

                    //Actual script addition is done here.
                    ScriptRef script = new ScriptRef(scriptName);
                    script.setProperty("listOfValidNPCs", listOfValidNPCs.getForm());
                    script.setProperty("listOfValidCells", listOfInvalidCells.getForm());
                    script.setProperty("uniqueSpawnFaction", uniqueFaction);
                    script.setProperty("ASISUseInteriorSpawns", intSpawnsEnabledForm);
                    script.setProperty("isAmbushSpawn", n.getEDID().toUpperCase().contains("AMBUSH"));

                    for (int i = 0; i < 10; i++) {
                        script.setProperty("ASISSpawnWeight" + i, (globals[i]));
                    }
                    script.setProperty("ASISIntSpawnsReduceFactor", intSpawnsReducedForm);
                    script.setProperty("ASISMaxNumSpawns", maxSpawnsForm);
                    script.setProperty("ASISSpawnMult", multForm);
                    script.setProperty("ASISMaxNum", maxNumForm);
                    script.setProperty("SelfForm", n.getForm());
                    script.setProperty("modExcludeFactions", excludeFactionsSet.toArray(new FormID[0]));
                    n.getScriptPackage().addScript(script);

                    ASIS.npcsToWrite.add(n.getForm());
                    patch.addRecord(n);

                }
            }
            //Add the NPC to the patch.
            patch.addRecord(listOfValidNPCs);
        } catch (IOException | NumberFormatException exception) {
            System.err.println(exception.toString());
            SPGlobal.logException(exception);
            SPGlobal.flush();
            JOptionPane.showMessageDialog(null, "Something terrible happened running " + name
                    + ": " + exception + " Check the readme and debug logs.");
            System.exit(0);
        }
    }

    private void IniSectionToMap(Ini theIni, String sectionName, Map<String, Integer> theMap) {
        Ini.Section iniSection = theIni.get(sectionName);
        for (Map.Entry<String, String> e : iniSection.entrySet()) {
            try {
                theMap.put(e.getKey().toLowerCase(), Integer.parseInt(e.getValue()));
            } catch (NumberFormatException ex) {
                String error = "Error reading IncreasedSpawns.ini ["
                        + sectionName + "] entry: " + e.getKey()
                        + ", value after the = is not a number";
                SPGlobal.logError("ASIS Increased Spawns", error);
            }
        }
    }

}
