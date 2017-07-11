package asis.ini_asis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import lev.Ln;
import skyproc.FormID;
import skyproc.ModListing;

/**
 *
 * @author Dres Croffgrin and Plutoman
 */
public class INI_asis {

    public INI_asis(String fileName) throws IOException {
        //Creates a BufferedReader to allow for reding of the ini file that the object represents
        //Will throw IOException of the ini sent does not exist or is corrupt
        iniReader = new BufferedReader(new FileReader(fileName));

        this.fileName = fileName;
    }//close constructor INI(String filename)

    public INI_asis(String fileName, Collection<IniSectionHead> sectionsToAdd) throws IOException {
        iniReader = new BufferedReader(new FileReader(fileName));

        this.fileName = fileName;

        addSection(sectionsToAdd);
    }

    //Reads the INI file and stores the contents in a Map
    public void readData() throws IOException {
        String currentLine; //Will store the string with the entire current line
        IniSectionHead currentSection = null; //The current INI section being processed

        if (iniReader == null) {
            iniReader = new BufferedReader(new FileReader(fileName));
        }

        boolean foundType = false; //A boolean indicating if there is an ini section being parsed

        //Loops through each line of the ini file and processes the contents
        while ((currentLine = iniReader.readLine()) != null) {
            //Removes all whitespace from the current line
            //and removes all text in a line that comes after a ';'

            currentLine = Ln.cleanLine(currentLine, commentMarkingString);

            //execute if current line is the header for a new section
            if (containsHeader(currentLine)) {

                currentSection = getHeaderFrom(currentLine);
                foundType = true;
                iniMap.put(currentSection, new ArrayList<IniData>());

            } //execute if current line contains data.
            else if (foundType && !currentLine.equalsIgnoreCase("") && currentLine != null) {

                // if the data is single-entry
                if (currentSection.getFormat().equals(IniDataFormat.VALUE)) {
                    IniData value = getVALUEDataFrom(currentLine);
                    iniMap.get(currentSection).add(value);
                } // if the data is KEY:VALUE
                else if (currentSection.getFormat().equals(IniDataFormat.KEY_VALUE)) {

                    IniData value = getKEY_VALUEDataFrom(currentLine);
                    iniMap.get(currentSection).add(value);

                }

            }


        }// Close while

        iniReader.close();

        iniReader = null;

        loadIniMaps();
    }// close method readData()

    //Returns an ArrayList with the String representations of the sections
    // in the INI file
    public Collection<String> getIniSections() {
        ArrayList<String> iniSectionsToReturn = new ArrayList<>();

        //Gathers the INI Sections from the map
        Set iniSectionTypes = iniMap.keySet();
        Iterator iniIterator = iniSectionTypes.iterator();

        //Adds the INI Sections to an ArrayList
        while (iniIterator.hasNext()) {
            iniSectionsToReturn.add((String) iniIterator.next());
        }

        return iniSectionsToReturn;
    }// Close method getIniSections

    //Returns the KEY:VALUE map with the header specified by the String
    public Collection<IniData> getSectionData(IniSectionHead mapToGet) {
        return iniMap.get(mapToGet);
    }

    public Map<String, String> getMap(IniSectionHead sectionToChoose) {
        if (!sectionToChoose.getFormat().equals(IniDataFormat.KEY_VALUE)) {
            return new HashMap<>(0);
        }

        String name = sectionToChoose.getName();

        if (keyValueData.get(name) == null) {
            return new HashMap<>(0);
        } else {
            return keyValueData.get(name);
        }
    }

    public Map<Integer, String> getMapIntStr(IniSectionHead sectionToChoose) {
        if (!sectionToChoose.getFormat().equals(IniDataFormat.KEY_VALUE)) {
            return new HashMap<>(0);
        }

        String name = sectionToChoose.getName();
        HashMap<Integer, String> intStrMap = new HashMap<>();
        int key;
        String value;

        for (Map.Entry<String, String> entry : keyValueData.get(name).entrySet()) {
            key = Integer.parseInt(entry.getKey());
            value = entry.getValue();
            intStrMap.put(key, value);
        }
        return intStrMap;
    }

    public Map<String, ArrayList<Integer>> getMapStrIntArrayList(IniSectionHead sectionToChoose) {
        if (!sectionToChoose.getFormat().equals(IniDataFormat.KEY_VALUE)) {
            return new HashMap<>(0);
        }

        String name = sectionToChoose.getName();
        HashMap<String, ArrayList<Integer>> intArrayStrMap = new HashMap<>();
        String key;
        String valueTemp;
        ArrayList<Integer> value = new ArrayList<>();

        for (Map.Entry<String, String> entry : keyValueData.get(name).entrySet()) {
            key = entry.getKey();
            valueTemp = entry.getValue();
            Scanner s = new Scanner(valueTemp);
            s.useDelimiter(" ");
            while (s.hasNext()) {
                value.add(Integer.parseInt(s.next()));
            }
            try {
                intArrayStrMap.put(key, value);
            } catch (NullPointerException e) {
                //Continue on.
            }
            s.close();
        }
        return intArrayStrMap;
    }

    public Collection<String> getCollection(IniSectionHead sectionToChoose) {
        if (!sectionToChoose.getFormat().equals(IniDataFormat.VALUE)) {
            return new ArrayList<>(0);
        }

        String name = sectionToChoose.getName();

        if (valueData.get(name) == null) {
            return new ArrayList<>(0);
        } else {
            return valueData.get(name);
        }
    }

    public Collection<FormID> getCollectionForms(IniSectionHead sectionToChoose) {

        if (!sectionToChoose.getFormat().equals(IniDataFormat.VALUE)) {
            return new ArrayList<>(0);
        }

        String name = sectionToChoose.getName();
        Collection<FormID> forms = new ArrayList<>();
        ArrayList<String> temp = (ArrayList) valueData.get(name);

        for (int i = 0; i < valueData.get(name).size(); i++) {
            forms.add(new FormID(temp.get(i), new ModListing("Skyrim", true)));
        }
        return forms;
    }

    /*
     * Prerequisites: - readData() has been run
     *
     * Returns: - A Map of all IniSectionHeads from the parameter that are valid
     * sections for this ini mapped to a Collection of IniData objects for that
     * head. The Collection is implemented as an ArrayList.
     */
    public Map<IniSectionHead, Collection<IniData>> getSectionData(Collection<IniSectionHead> mapsToGet) {
        Map<IniSectionHead, Collection<IniData>> dataToReturn = new TreeMap<>();

        // loops through the sections in the parameter, and adds all of the valid data associated with those
        //   keys to the map to return.
        for (IniSectionHead currentSection : mapsToGet) {
            if (iniMap.containsKey(currentSection)) {
                dataToReturn.put(currentSection, getSectionData(currentSection));
            }
        }
        return dataToReturn;
    }

    //Returns the full map for the INI
    public Map<IniSectionHead, Collection<IniData>> getData() {
        return iniMap;
    }

    public void setCommentMarkingString(String markerToSet) {
        commentMarkingString = markerToSet;
    }

    public void setKeyValueDelimiter(String delim) {
        keyValueDelimiter = delim;
    }

    public final void addSection(IniSectionHead section) {
        sections.add(section);
    }

    public final void addSection(Collection<IniSectionHead> sections) {
        for (IniSectionHead currentSection : sections) {
            this.sections.add(currentSection);
        }
    }

    private boolean hasSection(String sectionName) {
        boolean hasSection = false;

        for (IniSectionHead currentHead : sections) {
            if (currentHead.getName().equalsIgnoreCase(sectionName)) {
                hasSection = true;
            }
        }

        return hasSection;
    }

    private boolean containsHeader(String line) {
        if (!(line.contains("[") && line.contains("]"))) {
            return false;
        }

        //ensures that there is a string between the brackets to be used as a header
        if (line.indexOf("[") + 1 >= line.indexOf("]")) {
            return false;
        }

        String headerValue = getHeaderStringFrom(line);

        return hasSection(headerValue);
    }

    private String getHeaderStringFrom(String line) {
        return line.substring(line.indexOf("[") + 1, line.indexOf("]"));
    }

    private IniSectionHead getHeaderFrom(String line) {
        for (IniSectionHead currentHead : sections) {
            if (currentHead.getName().equalsIgnoreCase(getHeaderStringFrom(line))) {
                return currentHead;
            }
        }
        return null;
    }

    private IniData getVALUEDataFrom(String line) {
        return new IniData(IniDataFormat.VALUE, line);
    }

    private IniData getKEY_VALUEDataFrom(String line) {
        Scanner lineParser = new Scanner(line);
        lineParser.useDelimiter(keyValueDelimiter);

        String key = lineParser.next();
        String value = lineParser.next();
        lineParser.close();

        return new IniData(IniDataFormat.KEY_VALUE, key, value);
    }

    private Map<String, Map<String, String>> keyValueData() {
        Map<String, Map<String, String>> dataToReturn = new TreeMap<>();

        for (IniSectionHead currentSection : iniMap.keySet()) {
            if (currentSection.getFormat().equals(IniDataFormat.KEY_VALUE)) {
                dataToReturn.put(currentSection.getName(), new TreeMap<String, String>());
                Map<String, String> currentDataMap = dataToReturn.get(currentSection.getName());

                for (IniData currentData : iniMap.get(currentSection)) {
                    currentDataMap.put(currentData.getKey(), currentData.getValue());
                }
            }
        }

        return dataToReturn;
    }

    private Map<String, Collection<String>> valueData() {
        Map<String, Collection<String>> dataToReturn = new TreeMap<>();

        for (IniSectionHead currentSection : iniMap.keySet()) {
            if (currentSection.getFormat().equals(IniDataFormat.VALUE)) {
                dataToReturn.put(currentSection.getName(), new ArrayList<String>());
                Collection<String> currentDataCollection = dataToReturn.get(currentSection.getName());

                for (IniData currentData : iniMap.get(currentSection)) {
                    currentDataCollection.add(currentData.getValue());
                }
            }
        }

        return dataToReturn;
    }

    private void loadIniMaps() {
        keyValueData = keyValueData();
        valueData = valueData();
    }

    public Map<String, Map<String, String>> getKeyValueMap() {
        return keyValueData;
    }

    public Map<String, Collection<String>> getValueMap() {
        return valueData;
    }
    // The Reader to read the ini.
    private BufferedReader iniReader;
    private String fileName;
    private String commentMarkingString = ";";
    private String keyValueDelimiter = "[:[=]]";
    // The map to store all of the INIs data
    private Map<IniSectionHead, Collection<IniData>> iniMap = new TreeMap<>();
    private Collection<IniSectionHead> sections = new ArrayList<>();
    private Map<String, Map<String, String>> keyValueData = null;
    private Map<String, Collection<String>> valueData = null;

    public static class IniSectionHead implements Comparable<IniSectionHead> {

        public IniSectionHead(String name, IniDataFormat format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public IniDataFormat getFormat() {
            return format;
        }

        public boolean equals(IniSectionHead other) {
            return ((other.getName().equalsIgnoreCase(getName())) && (other.getFormat().equals(getFormat())));
        }

        @Override
        public boolean equals(Object otherObject) {
            try {
                IniSectionHead other = (IniSectionHead) otherObject;
                return ((other.getName().equalsIgnoreCase(getName())) && (other.getFormat().equals(getFormat())));
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public int compareTo(IniSectionHead other) {
            if (this.equals(other)) {
                return 0;
            } else {
                return (this.getName().compareTo(other.getName()));
            }
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 43 * hash + (this.format != null ? this.format.hashCode() : 0);
            return hash;
        }
        private String name;
        private IniDataFormat format;
    }

    public static class IniData {

        public IniData(IniDataFormat format) {
            this.format = format;
        }

        public IniData(IniDataFormat format, String value) {
            this.format = format;
            this.value = value;
        }

        public IniData(IniDataFormat format, String key, String value) {
            this.format = format;
            this.key = key;
            this.value = value;
        }

        public IniDataFormat getFormat() {
            return format;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public boolean equals(IniData other) {
            if (!other.getFormat().equals(getFormat())) {
                return false;
            }
            if (!other.getKey().equals(getKey())) {
                return false;
            }
            return (other.getValue().equals(getValue()));
        }
        private IniDataFormat format;
        private String key = null;
        private String value;
    }

    public enum IniDataFormat {

        KEY_VALUE,
        VALUE;
    }
}