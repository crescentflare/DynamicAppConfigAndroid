package com.crescentflare.appconfigexample;

import android.app.Application;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigManager;
import com.crescentflare.appconfigexample.appconfig.plugins.ExampleAppConfigLogPlugin;
import com.crescentflare.appconfigexample.utility.Logger;

/**
 * The singleton application context (containing the other singletons in the app)
 */
public class ExampleApplication extends Application implements AppConfigStorage.ChangedConfigListener
{
    // ---
    // Initialization
    // ---

    @Override
    public void onCreate()
    {
        super.onCreate();
        if (!BuildConfig.BUILD_TYPE.equals("release"))
        {
            ExampleAppConfigManager.instance.addPlugin(new ExampleAppConfigLogPlugin());
            AppConfigStorage.instance.init(this, ExampleAppConfigManager.instance);
            AppConfigStorage.instance.setLoadingSourceAssetFile("appConfig.json");
        }
        Logger.setApplicationContext(getApplicationContext());
        Logger.clear();
        Logger.log("Application started");
    }


    // ---
    // Listeners
    // ---

    @Override
    public void onChangedConfig()
    {
        // In here the application can re-initialize singletons if they rely on the config or
        // do other things necessary
    }
}
