/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASIS_Controller;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import skyproc.*;
import skyproc.gui.*;

/**
 *
 * @author David Tynan
 */
public class Controller {

    public Controller() {
        AllRecords.addAsOverrides(SPGlobal.getDB());
    }

    static Map<RecordSelector, Set<MajorRecord>> validRecordsMap = new HashMap<>();
    static Set<Framework_Module> modules = new LinkedHashSet<>();
    
    private static final Mod AllRecords = new Mod("Controller_temp", false);

    public static Mod getAllRecords() {
        return AllRecords;
    }

    public void addModule(Framework_Module m) {
        modules.add(m);
    }

    public void removeModule(Framework_Module m) {
        modules.remove(m);
    }

    public void runAllModule() {
        for (Framework_Module m : modules) {
            runModule(m);
        }
    }

    public void runModule(Framework_Module toRun) {
        SPProgressBarPlug.setStatus(toRun.getName() + ": Starting Patch");
        SPProgressBarPlug.incrementBar();

        processInput(toRun);
        setValidRecords(toRun);
        toRun.runModuleChanges();
        AllRecords.addAsOverrides(SPGlobal.getGlobalPatch());
    }

    protected void processInput(Framework_Module runningModule) {
        for (RecordSelector selector : runningModule.getRecordSelectors()) {
            for (SelectionSet inclusions : selector.getInclusionsSet()) {
                inclusions.setup();
            }
            for (SelectionSet exclusions : selector.getExclusionsSet()) {
                exclusions.setup();
            }
        }

    }

    protected void setValidRecords(Framework_Module runningModule) {
        for (RecordSelector selector : runningModule.getRecordSelectors()) {
            Set<MajorRecord> sourceRecords = selector.getRecordSource().getRecords(selector.getGRUP());
            
            for (MajorRecord r : sourceRecords) {
                selector.validate(r);
            }
        }
    }

}
