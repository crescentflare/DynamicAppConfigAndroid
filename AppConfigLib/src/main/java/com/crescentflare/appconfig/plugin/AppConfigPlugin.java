package com.crescentflare.appconfig.plugin;

import android.app.Activity;

/**
 * Library plugin: an interface to define a custom plugin
 * Plugins can be made to manage custom values or add custom interaction to the selection menu
 * For example: launching a custom activity with development tools
 */
public interface AppConfigPlugin
{
    String displayName();
    String displayValue();
    boolean canInteract();
    void interact(Activity fromActivity);
}
