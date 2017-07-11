/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import lev.gui.LCheckBox;
import lev.gui.LComboBox;
import skyproc.SPGlobal;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;
import skyproc.gui.SUMGUI;

/**
 *
 * @author Justin Swanson
 */
public class OtherSettingsPanel extends SPSettingPanel {

    LCheckBox noBOSS;

    public OtherSettingsPanel(SPMainMenuPanel parent_) {
	super(parent_, "Other Settings", ASIS.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	noBOSS = new LCheckBox("Skip BOSS", ASIS.settingsFont, ASIS.settingsColor);
	noBOSS.setOffset(2);
	noBOSS.addShadow();
	setPlacement(noBOSS);
	AddSetting(noBOSS);

	alignRight();

    }
}
