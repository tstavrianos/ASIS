/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asis;

import ASIS_Controller.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.JOptionPane;
import org.ini4j.Config;
import org.ini4j.Ini;
import skyproc.*;
import skyproc.genenums.ActorValue;
import skyproc.genenums.Skill;
import skyproc.gui.*;

/**
 *
 * @author David Tynan
 */
public class AutomaticSpellsModule extends Framework_Module_Abstract {

    private final String thisIniName = "AutomaticSpells.ini";
    private final Ini thisIni;

    private final String patchSettings = "PatchSettings";
    private final String NPCInclusions = "NPCInclusions";
    private final String NPCExclusions = "NPCExclusions";
    private final String KeywordExclusions = "NPCKeywordExclusions";
    private final String NPCModExclusions = "NPCModExclusions";
    private final String effectKYWDPrefixes = "EffectKeywordPrefixes";

    private final String SPELLEXCLUSIONSCONTAINS = "SPELLEXCLUSIONSCONTAINS";
    private final String SPELLEXCLUSIONSSTARTSWITH = "SPELLEXCLUSIONSSTARTSWITH";

    private final String spellModInclusions = "spellModInclusions";

    private final Set<String> effectPrefixes = new HashSet<>();

    public AutomaticSpellsModule() throws IOException {
        name = "Automatic Spells";
        thisIni = new Ini();
        Config c = thisIni.getConfig();
        c.setEmptyOption(true);
        c.setEmptySection(true);
        thisIni.load(new File(thisIniName));

        effectPrefixes.addAll(thisIni.get(effectKYWDPrefixes).keySet());

        RecordSelector npcsToGetSpells = new RecordSelectorBuilder().type(GRUP_TYPE.NPC_).RecordValidator(ASIS.validNPCs_NoSpellsPerks).build();
        npcsToGetSpells.addInclusion(new SelectionSet(thisIni.get(NPCInclusions), new Matcher.EDIDStartsWith()));
        npcsToGetSpells.addExclusion(new SelectionSet(thisIni.get(NPCExclusions), new Matcher.EDIDContains()));
        npcsToGetSpells.addExclusion(new SelectionSet(thisIni.get(NPCModExclusions), new Matcher.ModContains()));
        npcsToGetSpells.addExclusion(new SelectionSet(ASIS.getAsisIni().get("MODEXCLUSIONS"), new Matcher.ModContains()));
        npcsToGetSpells.addExclusion(new SelectionSet(thisIni.get(KeywordExclusions), new Matcher.HasKYWD()));
        this.addRecordSelector("npcsToGetSpells", npcsToGetSpells);

        RecordSelector spellsToDistribute = new RecordSelectorBuilder().type(GRUP_TYPE.SPEL).RecordValidator(RecordSelector.USE_NON_DELETED).build();
        spellsToDistribute.addInclusion(new SelectionSet(thisIni.get(spellModInclusions), new Matcher.ModContains()));
        spellsToDistribute.addExclusion(new SelectionSet(thisIni.get(SPELLEXCLUSIONSCONTAINS), new Matcher.EDIDContains()));
        spellsToDistribute.addExclusion(new SelectionSet(thisIni.get(SPELLEXCLUSIONSSTARTSWITH), new Matcher.EDIDStartsWith()));
        spellsToDistribute.addExclusion(new SelectionSet(ASIS.getAsisIni().get("MODEXCLUSIONS"), new Matcher.ModContains()));
        this.addRecordSelector("spellsToDistribute", spellsToDistribute);

    }

    private Mod merger;

    @Override
    public void runModuleChanges() {
        merger = Controller.getAllRecords();
        Map<SPEL, SpellInfo> spellsMap = getSpellInfos();
        Map<NPC_, NPCInfo> npcsMap = buildNPCInfos();
        addSpellsToNPCs(spellsMap, npcsMap);
    }

    private Map<SPEL, SpellInfo> getSpellInfos() {
        SPProgressBarPlug.setStatus("Automatic Spells: Mapping Spells");
        SPProgressBarPlug.incrementBar();

        Map<SPEL, SpellInfo> spellMap = new HashMap<>();

        for (MajorRecord r : getRecordSelector("spellsToDistribute").getValidRecords()) {
            SPEL s = (SPEL) r;
            if (s != null) {

                SpellInfo spellInf = getSpellInfo(s);
                if (spellInf != null) {
                    spellMap.put(s, spellInf);
                }

            }
        }
        return spellMap;
    }

    private void addSpellsToNPCs(Map<SPEL, SpellInfo> spellsMap, Map<NPC_, NPCInfo> npcsMap) {
        try {
            Mod patch = SPGlobal.getGlobalPatch();

            for (MajorRecord r : getRecordSelector("npcsToGetSpells").getValidRecords()) {
                NPC_ n = (NPC_) r;
                if (n == null) {
                    continue;
                }
                NPCInfo theNpcsInfo = npcsMap.get(n);
                if (theNpcsInfo == null) {
                    continue;
                }
                boolean changed = false;

                ArrayList<FormID> npcSpellsForms = n.getSpells();
                ArrayList<SPEL> npcSpells = new ArrayList<>();
                for (FormID f : npcSpellsForms) {
                    SPEL s = (SPEL) merger.getMajor(f, GRUP_TYPE.SPEL);
                    if (s != null) {
                        npcSpells.add(s);
                    }
                    // disabled warning because of unhandled SHOU
//                        else {
//                            SPGlobal.logError(name, "NPC: " + n + ", has spell FormID entry: " + f
//                                    + ", which cannot be resolved to a Spell\n"
//                                    + "Skipping for patch but this is probably a very bad thing");
//                        }
                }

                for (Entry<SPEL, SpellInfo> e : spellsMap.entrySet()) {
                    if (!npcSpells.contains(e.getKey()) && npcCanGet(theNpcsInfo, e.getValue())) {
                        n.addSpell(e.getKey().getForm());
                        changed = true;
                    }
                }
                if (changed) {
                    ASIS.npcsToWrite.add(n.getForm());
                    patch.addRecord(n);
                }

            }
        } catch (Exception exception) {
            System.err.println(exception.toString());
            SPGlobal.logException(exception);
            SPGlobal.flush();
            JOptionPane.showMessageDialog(null, "Something terrible happened running " + name
                    + ": " + exception + " Check the readme and debug logs.");
            System.exit(0);
        }
    }

    private Map<NPC_, NPCInfo> buildNPCInfos() {
        Map<NPC_, NPCInfo> npcMap = new HashMap<>();

        for (MajorRecord r : getRecordSelector("npcsToGetSpells").getValidRecords()) {
            NPC_ n = (NPC_) r;
            if (n != null) {
                NPCInfo theInfo = getNPCInfo(n);
                if (theInfo != null) {
                    npcMap.put(n, theInfo);
                }
            }

        }

        return npcMap;
    }

    private boolean npcCanGet(NPCInfo nInfo, SpellInfo spInfo) {
        Map<Skill, Integer> skillLevels = nInfo.getSkillLevels();

        for (Entry<Skill, Integer> e : spInfo.getNeededSkills().entrySet()) {
            if (skillLevels.get(e.getKey()) < e.getValue()) {
                return false;
            }
        }

        ArrayList<KYWD> magicKeys = new ArrayList<>();
        for (FormID keyForm : spInfo.getMainEffect().getKeywordSet().getKeywordRefs()) {
            KYWD key = (KYWD) merger.getMajor(keyForm, GRUP_TYPE.KYWD);
            if (key != null) {
                String edid = key.getEDID().toUpperCase();
                for (String prefix : effectPrefixes) {
                    if (edid.startsWith(prefix.toUpperCase())) {
                        magicKeys.add(key);
                    }
                }
            }
        }
        Set<KYWD> effects = nInfo.getHandEffects().get(spInfo.getEquipslot());
        if (effects != null && magicKeys.size() > 0) {
            for (KYWD key : magicKeys) {
                if (effects.contains(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    private NPC_ unTemplate(NPC_ n, NPC_.TemplateFlag templateFlag) {
        while (n.isTemplated() && n.get(templateFlag)) {
            NPC_ nTemp = (NPC_) merger.getMajor(n.getTemplate(), GRUP_TYPE.NPC_);
            if (nTemp == null) {
                LVLN lTemp = (LVLN) merger.getMajor(n.getTemplate(), GRUP_TYPE.LVLN);
                if (lTemp == null) {
                    SPGlobal.logError(name, "NPC: " + n
                            + ", has template FormID entry: " + n.getTemplate()
                            + ", which cannot be resolved to an NPC or Leveled NPC.\n"
                            + "Skipping for patch but this is probably a very bad thing");
                }
                n = null;
                break;
            }
            n = nTemp;
        }
        return n;
    }

    private SpellInfo getSpellInfo(SPEL s) {

        Skill curSkill = null;
        int curCost = -1;
        MGEF curEffect = null;
        Map<Skill, Integer> neededSkills = new HashMap();

        for (MagicEffectRef ref : s.getMagicEffects()) {
            FormID refID = ref.getMagicRef();
            MGEF effect = (MGEF) merger.getMajor(refID, GRUP_TYPE.MGEF);
            if (effect != null) {
                ActorValue av = effect.getSkillType();
                Skill sk = AutomaticPerksModule.getAVasSkill(av);
                if ((sk != null) && (isMagicSkill(sk))) {
                    if (!neededSkills.containsKey(sk)) {
                        neededSkills.put(sk, effect.getSkillLevel());
                    } else if (effect.getSkillLevel() > neededSkills.get(sk)) {
                        neededSkills.put(sk, effect.getSkillLevel());
                    }

                    float mag = ref.getMagnitude();
                    if (mag < 1.0) {
                        mag = (float) 1.0;
                    }
                    int dur = ref.getDuration();
                    if (dur == 0) {
                        dur = 10;
                    }

                    double cost = effect.getBaseCost() * Math.pow((mag * dur / 10), 1.1);
                    int iCost = (int) Math.floor(cost);
                    if (iCost > curCost) {
                        curSkill = sk;
                        curCost = iCost;
                        curEffect = effect;
                    }
                }
            }
        }
        SpellInfo info = null;
        if (curSkill != null) {
            info = new SpellInfo(s, curSkill, curEffect, neededSkills);
        }
        return info;
    }

    private NPCInfo getNPCInfo(NPC_ n) {

        NPC_ unTemplatedSpells = unTemplate(n, NPC_.TemplateFlag.USE_SPELL_LIST);
        NPC_ unTemplatedStats = unTemplate(n, NPC_.TemplateFlag.USE_STATS);

        if ((unTemplatedSpells == null) || (unTemplatedStats == null)) {
            return null;
        }

        NPCInfo theInfo = new NPCInfo();

        // get effects per equipSlot
        ArrayList<FormID> spells = unTemplatedSpells.getSpells();
        Map<SPEL, MGEF> effectsMap = new HashMap<>();

        for (FormID f : spells) {
            SPEL theSpell = (SPEL) merger.getMajor(f, GRUP_TYPE.SPEL);
            if (theSpell != null) {
                int curCost = -1;
                MGEF mainEffect = null;

                for (MagicEffectRef ref : theSpell.getMagicEffects()) {
                    FormID refID = ref.getMagicRef();
                    MGEF effect = (MGEF) merger.getMajor(refID, GRUP_TYPE.MGEF);
                    if ((effect != null) && (effect.getBaseCost() > 0.0)) {
                        float mag = ref.getMagnitude();
                        if (mag < 1.0) {
                            mag = (float) 1.0;
                        }
                        int dur = ref.getDuration();
                        if (dur == 0) {
                            dur = 10;
                        }

                        double cost = effect.getBaseCost() * Math.pow((mag * dur / 10), 1.1);
                        int iCost = (int) Math.floor(cost);
                        if (iCost > curCost) {
                            curCost = iCost;
                            mainEffect = effect;
                        }

                    }
                }
                if (mainEffect != null) {
                    effectsMap.put(theSpell, mainEffect);
                }
            }
        }
        for (Entry<SPEL, MGEF> entry : effectsMap.entrySet()) {
            FormID equipSlot = entry.getKey().getEquipSlot();
            for (FormID f : entry.getValue().getKeywordSet().getKeywordRefs()) {
                KYWD key = (KYWD) merger.getMajor(f, GRUP_TYPE.KYWD);
                if (key == null) {
                    String error = "Magic Effect " + entry.getValue().getEDID()
                            + " has a keyword reference " + f + " that cannot be resolved to a loaded keyword. Skipping.";
                    SPGlobal.log(name, error);
                } else {
                    String edid = key.getEDID().toUpperCase();
                    for (String prefix : effectPrefixes) {
                        if (edid.startsWith(prefix.toUpperCase())) {
                            theInfo.addEquipslotEffect(equipSlot, key);
                        }
                    }
                }
            }
        }

        // get skills
        ArrayList<Skill> skills = new ArrayList<>();
        skills.add(Skill.ALTERATION);
        skills.add(Skill.CONJURATION);
        skills.add(Skill.DESTRUCTION);
        skills.add(Skill.ILLUSION);
        skills.add(Skill.RESTORATION);
        for (Skill s : skills) {
            theInfo.addSkillLevel(s, unTemplatedStats.get(s));
        }

        return theInfo;
    }

    private boolean isMagicSkill(Skill sk) {
        switch (sk) {
            case ALTERATION:
            case CONJURATION:
            case DESTRUCTION:
            case ILLUSION:
            case RESTORATION:
                return true;
        }
        return false;

    }

    private class NPCInfo {

        private final Map<FormID, Set<KYWD>> handEffects;
        private final Map<Skill, Integer> skillLevels;

        public NPCInfo() {
            handEffects = new HashMap<>();
            skillLevels = new HashMap<>();
        }

        public void addEquipslotEffect(FormID f, KYWD k) {
            Set<KYWD> keySet = handEffects.get(f);
            if (keySet == null) {
                keySet = new HashSet<>();
                keySet.add(k);
                handEffects.put(f, keySet);
            } else {
                keySet.add(k);
            }
        }

        public Map<FormID, Set<KYWD>> getHandEffects() {
            return handEffects;
        }

        public void addSkillLevel(Skill theSkill, int i) {
            skillLevels.put(theSkill, i);
        }

        public Map<Skill, Integer> getSkillLevels() {
            return skillLevels;
        }

    }

    private class SpellInfo {

        private Map<Skill, Integer> neededSkills;
        private Skill mainSkill;
        private MGEF mainEffect;
        private SPEL theSpell;
        private FormID equipslot;

        private SpellInfo() {
        }

        private SpellInfo(SPEL theSpell, Skill mainSkill, MGEF mainEffect, Map<Skill, Integer> neededSkills) {
            this.theSpell = theSpell;
            this.neededSkills = neededSkills;
            this.mainSkill = mainSkill;
            this.mainEffect = mainEffect;
            equipslot = theSpell.getEquipSlot();
        }

        public Map<Skill, Integer> getNeededSkills() {
            return neededSkills;
        }

        public Skill getMainSkill() {
            return mainSkill;
        }

        public MGEF getMainEffect() {
            return mainEffect;
        }

        public FormID getEquipslot() {
            return equipslot;
        }

    }

}
