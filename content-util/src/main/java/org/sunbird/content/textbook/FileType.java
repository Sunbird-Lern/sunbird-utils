package org.sunbird.content.textbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileType {

    private String type;
    private List<String> seperators;

    public FileType(String type, List<String> seperator) {
        this.type = type;
        this.seperators = seperator;
    }

    public String getType() { return type; }

    public String getExtension() { return "." + type; }

    public String getSeperator() { return getSeperator(0); }

    public String getSeperator(int i) { return seperators.get(i); }

    public enum Type {
        CSV("csv", new String[] {","});

        private String type;
        private String[] seperators;

        Type(String type, String[] seperators) {
            this.type = type;
            this.seperators = seperators;
        }

        public FileType getFileType() { return new FileType(type, new ArrayList<>(Arrays.asList(seperators))); }

        public String getExtension() { return getFileType().getExtension(); }

        public String getSeperator() { return getSeperator(0); }

        public String getSeperator(int i) { return getFileType().getSeperator(i); }
    }

}
