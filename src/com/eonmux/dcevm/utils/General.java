package com.eonmux.dcevm.utils;

import com.intellij.openapi.util.io.FileUtilRt;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Objects;

public class General {
    public static void decompress(File in, File out) throws IOException {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(in)))) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    if (!parent.mkdirs()) {
                        throw new IOException("Could not create directory for JDK at " + out.toString());
                    }
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
    }

    public static void move(File currDir, File toDir) throws IOException, NullPointerException {
        for (File file : Objects.requireNonNull(currDir.listFiles())) {
            if (file.isDirectory()) {
                File newDir = new File(toDir.getAbsolutePath() + "//" + file.getName());
                FileUtilRt.createDirectory(newDir);
                move(file, newDir);
            }
            else {
                FileUtils.moveFile(file, new File(toDir.getAbsolutePath() + "//" + file.getName()));
            }
        }
    }

    public static String getFileAsString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try
        {
            URL url = new URL("file:///" + filePath);
            BufferedReader br = new BufferedReader(new FileReader(url.getPath()));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
