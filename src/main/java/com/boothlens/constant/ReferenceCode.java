package com.boothlens.constant;

public enum ReferenceCode {
    BOOTH_ANALYSIS("BA"),
    CONTRACTOR_REQUEST("CR");

    private final String value;

    ReferenceCode(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public String getReferenceCode(Integer id) {
        return value + "-" + id;
    }
}
