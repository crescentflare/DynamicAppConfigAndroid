package com.crescentflare.appconfigexample.test.page;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.test.ActivityInstrumentationTestCase2;

import com.crescentflare.appconfig.adapter.AppConfigAdapterEntry;
import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Contains the implementation of the cucumber references
 */
@CucumberOptions(features = "features", tags = "@main")
public class Main extends ActivityInstrumentationTestCase2<MainActivity>
{
    /**
     * Initialization
     */
    public Main(MainActivity activity)
    {
        super(MainActivity.class);
    }

    /**
     * Test scripts
     */
    @Given("^I am on the \"([^\"]*)\" page$")
    public void I_am_on_the_pageName_page(String pageName) throws Throwable
    {
        getActivity();
        onView(allOf(isDescendantOfA(withId(R.id.action_bar_container)), withText(pageName))).check(matches(isDisplayed()));
    }

    @When("^I select the \"([^\"]*)\" configuration$")
    public void I_select_the_configurationName_configuration(String configurationName) throws Throwable
    {
        onData(allOf(is(instanceOf(AppConfigAdapterEntry.class)), withConfigSelectionContent(configurationName))).inAdapterView(withId(R.id.app_config_activity_manage_list)).perform(click());
    }

    @Then("^I see the \"([^\"]*)\" settings$")
    public void I_see_the_configurationName_settings(String configurationName) throws Throwable
    {
        onView(withId(R.id.activity_main_config_name)).check(matches(withText("Selected config: " + configurationName)));
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
                return itemTextMatcher.matches(entry.getLabel()) && entry.getType() == AppConfigAdapterEntry.Type.Configuration;
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
