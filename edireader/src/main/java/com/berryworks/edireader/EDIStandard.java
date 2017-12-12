package com.berryworks.edireader;

public enum EDIStandard {
    ANSI("ANSI X.12"),
    EDIFACT("EDIFACT"),
    HL7("HL7"),
    NCPDP("NCPDP"),
    AL3("AL7"),
    CARGO("CARGO"),
    TRADACOMS("TRADACOMS"),
    TELCO("TELCO");

    private final String displayName;

    EDIStandard(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EDIStandard select(String name) {
        EDIStandard result = null;
        for (EDIStandard instance : values()) {
            if (instance.name().equalsIgnoreCase(name) || instance.displayName.contains(name)) {
                result = instance;
                break;
            }
        }
        if ("X.12".equalsIgnoreCase(name) || "X12".equalsIgnoreCase(name) || "ANSI".equalsIgnoreCase(name)) {
            return ANSI;
        }
        return result;
    }
}
