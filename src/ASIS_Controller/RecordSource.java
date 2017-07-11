/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ASIS_Controller;

import java.util.Set;
import skyproc.*;

/**
 *
 * @author David Tynan
 */
public interface RecordSource {
    public Set<MajorRecord> getRecords(GRUP_TYPE type);
}
