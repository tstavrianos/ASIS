/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package asis;

import ASIS_Controller.Controller;
import skyproc.*;

/**
 *
 * @author David Tynan
 */
public class UnTemplate {
    
    public UnTemplate() {
        Mod merger = Controller.getAllRecords();
        Mod patch = SPGlobal.getGlobalPatch();
        
        for (NPC_ n : merger.getNPCs()){
            if (!n.get(MajorRecord.MajorFlags.Deleted)){
                if (n.isTemplated() && n.get(NPC_.TemplateFlag.USE_SCRIPTS)){
                    FormID tForm = n.getTemplate();
                    LVLN lnpc = (LVLN) merger.getMajor(tForm, GRUP_TYPE.LVLN);
                    if (lnpc != null) {
                        n.set(NPC_.TemplateFlag.USE_SCRIPTS, false);
                        patch.addRecord(n);
                    }
                }
            }
        }
    }
}
