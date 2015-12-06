package com.crescentflare.appconfig.manager;


import com.crescentflare.appconfig.model.AppConfigBaseModel;
import com.crescentflare.appconfig.model.AppConfigStorageItem;

/**
 * Library manager: base manager for app customization
 * Derive your custom config manager from this class for integration
 */
public class AppConfigBaseManager
{
    /**
     * Member
     */
    AppConfigBaseModel currentConfig = null;


    /**
     * Return an instance of the app base model, override this function to return a specific model class for the app
     */
    public AppConfigBaseModel getBaseModelInstance()
    {
        return new AppConfigBaseModel();
    }

    /**
     * Used by the derived manager to return the selected configuration instance, typecasted to the specific model class for the app
     */
    protected AppConfigBaseModel getCurrentConfigInstance()
    {
        if (currentConfig == null)
        {
            return getBaseModelInstance();
        }
        return currentConfig;
    }

    /**
     * Used internally to convert the config item to the model
     */
    public void applyCurrentConfig(AppConfigStorageItem item)
    {
        currentConfig = getBaseModelInstance();
        if (item != null)
        {
            currentConfig.applyCustomSettings(item);
        }
    }
}
