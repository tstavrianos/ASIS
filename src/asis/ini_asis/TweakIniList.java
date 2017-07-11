package asis.ini_asis;

import asis.ini_asis.INI_asis.IniData;
import asis.ini_asis.INI_asis.IniDataFormat;
import asis.ini_asis.INI_asis.IniSectionHead;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.JOptionPane;
import lev.Ln;
import skyproc.ModListing;
import skyproc.SPGlobal;

/**
 *
 * @author NathanT
 */
public class TweakIniList {

    public TweakIniList() {
        File tweakDirectory = new File(tweakIniDirectory);

        filesInDirectory = tweakDirectory.listFiles();
        loadFileNameCollection();

        setLoadExclusionsFile();
        readLoadExclusions();

        setLoadOrderFile();
        readLoadOrder();

        addNewFilesToLoadOrder();
        readLoadOrder();

        loadTweakInis();

        compileIniData();

        loadIniMaps();
    }

    public Map<IniSectionHead, Collection<IniData>> getData() {
        return data;
    }

    private Map<String, Map<String, String>> keyValueData() {
        Map<String, Map<String, String>> dataToReturn = new TreeMap<String, Map<String, String>>();

        for (IniSectionHead currentSection : data.keySet()) {
            if (currentSection.getFormat().equals(IniDataFormat.KEY_VALUE)) {
                dataToReturn.put(currentSection.getName(), new TreeMap<String, String>());
                Map<String, String> currentDataMap = dataToReturn.get(currentSection.getName());

                for (IniData currentData : data.get(currentSection)) {
                    currentDataMap.put(currentData.getKey(), currentData.getValue());
                }
            }
        }

        return dataToReturn;
    }

    private Map<String, Collection<String>> valueData() {
        Map<String, Collection<String>> dataToReturn = new TreeMap<String, Collection<String>>();

        for (IniSectionHead currentSection : data.keySet()) {
            if (currentSection.getFormat().equals(IniDataFormat.VALUE)) {
                dataToReturn.put(currentSection.getName(), new ArrayList<String>());
                Collection<String> currentDataCollection = dataToReturn.get(currentSection.getName());

                for (IniData currentData : data.get(currentSection)) {
                    currentDataCollection.add(currentData.getValue());
                }
            }
        }

        return dataToReturn;
    }

    public Map<String, Map<String, String>> getKeyValueMap() {
        return keyValueData;
    }

    public Map<String, Collection<String>> getValueMap() {
        return valueData;
    }

    public Map<String, String> getMap(IniSectionHead sectionToChoose) {
        if (!sectionToChoose.getFormat().equals(IniDataFormat.KEY_VALUE)) {
            return null;
        }

        String name = sectionToChoose.getName();

        return keyValueData.get(name);
    }

    public Collection<String> getCollection(IniSectionHead sectionToChoose) {
        if (!sectionToChoose.getFormat().equals(IniDataFormat.VALUE)) {
            return null;
        }

        String name = sectionToChoose.getName();

        return valueData.get(name);
    }

    /*
     * Prerequisites: - fileInDirectory not be null
     *
     * Returns: - Nothing, but it sets the state variable loadOrderFile to point
     * towards the correct file.
     */
    private void setLoadOrderFile() {
        if (!existsLoadOrderFile()) {
            loadOrderFile = new File(tweakIniDirectory + "\\TweakIniLoadOrder.txt");
            populateDefaultLoadOrderFile();
        } else {
            loadOrderFile = new File(tweakIniDirectory + "\\TweakIniLoadOrder.txt");
        }
    }

    private void setLoadExclusionsFile() {
        if (!existsLoadExclusionsFile()) {
            loadExclusionsFile = new File(tweakIniDirectory + "\\TweakIniLoadExclusions.txt");
            populateDefaultLoadExclusionsFile();
        } else {
            loadExclusionsFile = new File(tweakIniDirectory + "\\TweakIniLoadExclusions.txt");
        }
    }

    private boolean existsLoadOrderFile() {
        boolean isFile = false;

        for (File currentFile : filesInDirectory) {
            if (currentFile.getName().equalsIgnoreCase("TweakIniLoadOrder.txt")) {
                isFile = true;
            }
        }

        return isFile;
    }

    private boolean existsLoadExclusionsFile() {
        boolean isFile = false;

        for (File currentFile : filesInDirectory) {
            if (currentFile.getName().equalsIgnoreCase("TweakIniLoadExclusions.txt")) {
                isFile = true;
            }
        }

        return isFile;
    }

    private void populateDefaultLoadOrderFile() {
        try {
            FileWriter loadOrderFileWriter = new FileWriter(loadOrderFile);
            PrintWriter loadOrderPrinter = new PrintWriter(loadOrderFileWriter);

            loadOrderPrinter.println(defaultLoadOrderHeader);
            loadOrderPrinter.println("");
            loadOrderPrinter.println("");

            for (File currentFile : filesInDirectory) {
                String name = currentFile.getName();

                if (name.substring(name.length() - 4).equalsIgnoreCase(".ini")) {
                    loadOrderPrinter.println(name);
                }
            }

            loadOrderPrinter.close();
            loadOrderFileWriter.close();
        } catch (IOException e) {
            display("There has been a file I/O error while writing to the load order file.  Please contact the author.");
        }
    }

    private void populateDefaultLoadExclusionsFile() {
        try {
            FileWriter loadExclusionsFileWriter = new FileWriter(loadExclusionsFile);
            PrintWriter loadExclusionsPrinter = new PrintWriter(loadExclusionsFileWriter);

            loadExclusionsPrinter.println(defaultLoadExclusionsHeader);
            loadExclusionsPrinter.println("");
            loadExclusionsPrinter.println("");

            loadExclusionsPrinter.close();
            loadExclusionsFileWriter.close();
        } catch (IOException e) {
            display("There has been a file I/O error while writing to the load exclusions file.  Please contact the author.");
        }
    }

    private void readLoadOrder() {
        loadOrder.clear();

        try {
            Scanner loadOrderReader = new Scanner(loadOrderFile);

            while (loadOrderReader.hasNext()) {
                String currentLine = loadOrderReader.nextLine();
                currentLine = Ln.cleanLine(currentLine, commentDelimiter);

                if (fileNames.contains(currentLine) && !loadExclusions.contains(currentLine)) {
                    loadOrder.add(currentLine);
                }
            }
        } catch (IOException e) {
            display("There was an error while reading the load order. Please contact the author.");
        }
    }

    private void readLoadExclusions() {
        try {
            Scanner loadExclusionsReader = new Scanner(loadExclusionsFile);

            while (loadExclusionsReader.hasNext()) {
                String currentLine = loadExclusionsReader.nextLine();
                currentLine = Ln.cleanLine(currentLine, commentDelimiter);

                if (fileNames.contains(currentLine)) {
                    loadExclusions.add(currentLine);
                }
            }
        } catch (IOException e) {
            display("There was an error while reading the load order. Please contact the author.");
        }
    }

    private void addNewFilesToLoadOrder() {
        for (File currentFile : filesInDirectory) {
            String name = currentFile.getName();

            boolean isIni = name.substring(name.length() - 4).equalsIgnoreCase(".ini");
            boolean isExcluded = loadExclusions.contains(name);
            boolean isInLoadOrder = loadOrder.contains(name);

            if (isIni && !(isExcluded || isInLoadOrder)) {
                appendToLoadOrderFile(name);
            }
        }
    }

    private void appendToLoadOrderFile(String name) {
        try {
            FileWriter appendingWriter = new FileWriter(loadOrderFile, true);
            PrintWriter appendPrinter = new PrintWriter(appendingWriter);

            appendPrinter.println(name);

            appendPrinter.close();
            appendingWriter.close();
        } catch (IOException e) {
            display("There was an I/O error while appending new files to the load order.  Please contact the author.");
        }
    }

    private void compileIniData() {
        for (INI_asis currentINI : tweakInis) {
            if (isValidToLoad(currentINI)) {
                addToData(currentINI);
            }
        }
    }

    private void addToData(INI_asis ini) {
        Map<IniSectionHead, Collection<IniData>> currentData = ini.getData();
        for (IniSectionHead currentSection : currentData.keySet()) {
            if (!data.containsKey(currentSection)) {
                data.put(currentSection, currentData.get(currentSection));
            } else {
                Collection<IniData> newData = currentData.get(currentSection);
                Collection<IniData> oldData = data.get(currentSection);

                for (IniData currentNewEntry : newData) {
                    if (currentNewEntry.getFormat().equals(IniDataFormat.VALUE)) {
                        boolean alreadyInList = false;
                        String value = currentNewEntry.getValue();

                        for (IniData currentOldEntry : oldData) {
                            if (currentOldEntry.getValue().equals(value)) {
                                alreadyInList = true;
                            }
                        }

                        if (!alreadyInList) {
                            oldData.add(currentNewEntry);
                        }
                    } else if (currentNewEntry.getFormat().equals(IniDataFormat.KEY_VALUE)) {
                        String key = currentNewEntry.getKey();

                        Collection<IniData> oldDataToRemove = new ArrayList<IniData>();
                        Collection<IniData> newDataToAdd = new ArrayList<IniData>();

                        for (IniData currentOldEntry : oldData) {

                            if (currentOldEntry.getKey().equals(key)) {
                                oldDataToRemove.add(currentOldEntry);
                                newDataToAdd.add(currentNewEntry);
                            }
                        }

                        for (IniData dataToRemove : oldDataToRemove) {
                            oldData.remove(dataToRemove);
                        }

                        for (IniData dataToAdd : newDataToAdd) {
                            oldData.add(dataToAdd);
                        }
                    }
                }
            }
        }
    }

    private void loadTweakInis() {
        initializeTweakIniSections();

        for (String currentIniName : loadOrder) {
            tweakIniFiles.add(new File(tweakIniDirectory + "\\" + currentIniName));
        }

        try {
            for (File currentFile : tweakIniFiles) {
                tweakInis.add(new INI_asis(currentFile.getAbsolutePath()));
            }

            for (INI_asis currentINI : tweakInis) {
                currentINI.addSection(tweakIniSections);
                currentINI.readData();
            }
        } catch (IOException e) {
            display("There was an I/O Error while reading the Tweak Inis.  Please contact the author.");
        }
    }

    private void loadFileNameCollection() {
        for (File currentFile : filesInDirectory) {
            fileNames.add(currentFile.getName());
        }
    }

    private void initializeTweakIniSections() {
        for (TweakIniSectionHead currentHead : TweakIniSectionHead.values()) {
            tweakIniSections.add(new IniSectionHead(currentHead.getName(), currentHead.getFormat()));
        }
    }

    private boolean isValidToLoad(INI_asis ini) {
        boolean isValid = false;
        boolean listsPlugin = false;
        Map<IniSectionHead, Collection<IniData>> iniData = ini.getData();

        String plugin = null;

        for (IniSectionHead currentSection : iniData.keySet()) {

            if (currentSection.getName().equalsIgnoreCase("GeneralSettings")) {
                for (IniData currentData : iniData.get(currentSection)) {
                    if (currentData.getKey().equalsIgnoreCase("plugin")) {
                        plugin = currentData.getValue();
                        listsPlugin = true;
                    }
                }
            }
        }

        if (!listsPlugin) {
            return true;
        }


        if (plugin != null && SPGlobal.getDB().hasMod(new ModListing(plugin))) {
            isValid = true;
        }


        return isValid;
    }

    private void loadIniMaps() {
        keyValueData = keyValueData();
        valueData = valueData();
    }

    private void displayData() {
        for (IniSectionHead currentSection : data.keySet()) {
            String displayString = "[" + currentSection.getName() + "]\n";

            for (IniData currentData : data.get(currentSection)) {
                if (currentData.getFormat().equals(IniDataFormat.KEY_VALUE)) {
                    displayString += currentData.getKey() + "=" + currentData.getValue() + "\n";
                } else if (currentData.getFormat().equals(IniDataFormat.VALUE)) {
                    displayString += currentData.getValue() + "\n";
                }
            }

            display(displayString);
        }
    }

    private void printIniValues(INI_asis ini) {
        Map<IniSectionHead, Collection<IniData>> iniData = ini.getData();

        for (IniSectionHead currentSection : iniData.keySet()) {
            String displayString = "[" + currentSection.getName() + "]\n";

            for (IniData currentData : iniData.get(currentSection)) {
                if (currentData.getFormat().equals(IniDataFormat.KEY_VALUE)) {
                    displayString += currentData.getKey() + "=" + currentData.getValue() + "\n";
                } else if (currentData.getFormat().equals(IniDataFormat.VALUE)) {
                    displayString += currentData.getValue() + "\n";
                }
            }

            display(displayString);
        }
    }

    private void display(Object o) {
        JOptionPane.showMessageDialog(null, o.toString());
    }
    private File[] filesInDirectory;
    private File loadOrderFile;
    private File loadExclusionsFile;
    private ArrayList<File> tweakIniFiles = new ArrayList<File>();
    private ArrayList<INI_asis> tweakInis = new ArrayList<INI_asis>();
    private Collection<String> fileNames = new ArrayList<String>();
    private ArrayList<String> loadOrder = new ArrayList<String>();
    private ArrayList<String> loadExclusions = new ArrayList<String>();
    private Collection<IniSectionHead> tweakIniSections = new ArrayList<IniSectionHead>();
    private Map<IniSectionHead, Collection<IniData>> data = new TreeMap<IniSectionHead, Collection<IniData>>();
    private Map<String, Map<String, String>> keyValueData = null;
    private Map<String, Collection<String>> valueData = null;
    private String tweakIniDirectory = "TweakInis";
    private String commentDelimiter = ";";

    public enum TweakIniSectionHead {

        GeneralSettings(IniDataFormat.KEY_VALUE, "GeneralSettings"),
        ASIS_Language(IniDataFormat.VALUE, "ASIS_Language"),
        ASIS_ModExclusions(IniDataFormat.VALUE, "ASIS_ModExclusions"),
        ASIS_ModExclusions_Omissions(IniDataFormat.VALUE, "ASIS_ModExclusions_Omissions"),
        IncreasedSpawns_PatchSettings(IniDataFormat.KEY_VALUE, "IncreasedSpawns_PatchSettings"),
        IncreasedSpawns_NPCInclusions(IniDataFormat.VALUE, "IncreasedSpawns_NPCInclusions"),
        IncreasedSpawns_NPCInclusions_Omissions(IniDataFormat.VALUE, "IncreasedSpawns_NPCInclusions_Omissions"),
        IncreasedSpawns_NPCExclusions(IniDataFormat.VALUE, "IncreasedSpawns_NPCExclusions"),
        IncreasedSpawns_NPCExclusions_Omissions(IniDataFormat.VALUE, "IncreasedSpawns_NPCExclusions_Omissions"),
        IncreasedSpawns_NPCReducedSpawnSettings(IniDataFormat.KEY_VALUE, "IncreasedSpawns_NPCReducedSpawnSettings"),
        IncreasedSpawns_NPCReducedSpawnSettings_Omissions(IniDataFormat.VALUE, "IncreasedSpawns_NPCReducedSpawnSettings_Omissions"),
        IncreasedSpawns_NPCMaxSpawnSettings(IniDataFormat.KEY_VALUE, "IncreasedSpawns_NPCMaxSpawnSettings"),
        IncreasedSpawns_NPCMaxSpawnSettings_Omissions(IniDataFormat.VALUE, "IncreasedSpawns_NPCMaxSpawnSettings_Omissions"),
        IncreasedSpawns_CellExclusions(IniDataFormat.VALUE, "IncreasedSpawns_CellExclusions"),
        IncreasedSpawns_CellExclusions_Omissions(IniDataFormat.VALUE, "IncreasedSpawns_CellExclusions_Omissions"),
        IncreasedSpawns_ModExclusions(IniDataFormat.VALUE, "IncreasedSpawns_ModExclusions"),
        IncreasedSpawns_ModExcusions_Omissions(IniDataFormat.VALUE, "IncreasedSpawns_ModExclusions_Omissions"),
        AutomaticSpells_SpellSettings(IniDataFormat.KEY_VALUE, "AutomaticSpells_SpellSettings"),
        AutomaticSpells_NPCInclusions(IniDataFormat.VALUE, "AutomaticSpells_NPCInclusions"),
        AutomaticSpells_NPCInclusions_Omissions(IniDataFormat.VALUE, "AutomaticSpells_NPCInclusions_Omissions"),
        AutomaticSpells_NPCExclusions(IniDataFormat.VALUE, "AutomaticSpells_NPCExclusions"),
        AutomaticSpells_NPCExclusions_Omissions(IniDataFormat.VALUE, "AutomaticSpells_NPCExclusions_Omissions"),
        AutomaticSpells_SpellInclusions(IniDataFormat.VALUE, "AutomaticSpells_SpellInclusions"),
        AutomaticSpells_SpellInclusions_Omissions(IniDataFormat.VALUE, "AutomaticSpells_SpellInclusions_Omissions"),
        AutomaticSpells_SpellExclusionsContains(IniDataFormat.VALUE, "AutomaticSpells_SpellExclusionsContains"),
        AutomaticSpells_SpellExclusionsContains_Omissions(IniDataFormat.VALUE, "AutomaticSpells_SpellExclusionsContains_Omissions"),
        AutomaticSpells_SpellExclusionsStartsWith(IniDataFormat.VALUE, "AutomaticSpells_SpellExclusionsStartsWith"),
        AutomaticSpells_SpellExclusionsStartsWith_Omissions(IniDataFormat.VALUE, "AutomaticSpells_SpellExclusionsStartsWith_Omissions"),
        AutomaticSpells_SpellModExclusions(IniDataFormat.VALUE, "AutomaticSpells_SpellModExclusions"),
        AutomaticSpells_SpellModExclusions_Omissions(IniDataFormat.VALUE, "AutomaticSpells_SpellModExclusions_Omissions"),
        AutomaticSpells_NPCModExclusions(IniDataFormat.VALUE, "AutomaticSpells_NPCModExclusions"),
        AutomaticSpells_NPCModExclusions_Omissions(IniDataFormat.VALUE, "AutomaticSpells_NPCModExclusions_Omissions"),
        NPCPotions_PatchSettings(IniDataFormat.KEY_VALUE, "NPCPotions_PatchSettings"),
        NPCPotions_NPCInclusions(IniDataFormat.VALUE, "NPCPotions_NPCInclusions"),
        NPCPotions_NPCInclusions_Omissions(IniDataFormat.VALUE, "NPCPotions_NPCInclusions_Omissions"),
        NPCPotions_NPCExclusions(IniDataFormat.VALUE, "NPCPotions_NPCExclusions"),
        NPCPotions_NPCExclusions_Omissions(IniDataFormat.VALUE, "NPCPotions_NPCExclusions_Omissions"),
        NPCPotions_ModExclusions(IniDataFormat.VALUE, "NPCPotions_ModExclusions"),
        NPCPotions_ModExclusions_Omissions(IniDataFormat.VALUE, "NPCPotions_ModExclusions_Omissions"),
        CustomizdAI_GMST(IniDataFormat.KEY_VALUE, "CustomizedAI_GMST"),
        CustomizedAI_GMST_Omissions(IniDataFormat.VALUE, "CustomizedAI_GMST_Omissions"),
        AutomaticPerks_NPCInclusions(IniDataFormat.VALUE, "AutomaticPerks_NPCInclusions"),
        AutomaticPerks_NPCInclusions_Omissions(IniDataFormat.VALUE, "AutomaticPerks_NPCInclusions_Omissions"),
        AutomaticPerks_NPCExclusions(IniDataFormat.VALUE, "AutomaticPerks_NPCExclusions"),
        AutomaticPerks_NPCExclusions_Omissions(IniDataFormat.VALUE, "AutomaticPerks_NPCExclusions_Omissions"),
        AutomaticPerks_PerkInclusions(IniDataFormat.VALUE, "AutomaticPerks_PerkInclusions"),
        AutomaticPerks_PerkInclusions_Omissions(IniDataFormat.VALUE, "AutomaticPerks_PerkInclusions_Omissions"),
        AutomaticPerks_PerkExclusionsContains(IniDataFormat.VALUE, "AutomaticPerks_PerkExclusionsContains"),
        AutomaticPerks_PerkExclusionsContains_Omissions(IniDataFormat.VALUE, "AutomaticPerks_PerkExclusionsContains_Omissions"),
        AutomaticPerks_PerkExclusionsStartsWith(IniDataFormat.VALUE, "AutomaticPerks_PerkExclusionsStartsWith"),
        AutomaticPerks_ModExclusions(IniDataFormat.VALUE, "AutomaticPerks_ModExclusions"),
        AutomaticPerks_ModExclusions_Omissions(IniDataFormat.VALUE, "AutomaticPerks_ModExclusions_Omissions");
        private INI_asis.IniDataFormat format;
        private String name;

        TweakIniSectionHead(INI_asis.IniDataFormat format, String name) {
            this.format = format;
            this.name = name;
        }

        String getName() {
            return name;
        }

        INI_asis.IniDataFormat getFormat() {
            return format;
        }
    }
    private final String defaultLoadOrderHeader = ";This is a list of Tweak Inis that will be loaded.";
    private final String defaultLoadExclusionsHeader = ";This is a list of Tweak Inis to be ignored while building ASIS.esp.";
}
