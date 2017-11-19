package com.crescentflare.appconfigexample.test.testcase;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigRunType;
import com.crescentflare.appconfigexample.test.model.ManageAppConfigModel;
import com.crescentflare.appconfigexample.test.model.TestApplication;
import com.crescentflare.appconfigexample.test.model.shared.SettingType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Feature: I can change my application configuration
 * As an app developer or tester
 * I want to be able to change the configuration
 * So I can test multiple configurations in one build
 */
@RunWith(AndroidJUnit4.class)
public class Main
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
     * Scenario: Selecting a configuration
     * Given I am on the "App configurations" page
     * When I select the "Test server" configuration
     * Then I see the "Test server" settings
     */
    @Test
    public void testSelectConfiguration()
    {
        TestApplication.instance
                .expectAppConfigurationsScreen()
                .revertToConfigurationDefaults()
                .selectConfig(ManageAppConfigModel.Configuration.Test)
                .expectMainAppScreen()
                .expectSetting(SettingType.Name).toBe("Test server")
                .expectSetting(SettingType.ApiURL).toBe("https://test.example.com/")
                .expectSetting(SettingType.RunType).toBe(ExampleAppConfigRunType.RunNormally)
                .expectSetting(SettingType.AcceptAllSSL).toBe(false)
                .expectSetting(SettingType.NetworkTimeoutSeconds).toBe(20);
    }
}
