package com.eonmux.dcevm.utils;

import java.io.File;
import java.io.FileFilter;

public class CompressedFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter {
    private final String[] okFileExtensions = new String[]{"zip", "tar.gz"};

    @Override
    public boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }
}