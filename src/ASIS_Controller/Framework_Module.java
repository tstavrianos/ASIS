/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ASIS_Controller;

import java.util.Set;

/**
 *
 * @author David Tynan
 */
public interface Framework_Module {
    
    public String getName();

    public Set<RecordSelector> getRecordSelectors();
    
    public RecordSelector getRecordSelector(String name);
    
    public void addRecordSelector(String name, RecordSelector recordSelector);

    public void runModuleChanges();
}
