package com.eonmux.dcevm.services;

import com.eonmux.dcevm.Attributes;
import com.eonmux.dcevm.Startup;
import com.eonmux.dcevm.utils.Version;
import com.thoughtworks.qdox.model.expression.Not;
import icons.*;
import com.eonmux.dcevm.utils.CompressedFileFilter;
import com.eonmux.dcevm.utils.General;
import com.eonmux.dcevm.utils.Network;
import com.intellij.execution.RunManager;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.util.io.ZipUtil;
import org.apache.maven.model.PluginConfiguration;
import org.jetbrains.annotations.NotNull;
import com.esotericsoftware.minlog.Log;
import org.w3c.dom.Attr;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.eonmux.dcevm.Attributes.*;

public class OperationsManager {
    NotificationManager notificationManager;
    ToggleButton toggleButton;
    ApplicationConfiguration appConfiguration;

    public OperationsManager(Startup startup) {
        notificationManager = startup.getNotificationManager();
        startup.toggleButton = new ToggleButton(this);
        toggleButton = startup.getToggleButton();
    }

    public void chooseFile(@NotNull final Project project) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new CompressedFileFilter());
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        String path = file.getPath();

        //Remove any existing JDKs at location
        FileUtilRt.delete(getInstallationDirectory());

        try {
            if (path.contains(TAR_GZ_EXTENSION)) {
                General.decompress(file, getInstallationDirectory());
            } else if (path.contains(ZIP_EXTENSION)) {
                ZipUtil.extract(file, getInstallationDirectory(), null);
            }
            String tmpDir = getInstallationDirectory().getAbsolutePath() + "\\" + Objects.requireNonNull(getInstallationDirectory().list())[0];

            General.move(new File(tmpDir), new File(getInstallationDirectory().getAbsolutePath()));

            FileUtilRt.delete(new File(tmpDir));

            Attributes.JDK_DIR = getInstallationDirectory().getAbsolutePath();

            //Patch Project
            patchProject(project);

            setJavaExecutableIfNeeded(Attributes.getJavaExecutable(), project);
        } catch (IOException e) {
            Log.warn("Unexpected error on un-tar-gz DCEVM", e);
        }
    }

    public void chooseDir(@NotNull final Project project) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.showOpenDialog(null);
        String path = chooser.getSelectedFile().getAbsolutePath();

        Attributes.setJdkDir(path);
        setJavaExecutableIfNeeded(Attributes.getJavaExecutable(), project);
    }

    public void download(@NotNull final Project project, @NotNull final String url) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Downloading " + Attributes.DCEVM_NAME, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                final File tmpFile = new File(FileUtilRt.getTempDirectory(), Attributes.DCEVM_NAME);
                FileUtilRt.delete(tmpFile);
                Network.download(project, tmpFile, url, indicator, () -> {
                    long length = tmpFile.length();
                    Log.info("DCEVM JDK downloaded. Size: " + length);
                    PropertiesComponent.getInstance().setValue(Attributes.LOCAL_DCEVM_SIZE_KEY, String.valueOf(length));
                    FileUtilRt.delete(getInstallationDirectory());
                    try {
                        if (url.contains(TAR_GZ_EXTENSION)) {
                            General.decompress(tmpFile, getInstallationDirectory());
                        } else if (url.contains(ZIP_EXTENSION)) {
                            ZipUtil.extract(tmpFile, getInstallationDirectory(), null);
                        }

                        String tmpDir =
                                getInstallationDirectory().getAbsolutePath() + "\\" + Objects.requireNonNull(getInstallationDirectory().list())[0];

                        General.move(new File(tmpDir), new File(getInstallationDirectory().getAbsolutePath()));

                        FileUtilRt.delete(new File(tmpDir));

                        Attributes.setJdkDir(getInstallationDirectory().getAbsolutePath());
                        patchProject(project);
                        setJavaExecutableIfNeeded(Attributes.getJavaExecutable(), project);
                    } catch (IOException e) {
                        Log.warn("Unexpected error on un-tar-gz DCEVM", e);
                    }
                });
            }
        });
    }

    public void checkIfDcevmUpToDate(@NotNull final Project project, @NotNull final String url) {
        String versionInfo = General.getFileAsString(Attributes.getJdkDir() + "/release");
        Version versionNumber = new Version("0");
        String[] versionLines = versionInfo.split("\\n");
        for (String s : versionLines) {
            if (s.contains("JAVA_VERSION=\"")) {
                versionNumber = new Version(s.replace("JAVA_VERSION=\"", "").replace("\"", ""));
            }
        }
        if (versionNumber.compareTo(VERSION) < 0) {
            int remoteSize = Network.getSize(url);
            if (remoteSize <= 0) {
                return;
            }
            int localSize = PropertiesComponent.getInstance().getInt(Attributes.LOCAL_DCEVM_SIZE_KEY, -1);
            if (localSize != remoteSize) {
                String message = "DCEVM JRE update available. Your Version is: " + versionNumber.get() + " Latest Version is: " + VERSION.get() +
                        " \n Previous recorded download size was %d. Size on server now is: %d\n";
                notificationManager.customNotificationInfo(project, String.format(message, localSize, remoteSize));
                notificationManager.askPermissionToUpdateDcevm(project, () -> download(project, url));
            }
        }
    }

    public void setJavaExecutableIfNeeded(@NotNull final File javaExecutable, @NotNull final Project project) {
        if (!SystemInfo.isWindows) {
            try {
                FileUtil.setExecutable(javaExecutable);
            } catch (IOException e) {
                notificationManager.notifyCannotSetJavaExecutable(project);
            }
        }
    }

    public boolean isAvailableLocally() {
        File dir = Attributes.getJdkDir();
        return dir.isDirectory() && Objects.requireNonNull(dir.list()).length > 0;
    }

    public void reloadHotSwap() {
        if (!(new File(Attributes.getJdkDir().getAbsolutePath(), "release")).exists()) {
            JOptionPane.showMessageDialog(null, "Click on one of the options in the Event Log", "Please Configure DCEVM correctly first",
                    JOptionPane.WARNING_MESSAGE);
        }
        else {
            if(appConfiguration == null) {
                JOptionPane.showMessageDialog(null, "No application configurations found. (Are you developing a plugin?)", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            appConfiguration.setAlternativeJrePath(Attributes.getJdkDir().getAbsolutePath());
            appConfiguration.setAlternativeJrePathEnabled(true);
            JOptionPane.showMessageDialog(null, "Enabled DCEVM at " + Attributes.getJdkDir().getAbsolutePath(), "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            toggleButton.getTemplatePresentation().setIcon(PluginIcons.RELOAD_16);
            toggleButton.forceUpdate();
        }
    }

    public void patchProject(@NotNull Project project) {
        for (RunConfiguration configuration : RunManager.getInstance(project).getAllConfigurationsList()) {
            notificationManager.customNotificationInfo(project, "Found Project Configuration: " + configuration.getName() + " Of Type: " + configuration.getType().getClass().getCanonicalName());
            if (configuration instanceof ApplicationConfiguration) {
                appConfiguration = (ApplicationConfiguration) configuration;
                appConfiguration.setAlternativeJrePath(Attributes.getJdkDir().getAbsolutePath());
                appConfiguration.setAlternativeJrePathEnabled(true);
                notificationManager.customNotificationInfo(project, "Enabled DCEVM found at: " + Attributes.getJdkDir().getAbsolutePath());
                toggleButton.getTemplatePresentation().setIcon(PluginIcons.RELOAD_16);
                toggleButton.forceUpdate();
            }
        }
    }
}
