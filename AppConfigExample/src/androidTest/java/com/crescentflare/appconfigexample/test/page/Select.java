package com.crescentflare.appconfigexample.test.page;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.test.ActivityInstrumentationTestCase2;

import com.crescentflare.appconfig.adapter.AppConfigAdapterEntry;
import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Contains the implementation of the cucumber references
 */
public class Select extends ActivityInstrumentationTestCase2<MainActivity>
{
    /**
     * Initialization
     */
    public Select(MainActivity activity)
    {
        super(MainActivity.class);
    }

    /**
     * Test scripts
     */
    @When("^I select the \"([^\"]*)\" configuration$")
    public void I_select_the_configurationName_configuration(String configurationName) throws Throwable
    {
        onView(withId(R.id.app_config_activity_manage_hackfix)).perform(click());
        onData(allOf(is(instanceOf(AppConfigAdapterEntry.class)), withConfigSelectionContent(configurationName))).inAdapterView(withId(R.id.app_config_activity_manage_list)).perform(click());
    }

    @When("^I edit the \"([^\"]*)\" configuration$")
    public void I_edit_the_configurationName_configuration(String configurationName) throws Throwable
    {
        onData(allOf(is(instanceOf(AppConfigAdapterEntry.class)), withConfigSelectionContent(configurationName))).inAdapterView(withId(R.id.app_config_activity_manage_list)).perform(longClick());
    }

    /**
     * Custom matcher for the manage config selection list view
     */
    public static Matcher<Object> withConfigSelectionContent(String expectedText)
    {
        checkNotNull(expectedText);
        return withConfigSelectionContent(equalTo(expectedText));
    }

    public static Matcher<Object> withConfigSelectionContent(final Matcher<String> itemTextMatcher)
    {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, AppConfigAdapterEntry>(AppConfigAdapterEntry.class)
        {
            @Override
            public boolean matchesSafely(AppConfigAdapterEntry entry)
            {
                return itemTextMatcher.matches(entry.getLabel().replace(" *", "")) && entry.getType() == AppConfigAdapterEntry.Type.Configuration;
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
