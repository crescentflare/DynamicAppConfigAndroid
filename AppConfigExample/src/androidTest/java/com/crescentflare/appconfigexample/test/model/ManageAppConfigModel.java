package com.crescentflare.appconfigexample.test.model;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.R;
import com.crescentflare.appconfigexample.test.helper.CheckViewHelper;
import com.crescentflare.appconfigexample.test.helper.PerformViewHelper;
import com.crescentflare.appconfigexample.test.helper.WaitViewHelper;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static com.crescentflare.appconfigexample.test.helper.CheckViewHelper.withConfigTagStringMatching;

/**
 * Test model: manage app config
 * Interaction related to the app configurations screen
 */
public class ManageAppConfigModel
{
    // ---
    // Configuration enum
    // ---

    public enum Configuration
    {
        Mock,
        Test,
        TestInsecure,
        Accept,
        Production
    }


    // ---
    // Interaction
    // ---

    public ManageAppConfigModel revertToConfigurationDefaults()
    {
        getInstrumentation().runOnMainSync(new Runnable()
        {
            @Override
            public void run()
            {
                AppConfigStorage.instance.clearAllToDefaults(getInstrumentation().getTargetContext());
            }
        });
        return this;
    }

    public ManageAppConfigModel selectConfig(Configuration configuration)
    {
        onView(withTagValue(withConfigTagStringMatching(configurationToString(configuration)))).perform(scrollTo()).perform(click());
        return this;
    }

    public ManageAppConfigModel editConfig(Configuration configuration)
    {
        onView(withTagValue(withConfigTagStringMatching(configurationToString(configuration)))).perform(scrollTo()).perform(longClick());
        return this;
    }


    // ---
    // Checks
    // ---

    public MainAppModel expectMainAppScreen()
    {
        CheckViewHelper.checkOnPage("Example App Config");
        return new MainAppModel();
    }

    public EditAppConfigModel expectEditConfigScreen()
    {
        CheckViewHelper.checkOnPage("Edit configuration");
        WaitViewHelper.waitOptionalTextDisappear("Loading configurations...", 5000);
        return new EditAppConfigModel();
    }


    // ---
    // Helper
    // ---

    private String configurationToString(Configuration configuration)
    {
        switch (configuration)
        {
            case Mock:
                return "Mock server";
            case Test:
                return "Test server";
            case TestInsecure:
                return "Test server (insecure)";
            case Accept:
                return "Acceptation server";
            case Production:
                return "Production";
        }
        return "";
    }
}
