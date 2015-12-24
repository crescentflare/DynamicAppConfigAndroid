package com.crescentflare.appconfigexample;

import android.app.Application;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigManager;

/**
 * The singleton application context (containing the other singletons in the app)
 */
public class ExampleApplication extends Application implements AppConfigStorage.ChangedConfigListener
{
    /**
     * Initialization
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (getResources().getBoolean(R.bool.app_config_selection_enabled))
        {
            AppConfigStorage.instance.init(this, ExampleAppConfigManager.instance);
            AppConfigStorage.instance.setLoadingSourceAssetFile("appConfig.json");
        }
    }

    /**
     * Listeners
     */
    @Override
    public void onChangedConfig()
    {
        //In here the application can re-initialize singletons if they rely on the config or
        //do other things necessary
    }
}
