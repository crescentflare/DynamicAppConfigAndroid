package com.crescentflare.appconfig.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.crescentflare.appconfig.adapter.AppConfigAdapter;
import com.crescentflare.appconfig.adapter.AppConfigAdapterEntry;
import com.crescentflare.appconfig.helper.AppConfigResourceHelper;
import com.crescentflare.appconfig.manager.AppConfigStorage;

import java.util.ArrayList;

/**
 * Library activity: managing configurations
 * Be able to select, add and edit app configurations
 */
public class ManageAppConfigActivity extends AppCompatActivity implements AppConfigStorage.ChangedConfigListener
{
    // ---
    // Constants
    // ---

    private static final int RESULT_CODE_CUSTOM_COPY_FROM = 1000;
    private static final int RESULT_CODE_EDIT_CONFIG = 1001;


    // ---
    // Members
    // ---

    private LinearLayout layout = null;
    private ListView listView = null;
    private LinearLayout spinnerView = null;
    private AppConfigAdapter adapter = null;
    private int buildNr = -1;


    // ---
    // Initialization
    // ---

    public static Intent newInstance(Context context)
    {
        return new Intent(context, ManageAppConfigActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Obtain build number
        super.onCreate(savedInstanceState);
        if (!AppConfigStorage.instance.isInitialized())
        {
            finish();
            return;
        }
        try
        {
            PackageManager manager = getPackageManager();
            if (manager != null)
            {
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                if (info != null)
                {
                    buildNr = info.versionCode;
                }
            }
        }
        catch (PackageManager.NameNotFoundException ignored)
        {
        }

        // Create layout and configure action bar
        layout = createContentView();
        setTitle(AppConfigResourceHelper.getString(this, "app_config_title_list"));
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setContentView(layout);

        // Load data and populate content
        AppConfigStorage.instance.loadFromSource(this, new Runnable()
        {
            @Override
            public void run()
            {
                populateContent();
            }
        });
    }

    public static void startWithResult(Activity fromActivity, int resultCode)
    {
        fromActivity.startActivityForResult(newInstance(fromActivity), resultCode);
    }


    // ---
    // State handling
    // ---

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_CUSTOM_COPY_FROM && resultCode == RESULT_OK)
        {
            String resultString = data.getStringExtra(AppConfigStringChoiceActivity.ARG_INTENT_RESULT_SELECTED_STRING);
            if (resultString.length() > 0)
            {
                EditAppConfigActivity.startWithResult(this, resultString, true, RESULT_CODE_EDIT_CONFIG);
            }
        }
        else if (requestCode == RESULT_CODE_EDIT_CONFIG && resultCode == RESULT_OK)
        {
            populateContent();
        }
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
        populateContent();
        AppConfigStorage.instance.addChangedConfigListener(this);
    }


    // ---
    // Menu handling
    // ---

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ---
    // Layout and content handling
    // ---

    @Override
    public void onChangedConfig()
    {
        populateContent();
    }

    private int dip(int pixels)
    {
        return (int)(getResources().getDisplayMetrics().density * pixels);
    }

    private LinearLayout createContentView()
    {
        // Create main layout
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a toolbar on top (if no action bar is present)
        if (getSupportActionBar() == null)
        {
            Toolbar bar = new Toolbar(this);
            layout.addView(bar, 0);
            setSupportActionBar(bar);
        }

        // Add frame layout to contain listview or loading indicator
        FrameLayout container = new FrameLayout(this);
        layout.addView(container);

        // Add listview for configurations
        listView = new ListView(this);
        adapter = new AppConfigAdapter(this);
        listView.setId(AppConfigResourceHelper.getIdentifier(this, "app_config_activity_manage_list"));
        listView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "api_config_background"));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);
        container.addView(listView);

        // Add spinner view for loading
        spinnerView = new LinearLayout(this);
        spinnerView.setBackgroundColor(Color.WHITE);
        spinnerView.setGravity(Gravity.CENTER);
        spinnerView.setOrientation(LinearLayout.VERTICAL);
        spinnerView.setPadding(dip(8), dip(8), dip(8), dip(8));
        container.addView(spinnerView);

        // Add progress bar to it (animated spinner)
        ProgressBar iconView = new ProgressBar(this);
        spinnerView.addView(iconView);

        // Add loading text to it
        TextView progressTextView = new TextView(this);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins(0, dip(8), 0, 0);
        progressTextView.setLayoutParams(textLayoutParams);
        progressTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        progressTextView.setText(AppConfigResourceHelper.getString(this, "app_config_loading"));
        spinnerView.addView(progressTextView);

        // Add build number below loading text
        if (buildNr > 0)
        {
            TextView progressBuildView = new TextView(this);
            LinearLayout.LayoutParams buildLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buildLayoutParams.setMargins(0, dip(2), 0, 0);
            progressBuildView.setLayoutParams(buildLayoutParams);
            progressBuildView.setGravity(Gravity.CENTER_HORIZONTAL);
            progressBuildView.setText("(" + AppConfigResourceHelper.getString(this, "app_config_field_build").toLowerCase() + ": " + buildNr + ")");
            spinnerView.addView(progressBuildView);
        }

        // List view click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AppConfigAdapterEntry entry = (AppConfigAdapterEntry)parent.getItemAtPosition(position);
                if (entry.getType() == AppConfigAdapterEntry.Type.Configuration)
                {
                    if (entry.getSection() == AppConfigAdapterEntry.Section.Add)
                    {
                        ArrayList<String> configs = AppConfigStorage.instance.configList();
                        AppConfigStringChoiceActivity.startWithResult(ManageAppConfigActivity.this, AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_title_edit_new"), AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_header_choose_custom_copy"), configs, RESULT_CODE_CUSTOM_COPY_FROM);
                    }
                    else
                    {
                        AppConfigStorage.instance.selectConfig(ManageAppConfigActivity.this, entry.getName());
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        });

        // List view long click handler (edit configuration)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                AppConfigAdapterEntry entry = (AppConfigAdapterEntry)parent.getItemAtPosition(position);
                if (entry.getType() == AppConfigAdapterEntry.Type.Configuration)
                {
                    if (entry.getSection() == AppConfigAdapterEntry.Section.Add)
                    {
                        ArrayList<String> configs = AppConfigStorage.instance.configList();
                        AppConfigStringChoiceActivity.startWithResult(ManageAppConfigActivity.this, AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_title_choose_custom_copy"), AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_header_choose_custom_copy"), configs, RESULT_CODE_CUSTOM_COPY_FROM);
                    }
                    else
                    {
                        EditAppConfigActivity.startWithResult(ManageAppConfigActivity.this, entry.getName(), false, RESULT_CODE_EDIT_CONFIG);
                    }
                    return true;
                }
                return false;
            }
        });
        return layout;
    }

    private void populateContent()
    {
        // Show/hide spinner depending on the config being loaded
        spinnerView.setVisibility(AppConfigStorage.instance.isLoaded() ? View.GONE : View.VISIBLE);
        listView.setVisibility(AppConfigStorage.instance.isLoaded() ? View.VISIBLE : View.GONE);
        if (!AppConfigStorage.instance.isLoaded())
        {
            return;
        }

        // Add last selection configuration (if present)
        ArrayList<AppConfigAdapterEntry> entries = new ArrayList<>();
        entries.add(AppConfigAdapterEntry.entryForHeader(AppConfigResourceHelper.getString(this, "app_config_header_list_last_selection")));
        if (AppConfigStorage.instance.getSelectedConfig() != null)
        {
            String configName = AppConfigStorage.instance.getSelectedConfigName();
            entries.add(AppConfigAdapterEntry.entryForConfiguration(AppConfigAdapterEntry.Section.LastSelected, configName, configName, AppConfigStorage.instance.isConfigOverride(configName)));
        }
        else
        {
            entries.add(AppConfigAdapterEntry.entryForConfiguration(AppConfigAdapterEntry.Section.LastSelected, "", AppConfigResourceHelper.getString(this, "app_config_item_none"), false));
        }

        // Add list of configurations
        ArrayList<String> configs = AppConfigStorage.instance.configList();
        if (configs.size() > 0)
        {
            entries.add(AppConfigAdapterEntry.entryForHeader(AppConfigResourceHelper.getString(this, "app_config_header_list")));
            for (String configName : configs)
            {
                entries.add(AppConfigAdapterEntry.entryForConfiguration(AppConfigAdapterEntry.Section.Predefined, configName, AppConfigStorage.instance.isConfigOverride(configName)));
            }
        }

        // Add area for custom configurations, and adding them
        ArrayList<String> customConfigs = AppConfigStorage.instance.customConfigList();
        entries.add(AppConfigAdapterEntry.entryForHeader(AppConfigResourceHelper.getString(this, "app_config_header_list_custom")));
        for (String configName : customConfigs)
        {
            if (AppConfigStorage.instance.isCustomConfig(configName))
            {
                entries.add(AppConfigAdapterEntry.entryForConfiguration(AppConfigAdapterEntry.Section.Custom, configName, false));
            }
        }
        entries.add(AppConfigAdapterEntry.entryForConfiguration(AppConfigAdapterEntry.Section.Add, "", AppConfigResourceHelper.getString(this, "app_config_action_add"), false));

        // Add build information
        entries.add(AppConfigAdapterEntry.entryForHeader(AppConfigResourceHelper.getString(this, "app_config_header_list_build_info")));
        entries.add(AppConfigAdapterEntry.entryForBuildInfo(AppConfigResourceHelper.getString(this, "app_config_field_build"), "" + buildNr));
        entries.add(AppConfigAdapterEntry.entryForBuildInfo(AppConfigResourceHelper.getString(this, "app_config_field_api_level"), "" + Build.VERSION.SDK_INT));
        entries.add(AppConfigAdapterEntry.entryForFooter());
        adapter.setEntries(entries);
    }
}
