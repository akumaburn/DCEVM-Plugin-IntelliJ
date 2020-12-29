package com.eonmux.dcevm.services;

import com.eonmux.dcevm.Attributes;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Amir Eslampanah
 * @date date(" EEEEE yyyy - MM - dd HH : mm : ssZ ")
 */
public class NotificationManager {

    public void customNotificationInfo(@NotNull Project project, String msg) {
        new Notification(Attributes.DCEVM_NAME, Attributes.DCEVM_NAME, msg, NotificationType.INFORMATION).notify(project);
    }

    public void customNotificationWarn(@NotNull Project project, String msg) {
        new Notification(Attributes.DCEVM_NAME, Attributes.DCEVM_NAME, msg, NotificationType.WARNING).notify(project);
    }

    public void customNotificationError(@NotNull Project project, String msg) {
        new Notification(Attributes.DCEVM_NAME, Attributes.DCEVM_NAME, msg, NotificationType.ERROR).notify(project);
    }

    public void notifyNoDcevmAvailableForLocalEnvironment(@NotNull Project project) {
        new Notification(Attributes.DCEVM_NAME, Attributes.DCEVM_NAME, "No DCEVM is assembled for the current environment",
                NotificationType.INFORMATION).notify(project);
    }

    public void notifyCannotSetJavaExecutable(@NotNull Project project) {
        new Notification(Attributes.DCEVM_NAME, Attributes.DCEVM_NAME, "Can't set execution permission for java runtime",
                NotificationType.INFORMATION).notify(project);
    }

    public void notifyServerError(@Nullable Project project) {
        new Notification(Attributes.DCEVM_NAME, Attributes.DCEVM_NAME, "Can't download DCEVM because of server error",
                NotificationType.INFORMATION).notify(project);
    }

    public void askPermissionToDownloadDcevm(@NotNull Project project, @NotNull final Runnable callback) {
        Notification notification = new Notification(Attributes.DCEVM_NAME, "[Optional] DCEVM is available for your environment",
                "<html>Would you like to <a href=''>download</a> it? <b>WARN: Will Delete Previous Installations</b></html>",
                NotificationType.INFORMATION, (notification1, event) -> {
            notification1.expire();
            callback.run();
        });
        notification.notify(project);
    }

    public void askPermissionToUpdateDcevm(@NotNull Project project, @NotNull final Runnable callback) {
        Notification notification = new Notification(Attributes.DCEVM_NAME, "New DCEVM version is available for your environment",
                "<html>Would you like to <a href=''>download</a> it? <b>WARN: Will Delete Previous Installations</b></html>",
                NotificationType.INFORMATION, (notification1, event) -> {
            notification1.expire();
            callback.run();
        });
        notification.notify(project);
    }

    public void askPermissionToPickDcevm(@NotNull Project project, @NotNull final Runnable callback) {
        Notification notification =
                new Notification(Attributes.DCEVM_NAME, "[Optional] Set DCEVM directory: ", "<html><a href=''>Manually</a> </html>",
                        NotificationType.INFORMATION, (notification2, event) -> {
                    notification2.expire();
                    callback.run();
                });
        notification.notify(project);
    }

    public void askPermissionToInstallDcevm(@NotNull Project project, @NotNull final Runnable callback) {
        Notification notification = new Notification(Attributes.DCEVM_NAME,
                "[Optional] DCEVM can also be installed to (AppData (win) or user.home (nix/mac)) by picking: ",
                "<html><a href=''>ZIP or TAR.GZ File</a> <b>WARN: Will Delete Previous Installations</b></html>",
                NotificationType.INFORMATION, (notification3, event) -> {
            notification3.expire();
            callback.run();
        });
        notification.notify(project);
    }
}