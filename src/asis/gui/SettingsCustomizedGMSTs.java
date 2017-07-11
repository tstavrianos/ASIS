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
public class SettingsCustomizedGMSTs extends SPSettingPanel {

    LCheckBox customizedGMSTs;

    public SettingsCustomizedGMSTs(SPMainMenuPanel parent_) {
        super(parent_, "Customized GMST's", ASIS.blue);
    }

    @Override
    protected void initialize() {
        super.initialize();

        LTextPane introText = new LTextPane(settingsPanel.getWidth() - 40, 200, ASIS.settingsColor);
        introText.setText("Configure using customizedGMSTs.ini\n\n"
                + "This module is deprecated. It is suggested you use another mod to change GMSTs.");
        introText.setEditable(false);
        introText.setFont(ASIS.settingsFont);
        introText.setCentered();
        setPlacement(introText);
        Add(introText);
        
        /*
         * customizedGMSTs = new LCheckBox("Customized GMST's",
         * ASIS.settingsFont, ASIS.blue); customizedGMSTs.setOffset(2);
         * customizedGMSTs.addShadow(); setPlacement(customizedGMSTs);
         * AddSetting(customizedGMSTs);
         */
        alignRight();
    }
}