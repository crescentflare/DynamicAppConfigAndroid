package com.crescentflare.appconfigexample.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.R;

import java.util.HashMap;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Then;

/**
 * Contains the implementation of the cucumber references
 */
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
    @Then("^I see the \"([^\"]*)\" settings$")
    public void I_see_the_configurationName_settings(String configurationName) throws Throwable
    {
        onView(withId(R.id.activity_main_config_name)).check(matches(withText("name: " + configurationName)));
    }

    @Then("^I see \"([^\"]*)\" set to \"([^\"]*)\"$")
    public void I_see_settingName_set_to_value(String settingName, String value) throws Throwable
    {
        Map<String, Integer> settingIds = new HashMap();
        settingIds.put("name", R.id.activity_main_config_name);
        settingIds.put("apiUrl", R.id.activity_main_config_api_url);
        settingIds.put("runType", R.id.activity_main_config_run_type);
        settingIds.put("acceptAllSSL", R.id.activity_main_config_accept_all_ssl);
        settingIds.put("networkTimeoutSec", R.id.activity_main_config_network_timeout_sec);
        onView(withId(settingIds.get(settingName))).check(matches(withText(settingName + ": " + value)));
    }
}
