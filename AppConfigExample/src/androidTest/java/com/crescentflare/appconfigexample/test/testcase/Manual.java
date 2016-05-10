package com.crescentflare.appconfigexample.test.testcase;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.crescentflare.appconfigexample.MainActivity;
import com.crescentflare.appconfigexample.test.model.ManageAppConfigModel;
import com.crescentflare.appconfigexample.test.model.TestApplication;
import com.crescentflare.appconfigexample.test.model.shared.SettingType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Feature: I can manually change the active configuration
 * As an app developer or tester
 * I want to be able to manually change the active configuration
 * So I can optimize testing and make smaller test scripts
 */
@RunWith(AndroidJUnit4.class)
public class Manual
{
    /**
     * Members
     */
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    /**
     * Scenario: Manually change a configuration
     * Given I am on the "App configurations" page
     * When I reset configuration data
     * And I select the "Test server" configuration
     * And I manually change "apiUrl" into "https://manualchange.example.com/"
     * Then I see "apiUrl" set to "https://manualchange.example.com/"
     */
    @Test
    public void testManuallyChangeConfiguration()
    {
        TestApplication.instance
                .expectAppConfigurationsScreen()
                .revertToConfigurationDefaults()
                .selectConfig(ManageAppConfigModel.Configuration.Test)
                .expectMainAppScreen()
                .setSettingManually(SettingType.ApiURL).to("https://manualchange.example.com/")
                .expectSetting(SettingType.ApiURL).toBe("https://manualchange.example.com/");
    }
}
