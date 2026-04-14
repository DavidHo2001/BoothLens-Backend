package com.boothlens.util;

import org.springframework.data.util.Pair;

public class FileUtil {

    private FileUtil() {
    }

    public static String extractExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        if (dot == -1 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase();
    }

    public static Pair<String, String> extractFileNameAndExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return Pair.of("", "");
        }
        int dot = filename.lastIndexOf('.');
        if (dot == -1 || dot == filename.length() - 1) {
            return Pair.of(filename, "");
        }
        String name = filename.substring(0, dot);
        String extension = filename.substring(dot + 1).toLowerCase();
        return Pair.of(name, extension);
    }
}
