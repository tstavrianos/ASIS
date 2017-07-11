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
public class SettingsCustomizedAI extends SPSettingPanel {

    LCheckBox customizedAI;

    public SettingsCustomizedAI(SPMainMenuPanel parent_) {
        super(parent_, "Customized AI", ASIS.blue);
    }

    @Override
    protected void initialize() {
        super.initialize();

        LTextPane introText = new LTextPane(settingsPanel.getWidth() - 40, 200, ASIS.settingsColor);
        introText.setText("Configure using customizedAI.ini\n\n"
                + "This module is deprecated. It is suggested you use another mod to change AI values.");
        introText.setEditable(false);
        introText.setFont(ASIS.settingsFont);
        introText.setCentered();
        setPlacement(introText);
        Add(introText);
        
        /*
         * customizedAI = new LCheckBox("Customized AI", ASIS.settingsFont,
         * ASIS.blue); customizedAI.setOffset(2); customizedAI.addShadow();
         * setPlacement(customizedAI); AddSetting(customizedAI);
         */
        alignRight();
    }
}