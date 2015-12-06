package com.crescentflare.appconfigexample;

import android.app.Application;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigManager;

/**
 * The singleton application context (containing the other singletons in the app)
 */
public class ExampleApplication extends Application
{
    /**
     * Initialization
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        AppConfigStorage.instance.init(this, new ExampleAppConfigManager());
        AppConfigStorage.instance.setLoadingSourceAssetFile("appConfig.json");
    }
}