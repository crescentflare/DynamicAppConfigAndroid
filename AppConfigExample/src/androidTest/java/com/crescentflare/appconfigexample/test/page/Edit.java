package com.crescentflare.appconfigexample.test.page;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.test.ActivityInstrumentationTestCase2;

import com.crescentflare.appconfig.adapter.AppConfigAdapterEntry;
import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Contains the implementation of the cucumber references
 */
public class Edit extends ActivityInstrumentationTestCase2<MainActivity>
{
    /**
     * Initialization
     */
    public Edit(MainActivity activity)
    {
        super(MainActivity.class);
    }

    /**
     * Test scripts
     */
    @When("^I change \"([^\"]*)\" into string \"([^\"]*)\"$")
    public void I_change_settingName_into_string_value(String settingName, String value) throws Throwable
    {
        onView(withTagValue(withStringMatching(settingName))).perform().perform(clearText()).perform(click()).perform(typeText(value));
    }

    @When("^I change \"([^\"]*)\" into number \"([^\"]*)\"$")
    public void I_change_settingName_into_number_value(String settingName, String value) throws Throwable
    {
        onView(withTagValue(withStringMatching(settingName))).perform().perform(clearText()).perform(click()).perform(typeText(value));
    }

    @When("^I apply the changes$")
    public void I_apply_the_changes() throws Throwable
    {
        onView(withId(R.id.app_config_activity_edit_save)).perform(click());
    }

    @When("I manually change \"([^\"]*)\" into \"([^\"]*)\"$")
    public void I_manually_change_settingName_into_string_value(final String settingName, final String value) throws Throwable
    {
        getInstrumentation().runOnMainSync(new Runnable()
        {
            @Override
            public void run()
            {
                AppConfigStorage.instance.manuallyChangeCurrentConfig(getInstrumentation().getTargetContext(), settingName, value);
            }
        });
    }

    /**
     * Custom matcher for the edit views
     */
    public static Matcher<Object> withStringMatching(String expectedText)
    {
        checkNotNull(expectedText);
        return withStringMatching(equalTo(expectedText));
    }

    @SuppressWarnings("rawtypes")
    public static Matcher<Object> withStringMatching(final Matcher<String> itemTextMatcher)
    {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, String>(String.class)
        {
            @Override
            public boolean matchesSafely(String string)
            {
                return itemTextMatcher.matches(string);
            }

            @Override
            public void describeTo(Description description)
            {
                description.appendText("with string: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }
}
