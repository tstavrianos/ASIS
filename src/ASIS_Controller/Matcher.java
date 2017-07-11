/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASIS_Controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ini4j.Ini;
import org.ini4j.Profile;
import skyproc.*;

/**
 *
 * @author David Tynan
 */
public abstract class Matcher {

    abstract void setup(Ini.Section section);

    abstract boolean isMatch(MajorRecord rec, Ini.Section section);

    public static class MatchAll extends Matcher {

        @Override
        void setup(Profile.Section section) {
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            return true;
        }

    }

    public static class EDIDContains extends Matcher {

        public EDIDContains() {
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            if (section != null) {
                String EDID = rec.getEDID().toUpperCase();
                Set<String> keys = section.keySet();
                if (keys != null) {
                    for (String s : keys) {
                        String upper = s.toUpperCase();
                        if (EDID.contains(upper)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        void setup(Profile.Section section) {

        }

    }

    public static class EDIDContainsOrBlank extends EDIDContains {

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            if ((section == null) || (section.isEmpty())) {
                return true;
            }
            return super.isMatch(rec, section);
        }

        public EDIDContainsOrBlank() {
        }

    }

    public static class EDIDStartsWith extends Matcher {

        public EDIDStartsWith() {
        }

        @Override
        void setup(Profile.Section section) {
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            if (section != null) {
                String EDID = rec.getEDID().toUpperCase();
                Set<String> keys = section.keySet();
                if (keys != null) {
                    for (String s : section.keySet()) {
                        String upper = s.toUpperCase();
                        if (EDID.startsWith(upper)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

    }

    public static class EDIDEquals extends Matcher {

        public EDIDEquals() {
        }

        @Override
        void setup(Profile.Section section) {
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            if (section != null) {
                String EDID = rec.getEDID().toUpperCase();
                Set<String> keys = section.keySet();
                if (keys != null) {
                    for (String s : section.keySet()) {
                        String upper = s.toUpperCase();
                        if (EDID.equalsIgnoreCase(upper)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

    }

    public static class ModContains extends Matcher {

        Set<MajorRecord> containedRecords = new HashSet<>();

        public ModContains() {
        }

        @Override
        void setup(Profile.Section section) {
            if (section != null) {
                Set<String> keys = section.keySet();
                if (keys != null) {
                    for (String s : keys) {
                        Mod tempMod = SPDatabase.getMod(new ModListing(s));
                        if (tempMod != null) {
                            containedRecords.addAll(tempMod.getRecords());
                        }
                    }
                }
            }
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            return containedRecords.contains(rec);
        }

    }
    
    public static class NotModContains extends Matcher {

        Set<MajorRecord> containedRecords = new HashSet<>();

        public NotModContains() {
        }

        @Override
        void setup(Profile.Section section) {
            if (section != null) {
                Set<String> keys = section.keySet();
                if (keys != null) {
                    for (String s : keys) {
                        Mod tempMod = SPDatabase.getMod(new ModListing(s));
                        if (tempMod != null) {
                            containedRecords.addAll(tempMod.getRecords());
                        }
                    }
                }
            }
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            return !containedRecords.contains(rec);
        }

    }

    public static class HasKYWD extends Matcher {

        private final Mod keywords;
        HashSet<String> keyHashes = new HashSet<>();

        public HasKYWD() {
            keywords = Controller.getAllRecords();
        }

        @Override
        void setup(Profile.Section section) {
            if (section != null) {
                Set<String> keys = section.keySet();
                if (keys != null) {
                    for (String s : section.keySet()) {
                        String upper = s.toUpperCase();
                        keyHashes.add(upper);
                    }
                }
            }
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            NPC_ n = (NPC_) rec;
            if (n != null) {
                KeywordSet keys = n.getKeywordSet();
                for (FormID f : keys.getKeywordRefs()) {
                    KYWD key = (KYWD) keywords.getMajor(f, GRUP_TYPE.KYWD);
                    if (key == null) {
                        SPGlobal.logError("Matcher Error", n + " has keyword entry with FormID: " + f + " that cannot be resolved to a KYWD.");
                    } else {
                        String upper = key.getEDID().toUpperCase();
                        if (keyHashes.contains(upper)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            MGEF m = (MGEF) rec;
            if (m != null) {
                KeywordSet keys = m.getKeywordSet();
                for (FormID f : keys.getKeywordRefs()) {
                    KYWD key = (KYWD) keywords.getMajor(f, GRUP_TYPE.KYWD);
                    if (key == null) {
                        SPGlobal.logError("Matcher Error", n + " has keyword entry with FormID: " + f + " that cannot be resolved to a KYWD.");
                    } else {
                        String upper = key.getEDID().toUpperCase();
                        if (keyHashes.contains(upper)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class HasFaction extends Matcher {

        private final HashSet<FormID> factions = new HashSet<>();

        public HasFaction() {
        }

        @Override
        void setup(Profile.Section section) {
            if (section != null) {
                Set<Map.Entry<String, String>> entrySet = section.entrySet();
                if (entrySet != null) {
                    for (Map.Entry<String, String> ent : entrySet) {
                        FormID form = new FormID(ent.getKey(), ent.getValue());
                        factions.add(form);
                    }
                }
            }
        }

        @Override
        boolean isMatch(MajorRecord rec, Profile.Section section) {
            NPC_ n = (NPC_) rec;
            if (n != null) {
                ArrayList<SubFormInt> factionsSub = n.getFactions();
                for (SubFormInt sub : factionsSub) {
                    FormID f = sub.getForm();
                    if (factions.contains(f)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }
}
