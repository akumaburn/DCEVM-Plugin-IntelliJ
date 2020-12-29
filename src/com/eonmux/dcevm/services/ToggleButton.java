package com.eonmux.dcevm.services;

import com.eonmux.dcevm.Attributes;
import com.eonmux.dcevm.Startup;
import icons.*;
import com.esotericsoftware.minlog.Log;
import com.intellij.ide.ui.customization.CustomActionsSchema;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class ToggleButton extends AnAction {

    OperationsManager operationsManager;

    public ToggleButton(OperationsManager manager) {
        //Set Icon to off
        //TODO: Make this work with setEnabled instead of manually messing with it
        this.getTemplatePresentation().setIcon(PluginIcons.RELOAD_OFF_16);
        this.getTemplatePresentation().setDisabledIcon(PluginIcons.RELOAD_OFF_16);

        //Add the action
        ActionManager.getInstance().registerAction("DCEVM", this);
        DefaultActionGroup mainToolBar = (DefaultActionGroup)ActionManager.getInstance().getAction("NavBarToolBar");
        mainToolBar.addSeparator();
        mainToolBar.addAction(this);

        //Refresh the toolbars
        forceUpdate();
        operationsManager = manager;
    }

    public void forceUpdate() {
        //TODO: Not ideal find the correct method to do this programatically
        //TODO: Note: ActionToolbarImpl.updateAllToolbarsImmediately
        try {
            CustomActionsSchema.setCustomizationSchemaForCurrentProjects();
        }
        catch (Exception e) {
            Log.debug(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        operationsManager.reloadHotSwap();
    }


}
