package com.crescentflare.appconfigexample.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.R;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Contains the implementation of the shared cucumber references
 */
public class Shared extends ActivityInstrumentationTestCase2<MainActivity>
{
    /**
     * Initialization
     */
    public Shared(MainActivity activity)
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

    @When("^I reset configuration data$")
    public void I_reset_configuration_data() throws Throwable
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AppConfigStorage.instance.clearAllToDefaults(getActivity());
            }
        });
    }

    @Then("^I see the \"([^\"]*)\" page$")
    public void I_see_the_pageName_page(String pageName) throws Throwable
    {
        onView(allOf(isDescendantOfA(withId(R.id.action_bar_container)), withText(pageName))).check(matches(isDisplayed()));
    }
}
