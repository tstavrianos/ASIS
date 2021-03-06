/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asis.gui;

import asis.ASIS;
import lev.gui.LCheckBox;
import lev.gui.LTextPane;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;

/**
 *
 * @author pc tech
 */
public class SettingsAutomaticPerks extends SPSettingPanel {

    LCheckBox automaticPerks;

    public SettingsAutomaticPerks(SPMainMenuPanel parent_) {
        super(parent_, "Automatic Perks", ASIS.blue);
    }

    @Override
    protected void initialize() {
        super.initialize();
        
        LTextPane introText = new LTextPane(settingsPanel.getWidth() - 40, 200, ASIS.settingsColor);
        introText.setText("Configure using AutomaticPerks.ini");
        introText.setEditable(false);
        introText.setFont(ASIS.settingsFont);
        introText.setCentered();
        setPlacement(introText);
        Add(introText);

        /*
         * automaticPerks = new LCheckBox("Automatic Perks", ASIS.settingsFont,
         * ASIS.blue); automaticPerks.setOffset(2); automaticPerks.addShadow();
         * setPlacement(automaticPerks); AddSetting(automaticPerks);
         */
        alignRight();
    }
}