package com.crescentflare.appconfigexample.test.testcase;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigLogLevel;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigRunType;
import com.crescentflare.appconfigexample.test.model.ManageAppConfigModel;
import com.crescentflare.appconfigexample.test.model.TestApplication;
import com.crescentflare.appconfigexample.test.model.shared.SettingType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Feature: I can use a custom plugin
 * As an app developer
 * I want to be able to add custom plugins in the selection menu
 * So I can integrate my own tools within the app config library easily
 */
@RunWith(AndroidJUnit4.class)
public class Plugin
{
    // ---
    // Members
    // ---

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);


    // ---
    // Scenarios
    // ---

    /**
     * Scenario: Using a custom plugin
     * Given I am on the "App configurations" page
     * When I select the "View log" custom plugin
     * Then I see the "Show log" screen
     */
    @Test
    public void testViewLog()
    {
        TestApplication.instance
                .expectAppConfigurationsScreen()
                .revertToConfigurationDefaults()
                .openCustomPlugin(ManageAppConfigModel.CustomPlugin.ViewLog)
                .expectShowLogScreen();
    }
}
