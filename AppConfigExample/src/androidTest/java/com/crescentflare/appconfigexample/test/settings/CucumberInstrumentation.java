package com.crescentflare.appconfigexample.test.settings;

import android.os.Bundle;
import android.support.test.runner.MonitoringInstrumentation;

import cucumber.api.CucumberOptions;
import cucumber.api.android.CucumberInstrumentationCore;

/**
 * Test helper: is able to run BDD-type scripts
 */
@CucumberOptions(features = "features", glue = "com.crescentflare.appconfigexample.test.page", tags = "@all")
public class CucumberInstrumentation extends MonitoringInstrumentation
{
    private final CucumberInstrumentationCore instrumentationCore = new CucumberInstrumentationCore(this);

    @Override
    public void onCreate(Bundle arguments)
    {
        super.onCreate(arguments);
        instrumentationCore.create(arguments);
        start();
    }

    public void onStart()
    {
        super.onStart();
        waitForIdleSync();
        instrumentationCore.start();
    }
}