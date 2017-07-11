/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ASIS_Controller;

import skyproc.*;

/**
 *
 * @author David Tynan
 */
public interface RecordValidator {
    public boolean useDeleted();
    public boolean isRejectedTemplate(MajorRecord rec);
    
}
