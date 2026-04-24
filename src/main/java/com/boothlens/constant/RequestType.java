package com.boothlens.constant;

import java.util.Locale;

public enum RequestType {
    CONTRACTOR,
    SUPPORT;

    public ReferenceCode toReferenceCode() {
        return switch (this) {
            case CONTRACTOR -> ReferenceCode.CONTRACTOR_REQUEST;
            case SUPPORT -> ReferenceCode.SUPPORT_REQUEST;
        };
    }

    public static RequestType fromString(String rawType) {
        if (rawType == null || rawType.isBlank()) {
            throw new IllegalArgumentException("requestType is required");
        }
        try {
            return RequestType.valueOf(rawType.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported requestType");
        }
    }
}
