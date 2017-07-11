/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import asis.ini_asis.INI_asis;
import asis.ini_asis.INI_asis.IniData;
import asis.ini_asis.INI_asis.IniDataFormat;
import asis.ini_asis.INI_asis.IniSectionHead;
import asis.ini_asis.TweakIniList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.swing.JOptionPane;
import skyproc.GMST;
import skyproc.GMST.BoolSetting;
import skyproc.GMST.FloatSetting;
import skyproc.GMST.IntSetting;
import skyproc.GMST.StringSetting;
import skyproc.Mod;
import skyproc.SPGlobal;

/**
 *
 * @author pc tech
 */
public class customizedGMSTs {

    public customizedGMSTs() {
    }

    public void runCustomizedGMSTs(ASIS asis) {

        this.asis = asis;
        tweakInis = this.asis.getTweakIni();


        try {
            /*
             * Create a patch to store data about the load order is NOT exported
             * to an actual patch.
             */
            //Creates a mod to store the data that is to be in the official patch
            Mod officialPatch = SPGlobal.getGlobalPatch();
            // Creates and initializes an INI object to store the INI data
            INI_asis ini = new INI_asis("customizedGMSTs.ini");
            Collection<IniSectionHead> sections = getSectionList();
            ini.addSection(sections);
            ini.readData();

            // Get INI Data that corresponds to the filter map
            Map<IniSectionHead, Collection<IniData>> iniData = ini.getData();

            //Processes the INI Data and makes the correct changes to officialPatch
            processIniData(iniData, officialPatch);

            /*
             * Close up shop.
             */

        } catch (Exception e) {
            // If a major error happens, print it everywhere and display a message box.
            System.out.println(e.toString());
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution.  Check the debug logs and contact the author.");
        }

    }

    private void processIniData(Map<IniSectionHead, Collection<IniData>> iniDataMap, Mod patch) {

        for (IniSectionHead currentIniSection : iniDataMap.keySet()) {
            Collection<IniData> currentIniData = iniDataMap.get(currentIniSection);

            if (currentIniSection.getName().equalsIgnoreCase("GMST")) {
                processGMSTData(currentIniData, patch);
            }
        }

    }// close method processIniData(...)

    //Adds legal GMSTs to the specified patch
    // Assumes that iniSectionMap has been filtered to contain only legal GMSTs
    private void processGMSTData(Collection<IniData> gmstData, Mod patch) {
        
        //Creates an ArrayList to store all of the GMSTs to add.  Although I
        // don't do anything with the ArrayList (all the GMSTs have to do to be
        // added is to be instantiated), it may be useful in the future.
        ArrayList<GMST> gmstToAdd = new ArrayList<>();


        Map<String, String> tweakIniGMSTList = tweakInis.getKeyValueMap().get("CustomizedGMSTs_GMST");
        Collection<String> tweakIniGMSTOmissions = tweakInis.getValueMap().get("CustomizedGMSTs_GMST_Omissions");


        //Iterates through the GMST keys
        for (IniData currentGMST : gmstData) {
            if (!currentGMST.getFormat().equals(IniDataFormat.KEY_VALUE)) {
                continue;
            }
            //Gets the GMST Value for the current key, as a String
            String currentGMSTValue = currentGMST.getValue();
            String currentGMSTName = currentGMST.getKey();

            if (tweakIniGMSTOmissions != null) {
                if (tweakIniGMSTOmissions.contains(currentGMSTName)) {
                    continue;
                }
            }
            if (tweakIniGMSTList != null) {
                if (tweakIniGMSTList.containsKey(currentGMSTName)) {
                    currentGMSTValue = tweakIniGMSTList.get(currentGMSTName);
                }
            }

            //Switches to determine the type of GMST based on starting character
            // Assumes that GMSTs are in Bethesda's form (first char indicates type)
            // Creates a GMST object (which adds it to the patch in the GMST constructor)
            char startingChar = currentGMSTName.charAt(0);
//            if (enableModOverride || !gmstIsChangedInLoadOrder(LegalGMST.valueOf(currentGMST), merger))
//            {
            switch (startingChar) {
                //case Boolean
                case 'b':
                case 'B':
                    gmstToAdd.add(new GMST(BoolSetting.valueOf(currentGMSTName.toString()), Boolean.parseBoolean(currentGMSTValue)));
                    break;
                //case Integer
                case 'i':
                case 'I':
                    gmstToAdd.add(new GMST(IntSetting.valueOf(currentGMSTName.toString()), Integer.parseInt(currentGMSTValue)));
                    break;
                //case String
                case 's':
                case 'S':
                    //case Float
                    gmstToAdd.add(new GMST(StringSetting.valueOf(currentGMSTName.toString()), currentGMSTValue));
                    break;
                case 'f':
                case 'F':
                    gmstToAdd.add(new GMST(FloatSetting.valueOf(currentGMSTName.toString()), Float.parseFloat(currentGMSTValue)));
                    break;
                //Just in case
                default:
                    JOptionPane.showMessageDialog(null, "Invalid GMST:\n" + currentGMSTName.toString() + "\nPlease contact the author.");
            }// Close switch(startingChar)
//            }// Close if
        }// Close for
    }// Close method processGMSTData(...)

    private Collection<IniSectionHead> getSectionList() {
        Collection<IniSectionHead> sectionList = new ArrayList<>();

        for (IniSection currentSection : IniSection.values()) {
            sectionList.add(new IniSectionHead(currentSection.getName(), currentSection.getFormat()));
        }

        return sectionList;
    }
    private ASIS asis;
    private TweakIniList tweakInis;

    //An enum containing the legal HEAD keys for an ini type
    private enum IniSection {

        GMST(IniDataFormat.KEY_VALUE, "GMST"),
        GENERAL_SETTINGS(IniDataFormat.KEY_VALUE, "GENERAL_SETTINGS");
        private IniDataFormat format;
        private String name;

        IniSection(IniDataFormat format, String name) {
            this.format = format;
            this.name = name;
        }

        String getName() {
            return name;
        }

        IniDataFormat getFormat() {
            return format;
        }
    }

    private enum GeneralSetting {

        enableModOverride;
    }
}
