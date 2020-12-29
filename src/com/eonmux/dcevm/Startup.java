package com.eonmux.dcevm;

import com.eonmux.dcevm.services.NotificationManager;
import com.eonmux.dcevm.services.OperationsManager;
import com.eonmux.dcevm.services.ToggleButton;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.text.StringUtil;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.jetbrains.annotations.NotNull;

public class Startup implements StartupActivity {
    NotificationManager notificationManager;
    OperationsManager operationsManager;
    public ToggleButton toggleButton;

    public Startup() {
        this.notificationManager = new NotificationManager();
        this.operationsManager = new OperationsManager(this);
    }

    @Override
    public void runActivity(@NotNull final Project project) {
        final Application application = ApplicationManager.getApplication();

        final String url = Attributes.getUrl();
        if (StringUtil.isEmpty(url)) {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
            if (propertiesComponent.getBoolean(Attributes.NO_DCEVM_ASSEMBLED_FOR_CURRENT_ENVIRONMENT_MESSAGE_SHOWN_KEY, false)) {
                notificationManager.notifyNoDcevmAvailableForLocalEnvironment(project);
                propertiesComponent.setValue(Attributes.NO_DCEVM_ASSEMBLED_FOR_CURRENT_ENVIRONMENT_MESSAGE_SHOWN_KEY, Boolean.TRUE.toString());
            }
            return;
        }


        if (operationsManager.isAvailableLocally()) {
            //Patch Project
            operationsManager.patchProject(project);
            operationsManager.setJavaExecutableIfNeeded(Attributes.getJavaExecutable(), project);

            //Check if up to date
            application.executeOnPooledThread(() -> operationsManager.checkIfDcevmUpToDate(project, url));
        } else {
            notificationManager.askPermissionToDownloadDcevm(project, () -> operationsManager.download(project, url));
        }

        notificationManager.askPermissionToInstallDcevm(project, () -> operationsManager.chooseFile(project));
        notificationManager.askPermissionToPickDcevm(project, () -> operationsManager.chooseDir(project));
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public OperationsManager getOperationsManager() {
        return this.operationsManager;
    }

    public ToggleButton getToggleButton() {
        return this.toggleButton;
    }
}
