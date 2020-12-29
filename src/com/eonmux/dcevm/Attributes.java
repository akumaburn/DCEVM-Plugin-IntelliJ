package com.eonmux.dcevm;

import com.eonmux.dcevm.services.NotificationManager;
import com.esotericsoftware.minlog.Log;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.openapi.util.SystemInfo;
import com.eonmux.dcevm.utils.Version;
import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 * @author Amir Eslampanah
 * @date date(" EEEEE yyyy - MM - dd HH : mm : ssZ ")
 */
public class Attributes {
    @NotNull
    private static final String JAVA_EXEC = "bin" + File.separator + "java" + (SystemInfo.isWindows ? ".exe" : "");
    @NotNull
    public static String JDK_DIR = getInstallationDirectory().toString();

    public static final String NO_DCEVM_ASSEMBLED_FOR_CURRENT_ENVIRONMENT_MESSAGE_SHOWN_KEY =
            "no.dcevm.assembled.for.current.environment.message.shown";
    public static final String LOCAL_DCEVM_SIZE_KEY = "local.dcevm.size";
    public static final String DCEVM_NAME = "DCEVM";
    public static final String ZIP_EXTENSION = ".zip", TAR_GZ_EXTENSION = ".tar.gz", WINDOWS_PREFIX = "WIN";

    public static final Version VERSION = new Version("11.0.8");

    private static final String LINUX64 =
            "https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases/download/dcevm-11.0.8%2B1/java11-openjdk-dcevm-linux.tar.gz";
    private static final String MACOS64 =
            "https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases/download/dcevm-11.0.8%2B1/java11-openjdk-dcevm-osx.tar.gz";
    private static final String WIN64 =
            "https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases/download/dcevm-11.0.8%2B1/java11-openjdk-dcevm-windows.zip";

    /**
     * @return url which points to the target assembled DCEVM to use at the current environment;
     * <code>null</code> if no DCEVM for the current environment has been assembled
     */
    @Nullable
    public static String getUrl() {
        if (SystemInfo.isLinux) {
            if (SystemInfo.is32Bit) {
                //return LINUX32;
                return null;
            }
            else if (SystemInfo.is64Bit) {
                return LINUX64;
            }
        }
        else if (SystemInfo.isMac) {
            return MACOS64;
        }
        else if (SystemInfo.isWindows) {
            if (SystemInfo.is32Bit) {
                //return WIN32;
                return null;
            }
            else if (SystemInfo.is64Bit) {
                return WIN64;
            }
        }
        return null;
    }

    public static File getInstallationDirectory() {
        String installPath;
        String operatingSystem = (System.getProperty("os.name")).toUpperCase();
        if (operatingSystem.contains(WINDOWS_PREFIX)) {
            installPath = System.getenv("AppData");
        } else {
            installPath = System.getProperty("user.home");
            installPath += "/Library/Application Support";
        }

        File installDir = new File(installPath + "//" + Attributes.DCEVM_NAME);
        if (!installDir.exists()) {
            if (!installDir.mkdirs()) {
                Log.error("Could not create directory for DCEVM at: " + installDir.getAbsolutePath());
            }
        }

        return installDir;
    }

    @NotNull
    public static File getJdkDir() {
        try {
            if (JDK_DIR.compareTo(getInstallationDirectory().toString()) == 0) {
                Log.info("DCEVM is pointed to user default JDK_DIR of: " + getInstallationDirectory().toString());
                if (!new File(JDK_DIR, JAVA_EXEC).exists()) {
                    Log.warn("JDK_DIR seems invalid," +
                            new File(JDK_DIR, JAVA_EXEC).getAbsolutePath() +
                            " does not exist? Permissions issue or Actually missing??");
                }
                return new File(JDK_DIR);
            } else if (!JDK_DIR.isBlank()) {
                Log.info("DCEVM is pointed to user specified JDK_DIR: " + JDK_DIR);
                if (!new File(JDK_DIR, JAVA_EXEC).exists()) {
                    Log.warn("JDK_DIR seems invalid," +
                            new File(JDK_DIR, JAVA_EXEC).getAbsolutePath() +
                            " does not exist? Permissions issue or Actually missing??");
                }
                return new File(JDK_DIR);
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Couldn't get JDK Path. " + e, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return new File("");
    }

    public static void setJdkDir(String path) {
        JDK_DIR = path;
    }

    @NotNull
    public static File getJavaExecutable() {
        return new File(getJdkDir(), JAVA_EXEC);
    }
}
