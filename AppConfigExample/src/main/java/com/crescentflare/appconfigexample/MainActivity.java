package com.crescentflare.appconfigexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.crescentflare.appconfig.activity.ManageAppConfigActivity;
import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfigexample.appconfig.ExampleAppConfigManager;

/**
 * The example activity shows a simple screen with a message
 */
public class MainActivity extends AppCompatActivity implements AppConfigStorage.ChangedConfigListener
{
    // ---
    // Constants
    // ---

    private static final int RESULT_CODE_MANAGE_APP_CONFIG = 1001;


    // ---
    // Initialization
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillContent();
        if (AppConfigStorage.instance.isInitialized() && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) > 0)
        {
            ManageAppConfigActivity.startWithResult(this, RESULT_CODE_MANAGE_APP_CONFIG);
        }
    }


    // ---
    // Activity state handling
    // ---

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fillContent();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        AppConfigStorage.instance.removeChangedConfigListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AppConfigStorage.instance.addChangedConfigListener(this);
        fillContent();
    }


    // ---
    // Fill content (show configuration values)
    // ---

    @Override
    public void onChangedConfig()
    {
        fillContent();
    }

    public void fillContent()
    {
        // Fetch text views
        TextView tvConfigName = (TextView)findViewById(R.id.activity_main_config_name);
        TextView tvConfigApiUrl = (TextView)findViewById(R.id.activity_main_config_api_url);
        TextView tvConfigRunType = (TextView)findViewById(R.id.activity_main_config_run_type);
        TextView tvConfigAcceptAllSSL = (TextView)findViewById(R.id.activity_main_config_accept_all_ssl);
        TextView tvConfigNetworkTimeout = (TextView)findViewById(R.id.activity_main_config_network_timeout_sec);
        TextView tvConfigConsoleUrl = (TextView)findViewById(R.id.activity_main_config_console_url);
        TextView tvConfigConsoleEnabled = (TextView)findViewById(R.id.activity_main_config_console_enabled);
        TextView tvConfigConsoleTimeout = (TextView)findViewById(R.id.activity_main_config_console_timeout_sec);
        TextView tvConfigLogLevel = (TextView)findViewById(R.id.activity_main_config_log_level);

        // Fill with config settings
        tvConfigName.setText(getString(R.string.prefix_config_name) + " " + ExampleAppConfigManager.currentConfig().getName());
        tvConfigApiUrl.setText(getString(R.string.prefix_config_api_url) + " " + ExampleAppConfigManager.currentConfig().getApiUrl());
        tvConfigRunType.setText(getString(R.string.prefix_config_run_type) + " " + ExampleAppConfigManager.currentConfig().getRunType().toString());
        tvConfigAcceptAllSSL.setText(getString(R.string.prefix_config_accept_all_ssl) + " " + (ExampleAppConfigManager.currentConfig().isAcceptAllSSL() ? "true" : "false"));
        tvConfigNetworkTimeout.setText(getString(R.string.prefix_config_network_timeout_sec) + " " + ExampleAppConfigManager.currentConfig().getNetworkTimeoutSec());
        tvConfigConsoleUrl.setText(getString(R.string.prefix_config_console_url) + " " + ExampleAppConfigManager.currentConfig().getConsoleUrl());
        tvConfigConsoleEnabled.setText(getString(R.string.prefix_config_console_enabled) + " " + (ExampleAppConfigManager.currentConfig().isConsoleEnabled() ? "true" : "false"));
        tvConfigConsoleTimeout.setText(getString(R.string.prefix_config_console_timeout_sec) + " " + ExampleAppConfigManager.currentConfig().getConsoleTimeoutSec());
        tvConfigLogLevel.setText(getString(R.string.prefix_config_log_level) + " " + ExampleAppConfigManager.currentConfig().getLogLevel().toString());
    }
}
