package com.crescentflare.appconfigexample.test.model;

import com.crescentflare.appconfigexample.test.helper.CheckViewHelper;
import com.crescentflare.appconfigexample.test.helper.WaitViewHelper;

/**
 * Test model: application
 * Used as the main application model to reach into other flows
 */
public class TestApplication
{
    /**
     * Singleton
     */
    public static TestApplication instance = new TestApplication();

    private TestApplication()
    {
    }

    /**
     * Checks
     */
    public ManageAppConfigModel expectAppConfigurationsScreen()
    {
        CheckViewHelper.checkOnPage("App configurations");
        WaitViewHelper.waitOptionalTextDisappear("Loading configurations...", 5000);
        return new ManageAppConfigModel();
    }
}
