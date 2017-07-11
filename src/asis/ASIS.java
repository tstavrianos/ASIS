/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import ASIS_Controller.*;
import asis.ASISSaveFile.GUISettings;
import asis.gui.*;
import asis.ini_asis.TweakIniList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.*;
import javax.sound.sampled.LineEvent.Type;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lev.gui.LSaveFile;
import org.ini4j.Config;
import org.ini4j.Ini;
import skyproc.*;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPProgressBarPlug;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;

/**
 *
 * @author pc tech
 */
public class ASIS implements SUM {

    public static String ASISlanguage;
    static ArrayList<String> modExclusions = new ArrayList<>(0);
    public static final RecordValidator validNPCs_NoScripts = new RecordValidator() {
        @Override
        public boolean useDeleted() {
            return false;
        }

        @Override
        public boolean isRejectedTemplate(MajorRecord rec) {
            NPC_ n = (NPC_) rec;
            if (n != null) {
                return n.isTemplated() && n.get(NPC_.TemplateFlag.USE_SCRIPTS);
            }
            return true;
        }
    };
    public static final RecordValidator validNPCs_NoSpellsPerks = new RecordValidator() {
        @Override
        public boolean useDeleted() {
            return false;
        }

        @Override
        public boolean isRejectedTemplate(MajorRecord rec) {
            NPC_ n = (NPC_) rec;
            if (n != null) {
                return n.isTemplated() && n.get(NPC_.TemplateFlag.USE_SPELL_LIST);
            }
            return true;
        }
    };
    Map settingsMap;
    public static boolean debug;
    //Stores the memory for the program, read from the ini and then restarts the program.
    public static int memory;
    private String finishedSound;
    //Stores the save file to store user settings on the disk.
    public static LSaveFile save = new ASISSaveFile();
    //Colors used in the GUI.
    static public Color blue = new Color(0, 147, 196);
    static public Color green = new Color(67, 162, 10);
    static public Color darkGreen = new Color(61, 128, 21);
    static public Color orange = new Color(247, 163, 52);
    static public Color yellow = new Color(255, 204, 26);
    static public Color lightGray = new Color(190, 190, 190);
    static public Color darkGray = new Color(110, 110, 110);
    public static Color headerColor = new Color(66, 181, 184);  // Teal
    public static Color settingsColor = new Color(72, 179, 58);  // Green
    //Fonts used in the GUI.
    static public Font settingsFont = new Font("Serif", Font.BOLD, 14);
    static public Font settingsFontSmall = new Font("Serif", Font.BOLD, 12);
    //The Main Menu panel for the background.
    static public SPMainMenuPanel settingsMenu;
    //All the individual panels for editing settings.
    //static public Settings settingsPanel;
    static public SettingsIncreasedSpawns settingsIncreasedSpawns;
    static public SettingsSpawnRandomizer settingsSpawnRandomizer;
    static public SettingsNPCPotions settingsNPCPotions;
    static public SettingsAutomaticPerks settingsAutomaticPerks;
    static public SettingsAutomaticSpells settingsAutomaticSpells;
    static public SettingsCustomizedAI settingsCustomizedAI;
    static public SettingsCustomizedGMSTs settingsCustomizedGMSTs;
    static public SettingsNPCEnchantFix settingsNPCEnchantFix;
    static public OtherSettingsPanel otherSettingsPanel;
    // Version
    public static final String version = "1.42.5";
    //Number of progress steps.
    final static int numSteps = 30;
    static int steps = 0;
    //Tweak INI's
    public TweakIniList tweakInis;

    // records changed by modules
    static final Set<FormID> npcsToWrite = new HashSet<>();
    static Ini asisIni;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {// throws IOException, Exception {
        //ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        //ASIS main = new ASIS();
        //main.processINI();

        /*if (!arguments.contains("-nonew") && memory != 0) {
         * 
         * NiftyFunc.allocateMoreMemory("100m", memory + "m", "ASIS.jar", "-nonew");
         * }
         *SPGlobal.setGlobalPatch(main.getExportPatch());
         */
        //save.init();
        try {
            SPGlobal.createGlobalLog();

//            List<String> temp = Arrays.asList(args);
//            if (!temp.contains("-NOBOSS")) {
//                List<String> newList = new ArrayList<>(temp);
//                newList.add("-NOBOSS");
//                args = newList.toArray(new String[0]);
//            }
            SUMGUI.open(new ASIS(), args);
        } catch (Exception e) {
            // If a major error happens, print it everywhere and display a message box.
            System.err.println(e.toString());
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
            SPGlobal.closeDebug();
        }

    }

    @Override
    public String description() {
        return "The patcher for ASIS.";
    }

    @Override
    public void runChangesToPatch() throws Exception {
        SPGlobal.loggingSync(true);
        SPGlobal.logging(true);
//        SPGlobal.getGlobalPatch().setFlag(Mod.Mod_Flags.STRING_TABLED, false);

        SPProgressBarPlug.setMax(numSteps);
        SPProgressBarPlug.setStatus("Initializing ASIS");

        asis();
    }

    public void asis() throws Exception {
        //SPProgressBarPlug.setStatus("Starting Import Process");
        //SPGUI.progress.pause(true);

        if (asis.ASIS.debug) {
            setSkyProcGlobalDebug();
        }

        //Mod patch = runModSetup();
        getTweakIniSetup();

        runClasses();

        SPProgressBarPlug.incrementBar();
    }

    public ArrayList<String> getModExclusions() {
        return modExclusions;
    }

    public TweakIniList getTweakIni() {
        return tweakInis;
    }

    private void setSkyProcGlobalDebug() {
//        SPGlobal.createGlobalLog();
//        SPGlobal.debugModMerge = true;
    }

    private void runClasses() throws IOException {
        Controller asisController = new Controller();
        UnTemplate unTemplateNpcs = new UnTemplate();

        Set<FormID> unTemplated = new HashSet<>();
        Mod patch = SPGlobal.getGlobalPatch();
        for (NPC_ n : patch.getNPCs()) {
            unTemplated.add(n.getForm());
        }

        if (save.getBool(GUISettings.AUTOMATICSPELLS_ON)) {
            SPProgressBarPlug.setStatus("Processing Automatic Spells");
            AutomaticSpellsModule spells = new AutomaticSpellsModule();
            asisController.runModule(spells);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.INCREASEDSPAWNS_ON)) {
            SPProgressBarPlug.setStatus("Processing Increased Spawns");
            IncreasedSpawnsModule increasedSpawns = new IncreasedSpawnsModule();
            asisController.runModule(increasedSpawns);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.NPCPOTIONS_ON)) {
            SPProgressBarPlug.setStatus("Processing NPCPotions");
            ASISnpcPotionsModule npcPotions = new ASISnpcPotionsModule();
            asisController.runModule(npcPotions);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.CUSTOMIZEDAI_ON)) {
            SPProgressBarPlug.setStatus("Processing Customized AI");
            CustomizedAI myCustomizedAI = new CustomizedAI();
            myCustomizedAI.runCustomizedAI(this);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.AUTOMATICPERKS_ON)) {
            SPProgressBarPlug.setStatus("Processing Automatic Perks");
            AutomaticPerksModule perks = new AutomaticPerksModule();
            asisController.runModule(perks);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.SPAWNRANDOMIZER_ON)) {
            SPProgressBarPlug.setStatus("Processing Spawn Randomizer");
            RandomSpawnsModule randomSpawns = new RandomSpawnsModule();
            asisController.runModule(randomSpawns);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.CUSTOMIZEDGMSTS_ON)) {
            SPProgressBarPlug.setStatus("Processing Customized GMSTs");
            customizedGMSTs customGMSTs = new customizedGMSTs();
            customGMSTs.runCustomizedGMSTs(this);
        }
        SPProgressBarPlug.incrementBar();

        if (save.getBool(GUISettings.NPCENCHANTFIX_ON)) {
            SPProgressBarPlug.setStatus("Processing NPC Enchantment Fix");
            npcEnchantModule npcEnchantFix = new npcEnchantModule();
            asisController.runModule(npcEnchantFix);
        }
        SPProgressBarPlug.incrementBar();

        unTemplated.removeAll(npcsToWrite);
        for (FormID f : unTemplated) {
            patch.remove(f);
        }

    }

    private void getTweakIniSetup() {
        try {
            tweakInis = new TweakIniList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There was an error with Tweak Inis.  Please contact the author.");
        }
    }

    public String getLanguage() {
        return ASISlanguage;
    }

    @Override
    public String getName() {
        return "ASIS";
    }

    @Override
    public GRUP_TYPE[] importRequests() {
        return new GRUP_TYPE[]{
            GRUP_TYPE.NPC_,
            GRUP_TYPE.SPEL,
            GRUP_TYPE.MGEF,
            GRUP_TYPE.PERK,
            GRUP_TYPE.LVLI,
            GRUP_TYPE.LVLN,
            GRUP_TYPE.FACT,
            GRUP_TYPE.AVIF,
            GRUP_TYPE.GLOB,
            GRUP_TYPE.KYWD
        };
    }

    @Override
    public boolean importAtStart() {
        return false;
    }

    @Override
    public boolean hasStandardMenu() {
        return true;
    }

    @Override
    public SPMainMenuPanel getStandardMenu() {
        settingsMenu = new SPMainMenuPanel(blue);
        settingsMenu.setVersion(version, new Point(80, 88));
        SUMGUI.helpPanel.setHeaderFont(new Font("Serif", Font.BOLD, 18));

        settingsIncreasedSpawns = new SettingsIncreasedSpawns(settingsMenu);
        settingsMenu.addMenu(settingsIncreasedSpawns, true, save, GUISettings.INCREASEDSPAWNS_ON);

        settingsAutomaticPerks = new SettingsAutomaticPerks(settingsMenu);
        settingsMenu.addMenu(settingsAutomaticPerks, true, save, GUISettings.AUTOMATICPERKS_ON);

        settingsAutomaticSpells = new SettingsAutomaticSpells(settingsMenu);
        settingsMenu.addMenu(settingsAutomaticSpells, true, save, GUISettings.AUTOMATICSPELLS_ON);

        settingsCustomizedAI = new SettingsCustomizedAI(settingsMenu);
        settingsMenu.addMenu(settingsCustomizedAI, true, save, GUISettings.CUSTOMIZEDAI_ON);

        settingsCustomizedGMSTs = new SettingsCustomizedGMSTs(settingsMenu);
        settingsMenu.addMenu(settingsCustomizedGMSTs, true, save, GUISettings.CUSTOMIZEDGMSTS_ON);

        settingsNPCPotions = new SettingsNPCPotions(settingsMenu);
        settingsMenu.addMenu(settingsNPCPotions, true, save, GUISettings.NPCPOTIONS_ON);

        settingsSpawnRandomizer = new SettingsSpawnRandomizer(settingsMenu);
        settingsMenu.addMenu(settingsSpawnRandomizer, true, save, GUISettings.SPAWNRANDOMIZER_ON);

        settingsNPCEnchantFix = new SettingsNPCEnchantFix(settingsMenu);
        settingsMenu.addMenu(settingsNPCEnchantFix, true, save, GUISettings.NPCENCHANTFIX_ON);

//        otherSettingsPanel = new OtherSettingsPanel(settingsMenu); 
//        settingsMenu.addMenu(otherSettingsPanel, false, save, GUISettings.OTHER_SETTINGS);
        return settingsMenu;
    }

    @Override
    public boolean hasCustomMenu() {
        return false;
    }

    @Override
    public boolean hasLogo() {
        return false;
    }

    @Override
    public URL getLogo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasSave() {
        return true;
    }

    @Override
    public LSaveFile getSave() {
        return save;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public ModListing getListing() {
        return new ModListing("ASIS", false);
    }

    @Override
    public Mod getExportPatch() {
        Mod patch = new Mod(getListing());
        patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
        patch.setAuthor("Pluto and Dres Croffgrin");
        return patch;
    }

    @Override
    public Color getHeaderColor() {
        return blue;
    }

    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JFrame openCustomMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean needsPatching() {
        return true;
    }

    @Override
    public void onStart() throws Exception {
        processINI();
        SPGlobal.Language lang;
        try {
            lang = SPGlobal.Language.valueOf(ASISlanguage);
        } catch (EnumConstantNotPresentException e) {
            lang = SPGlobal.Language.English;
        }
        SPGlobal.language = lang;
    }

    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
        if ( (finishedSound != null) && !finishedSound.isEmpty()){
            try {
                File soundFile = new File(finishedSound);
                playClip(soundFile);
            } catch (IOException | InterruptedException | LineUnavailableException | UnsupportedAudioFileException ex){
                SPGlobal.log("OnExit", "Could not play sound: " + ex);
            }
        }
        SPGlobal.logMain("EXIT", "Closing ASIS.");
    }

    @Override
    public ArrayList<ModListing> requiredMods() {
        return new ArrayList<>(Arrays.asList(new ModListing("ASIS-Dependency.esp")));
    }

    enum INItypes {

        LANGUAGE,
        MODEXCLUSIONS
    }

    private void processINI() throws IOException {
        SPProgressBarPlug.setStatus("ASIS: Processing INI");
        SPProgressBarPlug.incrementBar();
        //Sets up the file reader for the ini file.
        asisIni = new Ini();
        Config c = asisIni.getConfig();
        c.setEmptyOption(true);
        c.setEmptySection(true);
        c.setEscape(false);
        asisIni.load(new File("ASIS.ini"));

        Ini.Section lang = asisIni.get("LANGUAGE");
        try {
            Set<String> langKeys = lang.keySet();
            ASISlanguage = langKeys.iterator().next();
        } catch (Exception e) {
            ASISlanguage = "English";
        }

        Ini.Section exclusions = asisIni.get("ModExclusions");
        modExclusions.addAll(exclusions.keySet());

        Ini.Section settings = asisIni.get("Settings");
        try {
            debug = Boolean.parseBoolean(settings.get("debug"));
        } catch (Exception e) {
            debug = false;
        }
        try {
            memory = Integer.parseInt(settings.get("memory"));
        } catch (NumberFormatException numberFormatException) {
            memory = 1024;
        }
        
        try {
            finishedSound = settings.get("finishedSound");
        } catch (Exception e) {
            finishedSound = "";
        }

    }

    public static Ini getAsisIni() {
        return asisIni;
    }

    private static void playClip(File clipFile) throws IOException,
            UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        class AudioListener implements LineListener {

            private boolean done = false;

            @Override
            public synchronized void update(LineEvent event) {
                Type eventType = event.getType();
                if (eventType == Type.STOP || eventType == Type.CLOSE) {
                    done = true;
                    notifyAll();
                }
            }

            public synchronized void waitUntilDone() throws InterruptedException {
                while (!done) {
                    wait();
                }
            }
        }
        AudioListener listener = new AudioListener();
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile)) {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(listener);
            clip.open(audioInputStream);
            try {
                clip.start();
                listener.waitUntilDone();
            } finally {
                clip.close();
            }
        }
    }
}
