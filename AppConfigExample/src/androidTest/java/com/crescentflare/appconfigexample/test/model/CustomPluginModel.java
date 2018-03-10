package com.crescentflare.appconfigexample.test.model;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.R;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigLogLevel;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigRunType;
import com.crescentflare.appconfigexample.test.helper.CheckViewHelper;
import com.crescentflare.appconfigexample.test.model.shared.SettingType;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test model: custom plugin
 * Check for a custom plugin screen
 */
public class CustomPluginModel
{
    // ---
    // Checks
    // ---

    public CustomPluginModel expectShowLogScreen()
    {
        CheckViewHelper.checkOnPage("Example App Config log");
        return this;
    }
}
