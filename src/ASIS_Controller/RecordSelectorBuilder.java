/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ASIS_Controller;

import java.util.LinkedHashSet;
import java.util.Set;
import skyproc.*;

/**
 *
 * @author David
 */
public class RecordSelectorBuilder {
    private GRUP_TYPE type;
    private Set<SelectionSet> inclusionsSet = new LinkedHashSet<>();
    private Set<SelectionSet> exclusionsSet = new LinkedHashSet<>();
    private RecordSource recSource = RecordSelector.GLOBAL;
    private RecordValidator theRecordValidator = RecordSelector.USE_ALL;
    private Set<MajorRecord> validRecords = new LinkedHashSet<>(0);
    
    public RecordSelectorBuilder() {}
    
    public RecordSelector build() {
        return new RecordSelector(type, inclusionsSet, exclusionsSet, recSource, theRecordValidator, validRecords);
    }
    
    public RecordSelectorBuilder type(GRUP_TYPE g){
        this.type = g;
        return this;
    }
    
    public RecordSelectorBuilder inclusionsSet(Set<SelectionSet> iS){
        this.inclusionsSet = iS;
        return this;
    }
    
    public RecordSelectorBuilder exclusionsSet(Set<SelectionSet> eS){
        this.exclusionsSet = eS;
        return this;
    }
    
    public RecordSelectorBuilder RecordSource(RecordSource rS){
        this.recSource = rS;
        return this;
    }
    
    public RecordSelectorBuilder RecordValidator(RecordValidator rV){
        this.theRecordValidator = rV;
        return this;
    }
    
    public RecordSelectorBuilder ValidRecord(Set<MajorRecord> rV){
        this.validRecords = rV;
        return this;
    }
    
}