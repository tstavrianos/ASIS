/*
 * To change this template, choose Tools | Templates
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
public class RecordSelector {

    public static final RecordSource GLOBAL = new RecordSource() {

        @Override
        public Set<MajorRecord> getRecords(GRUP_TYPE type) {
            return new LinkedHashSet<>(Controller.getAllRecords().getGRUPs().get(type).getRecords());
        }
    };
    public static final RecordValidator USE_ALL = new RecordValidator() {
        @Override
        public boolean useDeleted() {
            return true;
        }

        @Override
        public boolean isRejectedTemplate(MajorRecord rec) {
            return false;
        }
    };
    public static final RecordValidator USE_NON_DELETED = new RecordValidator() {
        @Override
        public boolean useDeleted() {
            return false;
        }

        @Override
        public boolean isRejectedTemplate(MajorRecord rec) {
            return false;
        }
    };

    private final GRUP_TYPE type;
    private final Set<SelectionSet> inclusionsSet;
    private final Set<SelectionSet> exclusionsSet;
    private RecordSource recordSource;
    private RecordValidator theRecordValidator;
    private final Set<MajorRecord> validRecords;

    private RecordSelector() {
        this.type = null;
        inclusionsSet = null;
        exclusionsSet = null;
        validRecords = null;
    }
    
    public RecordSelector(RecordSelector rec){
        this.type = rec.getGRUP();
        this.inclusionsSet = new LinkedHashSet<>(rec.getInclusionsSet());
        this.exclusionsSet = new LinkedHashSet<>(rec.getExclusionsSet());
        this.recordSource = rec.getRecordSource();
        this.theRecordValidator = rec.getInputHandler();
        this.validRecords = new LinkedHashSet<>(rec.getValidRecords());
    }

    RecordSelector(GRUP_TYPE type, Set<SelectionSet> inclusionsSet,
            Set<SelectionSet> exclusionsSet, RecordSource recordSource,
            RecordValidator theRecordValidator, Set<MajorRecord> validRecords) {
        if (type == null) {
            throw new NullPointerException("A selection must have a GRUP type.");
        }
        this.type = type;
        this.inclusionsSet = new LinkedHashSet<>(inclusionsSet);
        this.exclusionsSet = new LinkedHashSet<>(exclusionsSet);
        this.recordSource = recordSource;
        this.theRecordValidator = theRecordValidator;
        this.validRecords = new LinkedHashSet<>(validRecords);
    }

    public GRUP_TYPE getGRUP() {
        return type;
    }

    public Set<MajorRecord> getValidRecords() {
        return validRecords;
    }

    public RecordValidator getInputHandler() {
        return theRecordValidator;
    }

    public void setRecordValidator(RecordValidator theRecordValidator) {
        this.theRecordValidator = theRecordValidator;
    }

    public void addInclusion(SelectionSet inc) {
        inclusionsSet.add(inc);
    }

    public void removeInclusion(SelectionSet inc) {
        inclusionsSet.remove(inc);
    }

    public void addExclusion(SelectionSet exc) {
        exclusionsSet.add(exc);
    }

    public void removeExclusion(SelectionSet exc) {
        exclusionsSet.remove(exc);
    }

    public Set<SelectionSet> getInclusionsSet() {
        return new LinkedHashSet<>(inclusionsSet);
    }

    public Set<SelectionSet> getExclusionsSet() {
        return new LinkedHashSet<>(exclusionsSet);
    }

    public RecordSource getRecordSource() {
        return recordSource;
    }

    public void setRecordSource(RecordSource theSource) {
        this.recordSource = theSource;
    }


    public void validate(MajorRecord rec) {
        boolean include = false;

        if (!(theRecordValidator.useDeleted()) && (rec.get(MajorRecord.MajorFlags.Deleted))) {
            return;
        }
        if (theRecordValidator.isRejectedTemplate(rec)) {
            return;
        }

        for (SelectionSet p : getInclusionsSet()) {
            include = p.isMatch(rec);
            if (include) {
                validRecords.add(rec);
                break;
            }
        }

        if (include) {
            for (SelectionSet p : getExclusionsSet()) {
                
                if (p.isMatch(rec)) {
                    validRecords.remove(rec);
                    return;
                }
            }

        }

    }
}
