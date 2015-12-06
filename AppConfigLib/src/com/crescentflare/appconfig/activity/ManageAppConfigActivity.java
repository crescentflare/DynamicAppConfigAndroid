package com.crescentflare.appconfig.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crescentflare.appconfig.adapter.AppConfigAdapter;
import com.crescentflare.appconfig.adapter.AppConfigAdapterEntry;
import com.crescentflare.appconfig.manager.AppConfigStorage;

import java.util.ArrayList;

/**
 * Library activity: managing configurations
 * Be able to select, add and edit app configurations
 */
public class ManageAppConfigActivity extends AppCompatActivity
{
    /**
     * Constants
     */
    private static final int BACKGROUND_COLOR = 0xFFE8E8E8;
    private static final int RESULT_CODE_CUSTOM_COPY_FROM = 1000;
    private static final int RESULT_CODE_EDIT_CONFIG = 1001;

    /**
     * Members
     */
    private FrameLayout layout = null;
    private ListView listView = null;
    private LinearLayout spinnerView = null;
    private AppConfigAdapter adapter = null;
    private int buildNr = -1;


    /**
     * Initialization
     */
    public static Intent newInstance(Context context)
    {
        return new Intent(context, ManageAppConfigActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Obtain build number
        super.onCreate(savedInstanceState);
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

        //Create layout and configure action bar
        layout = createContentView();
        setTitle("App configurations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(layout);

        //Load data and populate content
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

    /**
     * Menu handling
     */
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

    /**
     * Layout and content handling
     */
    private int dip(int pixels)
    {
        return (int)(getResources().getDisplayMetrics().density * pixels);
    }

    private FrameLayout createContentView()
    {
        //Create main layout
        layout = new FrameLayout(this);

        //Add listview for configurations
        listView = new ListViewCompat(this);
        adapter = new AppConfigAdapter(this);
        listView.setBackgroundColor(BACKGROUND_COLOR);
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);
        layout.addView(listView);

        //Add spinner view for loading
        spinnerView = new LinearLayout(this);
        spinnerView.setBackgroundColor(Color.WHITE);
        spinnerView.setGravity(Gravity.CENTER);
        spinnerView.setOrientation(LinearLayout.VERTICAL);
        spinnerView.setPadding(dip(8), dip(8), dip(8), dip(8));
        layout.addView(spinnerView);

        //Add progress bar to it (animated spinner)
        ProgressBar iconView = new ProgressBar(this);
        spinnerView.addView(iconView);

        //Add loading text to it
        String buildString = buildNr > 0 ? "\n(build: " + buildNr + ")" : "";
        TextView progressTextView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, dip(8), 0, 0);
        progressTextView.setLayoutParams(layoutParams);
        progressTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        progressTextView.setText("Loading configurations..." + buildString);
        spinnerView.addView(progressTextView);

        //Listview click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AppConfigAdapterEntry entry = (AppConfigAdapterEntry)parent.getItemAtPosition(position);
                if (entry.getType() == AppConfigAdapterEntry.Type.Configuration)
                {
                    if (entry.getName().length() > 0)
                    {
                        AppConfigStorage.instance.selectConfig(ManageAppConfigActivity.this, entry.getName());
                        setResult(RESULT_OK);
                        finish();
                    }
                    else
                    {
                        ArrayList<String> configs = AppConfigStorage.instance.configList();
                        AppConfigStringChoiceActivity.startWithResult(ManageAppConfigActivity.this, "New custom configuration", "Copy from...", configs, RESULT_CODE_CUSTOM_COPY_FROM);
                    }
                }
            }
        });

        //Listview long click handler (edit configuration)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                AppConfigAdapterEntry entry = (AppConfigAdapterEntry)parent.getItemAtPosition(position);
                if (entry.getType() == AppConfigAdapterEntry.Type.Configuration)
                {
                    if (entry.getName().length() > 0)
                    {
                        EditAppConfigActivity.startWithResult(ManageAppConfigActivity.this, entry.getName(), false, RESULT_CODE_EDIT_CONFIG);
                    }
                    else
                    {
                        ArrayList<String> configs = AppConfigStorage.instance.configList();
                        AppConfigStringChoiceActivity.startWithResult(ManageAppConfigActivity.this, "New custom configuration", "Copy from...", configs, RESULT_CODE_CUSTOM_COPY_FROM);
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
        //Enable listview, hide spinner
        spinnerView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        //Add list of configurations
        ArrayList<AppConfigAdapterEntry> entries = new ArrayList<>();
        ArrayList<String> configs = AppConfigStorage.instance.configList();
        if (configs.size() > 0)
        {
            entries.add(AppConfigAdapterEntry.entryForHeader("Predefined configurations"));
            if (AppConfigStorage.instance.getSelectedConfig() != null)
            {
                String configName = AppConfigStorage.instance.getSelectedConfigName();
                entries.add(AppConfigAdapterEntry.entryForConfiguration(configName, "Last selected: " + configName.substring(0, 1).toLowerCase() + configName.substring(1), AppConfigStorage.instance.isConfigOverride(configName)));
            }
            for (String configName : configs)
            {
                entries.add(AppConfigAdapterEntry.entryForConfiguration(configName, AppConfigStorage.instance.isConfigOverride(configName)));
            }
        }

        //Add area for custom configurations, and adding them
        ArrayList<String> customConfigs = AppConfigStorage.instance.customConfigList();
        entries.add(AppConfigAdapterEntry.entryForHeader("Custom configurations"));
        for (String configName : customConfigs)
        {
            if (AppConfigStorage.instance.isCustomConfig(configName))
            {
                entries.add(AppConfigAdapterEntry.entryForConfiguration(configName, true));
            }
        }
        entries.add(AppConfigAdapterEntry.entryForConfiguration("", "Add new...", false));

        //Add build information
        entries.add(AppConfigAdapterEntry.entryForHeader("Build information"));
        entries.add(AppConfigAdapterEntry.entryForBuildInfo("Build", "" + buildNr));
        entries.add(AppConfigAdapterEntry.entryForBuildInfo("API level", "" + Build.VERSION.SDK_INT));
        entries.add(AppConfigAdapterEntry.entryForFooter());
        adapter.setEntries(entries);
    }
}
