/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import java.util.ArrayList;
import skyproc.ModListing;
import skyproc.NPC_;

/**
 *
 * @author pc tech
 */
public class RandomActor {

    private NPC_ NPC;
    private ArrayList<Integer> groups;

    /**
     * @param args
     */
    public RandomActor(NPC_ n, ArrayList<Integer> grouplist) {

        this.NPC = n;
        this.groups = grouplist;
    }

    public String getEDID() {
        return NPC.getEDID();
    }

    public ModListing getModMaster() {
        return NPC.getFormMaster();
    }

    public ArrayList<Integer> getGroupList() {
        return groups;
    }

    public String getFormStr() {
        return NPC.getFormStr();
    }

    public NPC_ getNPC() {
        return NPC;
    }
}
