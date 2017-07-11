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
public class SettingsAutomaticSpells extends SPSettingPanel {

    LCheckBox automaticSpells;

    public SettingsAutomaticSpells(SPMainMenuPanel parent_) {
        super(parent_, "Automatic Spells", ASIS.blue);
    }

    @Override
    protected void initialize() {
        super.initialize();

        LTextPane introText = new LTextPane(settingsPanel.getWidth() - 40, 200, ASIS.settingsColor);
        introText.setText("Configure using AutomaticSpells.ini");
        introText.setEditable(false);
        introText.setFont(ASIS.settingsFont);
        introText.setCentered();
        setPlacement(introText);
        Add(introText);
        
        /*
         * automaticSpells = new LCheckBox("Automatic Spells",
         * ASIS.settingsFont, ASIS.blue); automaticSpells.setOffset(2);
         * automaticSpells.addShadow(); setPlacement(automaticSpells);
         * AddSetting(automaticSpells);
         */
        alignRight();
    }
}