package com.crescentflare.appconfigexample.test.model;

import android.support.test.espresso.matcher.BoundedMatcher;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.R;
import com.crescentflare.appconfigexample.test.helper.CheckViewHelper;
import com.crescentflare.appconfigexample.test.helper.PerformViewHelper;
import com.crescentflare.appconfigexample.test.helper.WaitViewHelper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

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
        PerformViewHelper.disableLongClick(R.id.app_config_activity_manage_list);
        onData(allOf(is(instanceOf(AppConfigAdapterEntry.class)), withConfigSelectionContent(configurationToString(configuration)))).inAdapterView(withId(R.id.app_config_activity_manage_list)).perform(click());
        return this;
    }

    public ManageAppConfigModel editConfig(Configuration configuration)
    {
        onData(allOf(is(instanceOf(AppConfigAdapterEntry.class)), withConfigSelectionContent(configurationToString(configuration)))).inAdapterView(withId(R.id.app_config_activity_manage_list)).perform(longClick());
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


    // ---
    // Custom matcher for the manage config selection list view
    // ---

    private static Matcher<Object> withConfigSelectionContent(String expectedText)
    {
        checkNotNull(expectedText);
        return withConfigSelectionContent(equalTo(expectedText));
    }

    private static Matcher<Object> withConfigSelectionContent(final Matcher<String> itemTextMatcher)
    {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, AppConfigAdapterEntry>(AppConfigAdapterEntry.class)
        {
            @Override
            public boolean matchesSafely(AppConfigAdapterEntry entry)
            {
                return itemTextMatcher.matches(entry.getLabel().replace(" *", "")) && entry.getType() == AppConfigAdapterEntry.Type.Configuration && entry.getSection() != AppConfigAdapterEntry.Section.LastSelected;
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendText("with item content: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }
}
