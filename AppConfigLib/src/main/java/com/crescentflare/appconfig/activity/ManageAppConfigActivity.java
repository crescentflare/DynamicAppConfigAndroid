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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crescentflare.appconfig.helper.AppConfigResourceHelper;
import com.crescentflare.appconfig.helper.AppConfigViewHelper;
import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfig.model.AppConfigBaseModel;
import com.crescentflare.appconfig.model.AppConfigStorageItem;
import com.crescentflare.appconfig.view.AppConfigCellList;
import com.crescentflare.appconfig.view.AppConfigClickableCell;
import com.crescentflare.appconfig.view.AppConfigEditableCell;
import com.crescentflare.appconfig.view.AppConfigSimpleCell;
import com.crescentflare.appconfig.view.AppConfigSwitchCell;

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
    private static final int RESULT_CODE_SELECT_ENUM = 1004;


    // ---
    // Members
    // ---

    private ArrayList<View> fieldViews = new ArrayList<>();
    private LinearLayout layout = null;
    private AppConfigCellList managingView = null;
    private LinearLayout spinnerView = null;
    private AppConfigStorageItem initialEditValues = null;
    private AppConfigStorageItem latestEditValues = null;
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
                initialEditValues = fetchEditedValues();
                latestEditValues = initialEditValues;
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
        else if (requestCode >= RESULT_CODE_SELECT_ENUM && requestCode < RESULT_CODE_SELECT_ENUM + 1000 && resultCode == RESULT_OK)
        {
            String resultString = data.getStringExtra(AppConfigStringChoiceActivity.ARG_INTENT_RESULT_SELECTED_STRING);
            if (resultString.length() > 0)
            {
                int index = requestCode - RESULT_CODE_SELECT_ENUM;
                if (index < fieldViews.size() && fieldViews.get(index) instanceof AppConfigClickableCell)
                {
                    ((AppConfigClickableCell)fieldViews.get(index)).setText(fieldViews.get(index).getTag() + ": " + resultString);
                }
            }
        }
    }

    @Override
    public void finish()
    {
        boolean hasChange = false;
        if (initialEditValues != null)
        {
            hasChange = !fetchEditedValues().equals(initialEditValues);
        }
        if (hasChange)
        {
            saveGlobalData();
        }
        super.finish();
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
        if (initialEditValues != null)
        {
            latestEditValues = fetchEditedValues();
        }
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
    // View component generators
    // ---

    @Override
    public void onChangedConfig()
    {
        populateContent();
    }

    private int dp(int dp)
    {
        return AppConfigViewHelper.dp(dp);
    }

    private AppConfigSimpleCell generateInfoView(String infoLabel, String infoValue)
    {
        AppConfigSimpleCell cellView = new AppConfigSimpleCell(this);
        cellView.setText(infoLabel + ": " + infoValue);
        return cellView;
    }

    private AppConfigClickableCell generateButtonView(String action, boolean edited)
    {
        return generateButtonView(null, action, edited);
    }

    private AppConfigClickableCell generateButtonView(String label, String setting, boolean edited)
    {
        AppConfigClickableCell cellView = new AppConfigClickableCell(this);
        cellView.setTag(label);
        cellView.setText(setting);
        if (edited)
        {
            cellView.setValue(AppConfigResourceHelper.getString(this, "app_config_item_edited"));
        }
        return cellView;
    }

    private AppConfigEditableCell generateEditTextView(String label, String setting, boolean limitNumbers)
    {
        AppConfigEditableCell editView = new AppConfigEditableCell(this);
        editView.setDescription(label);
        editView.setValue(setting);
        editView.setTag(label);
        editView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                supportInvalidateOptionsMenu();
            }
        });
        editView.setNumberLimit(limitNumbers);
        return editView;
    }

    private AppConfigSwitchCell generateSwitchView(String label, boolean setting)
    {
        AppConfigSwitchCell switchView = new AppConfigSwitchCell(this);
        LinearLayout.LayoutParams switchViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchView.setLayoutParams(switchViewLayoutParams);
        switchView.setText(label);
        switchView.setChecked(setting);
        switchView.setTag(label);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                supportInvalidateOptionsMenu();
            }
        });
        return switchView;
    }


    // ---
    // Layout and content handling
    // ---

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

        // Add frame layout to contain the editing views or loading indicator
        FrameLayout container = new FrameLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_background"));
        layout.addView(container);

        // Add managing view for configuration selection and global settings editing
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        managingView = new AppConfigCellList(this);
        managingView.setVisibility(View.GONE);
        scrollView.addView(managingView);
        container.addView(scrollView);

        // Add spinner view for loading
        spinnerView = new LinearLayout(this);
        spinnerView.setBackgroundColor(Color.WHITE);
        spinnerView.setGravity(Gravity.CENTER);
        spinnerView.setOrientation(LinearLayout.VERTICAL);
        spinnerView.setPadding(dp(8), dp(8), dp(8), dp(8));
        container.addView(spinnerView);

        // Add progress bar to it (animated spinner)
        ProgressBar iconView = new ProgressBar(this);
        spinnerView.addView(iconView);

        // Add loading text to it
        TextView progressTextView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, dp(12), 0, 0);
        progressTextView.setLayoutParams(layoutParams);
        progressTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        progressTextView.setText(AppConfigResourceHelper.getString(this, "app_config_loading"));
        spinnerView.addView(progressTextView);

        // Add build number below loading text
        if (buildNr > 0)
        {
            TextView progressBuildView = new TextView(this);
            LinearLayout.LayoutParams buildLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buildLayoutParams.setMargins(0, dp(2), 0, 0);
            progressBuildView.setLayoutParams(buildLayoutParams);
            progressBuildView.setGravity(Gravity.CENTER_HORIZONTAL);
            progressBuildView.setText("(" + AppConfigResourceHelper.getString(this, "app_config_field_build").toLowerCase() + ": " + buildNr + ")");
            spinnerView.addView(progressBuildView);
        }
        return layout;
    }

    private void generateEditingContent(String category, ArrayList<String> values, AppConfigStorageItem config, AppConfigBaseModel baseModel)
    {
        // Start section
        String title = AppConfigResourceHelper.getString(this, "app_config_header_global_prefix");
        if (category != null)
        {
            if (category.length() > 0)
            {
                title += ": " + category;
            }
            else
            {
                title += ": " + AppConfigResourceHelper.getString(this, "app_config_header_edit_other");
            }
        }
        managingView.startSection(title);

        // Fetch objects and filter by category
        ArrayList<String> editValues = new ArrayList<>();
        ArrayList<Object> editObjects = new ArrayList<>();
        for (String value : values)
        {
            boolean belongsToCategory = true;
            if (category != null && baseModel != null)
            {
                belongsToCategory = baseModel.valueBelongsToCategory(value, category);
            }
            if (belongsToCategory && !value.equals("name"))
            {
                editValues.add(value);
                editObjects.add(baseModel != null ? baseModel.getCurrentValue(value) : config.get(value));
            }
        }

        // Add editing views
        for (int i = 0; i < editValues.size(); i++)
        {
            final String value = editValues.get(i);
            View layoutView = null;
            final Object result = editObjects.get(i);
            if (result != null)
            {
                if (result instanceof Boolean)
                {
                    layoutView = generateSwitchView(value, (Boolean)result);
                }
                else if (result.getClass().isEnum())
                {
                    final int index = fieldViews.size();
                    layoutView = generateButtonView(value, value + ": " + result.toString(), false);
                    layoutView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Object constants[] = result.getClass().getEnumConstants();
                            ArrayList<String> enumValues = new ArrayList<>();
                            for (int i = 0; i < constants.length; i++)
                            {
                                enumValues.add(constants[i].toString());
                            }
                            if (enumValues.size() > 0)
                            {
                                AppConfigStringChoiceActivity.startWithResult(
                                        ManageAppConfigActivity.this,
                                        AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_title_choose_enum_prefix") + " " + value,
                                        AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_header_choose_enum"),
                                        enumValues,
                                        RESULT_CODE_SELECT_ENUM + index
                                );
                            }
                        }
                    });
                }
                else if (result instanceof Integer || result instanceof Long)
                {
                    layoutView = generateEditTextView(value, "" + result, true);
                }
                else if (result instanceof String)
                {
                    layoutView = generateEditTextView(value, (String)result, false);
                }
                if (layoutView != null)
                {
                    managingView.addSectionItem(layoutView);
                    fieldViews.add(layoutView.findViewWithTag(value));
                }
            }
        }

        // End section
        managingView.endSection();
    }

    private void populateContent()
    {
        // Show/hide spinner depending on the config being loaded
        spinnerView.setVisibility(AppConfigStorage.instance.isLoaded() ? View.GONE : View.VISIBLE);
        managingView.setVisibility(AppConfigStorage.instance.isLoaded() ? View.VISIBLE : View.GONE);
        if (!AppConfigStorage.instance.isLoaded())
        {
            return;
        }

        // Clear all views to re-populate
        managingView.removeAllViews();
        fieldViews.clear();

        // Add last selected configuration (if present)
        ArrayList<String> configs = AppConfigStorage.instance.configList();
        if (configs.size() > 0)
        {
            // Start section
            managingView.startSection(AppConfigResourceHelper.getString(this, "app_config_header_list_last_selection"));

            // Determine last selection
            String buttonName;
            boolean isOverride = false;
            boolean hasLastSelection = false;
            if (AppConfigStorage.instance.getSelectedConfig() != null)
            {
                buttonName = AppConfigStorage.instance.getSelectedConfigName();
                isOverride = AppConfigStorage.instance.isConfigOverride(buttonName);
                hasLastSelection = true;
            }
            else
            {
                buttonName = AppConfigResourceHelper.getString(this, "app_config_item_none");
            }

            // Add button
            AppConfigClickableCell selectButton = generateButtonView(buttonName, isOverride);
            selectButton.setId(AppConfigResourceHelper.getIdentifier(this, "app_config_activity_manage_select_current"));
            managingView.addSectionItem(selectButton);
            selectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setResult(RESULT_OK);
                    finish();
                }
            });
            if (hasLastSelection)
            {
                final String configName = buttonName;
                selectButton.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        EditAppConfigActivity.startWithResult(ManageAppConfigActivity.this, configName, false, RESULT_CODE_EDIT_CONFIG);
                        return true;
                    }
                });
            }

            // End section
            managingView.endSection();
        }

        // Add list of configurations
        if (configs.size() > 0)
        {
            // Start section
            managingView.startSection(AppConfigResourceHelper.getString(this, "app_config_header_list"));

            // Add buttons
            for (final String configName : configs)
            {
                AppConfigClickableCell configButton = generateButtonView(configName, AppConfigStorage.instance.isConfigOverride(configName));
                configButton.setTag("config: " + configName);
                managingView.addSectionItem(configButton);
                configButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        AppConfigStorage.instance.selectConfig(ManageAppConfigActivity.this, configName);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                configButton.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        EditAppConfigActivity.startWithResult(ManageAppConfigActivity.this, configName, false, RESULT_CODE_EDIT_CONFIG);
                        return true;
                    }
                });
            }

            // End section
            managingView.endSection();
        }

        // Add area for custom configurations, and adding them
        if (configs.size() > 0)
        {
            // Start section
            managingView.startSection(AppConfigResourceHelper.getString(this, "app_config_header_list_custom"));

            // Add buttons
            ArrayList<String> customConfigs = AppConfigStorage.instance.customConfigList();
            for (final String configName : customConfigs)
            {
                if (AppConfigStorage.instance.isCustomConfig(configName))
                {
                    AppConfigClickableCell configButton = generateButtonView(configName, false);
                    configButton.setTag("config: " + configName);
                    managingView.addSectionItem(configButton);
                    configButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            AppConfigStorage.instance.selectConfig(ManageAppConfigActivity.this, configName);
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    configButton.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            EditAppConfigActivity.startWithResult(ManageAppConfigActivity.this, configName, false, RESULT_CODE_EDIT_CONFIG);
                            return true;
                        }
                    });
                }
            }

            // Add new custom config button
            AppConfigClickableCell newButton = generateButtonView(AppConfigResourceHelper.getString(this, "app_config_action_add"), false);
            newButton.setId(AppConfigResourceHelper.getIdentifier(this, "app_config_activity_manage_new_custom"));
            managingView.addSectionItem(newButton);
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ArrayList<String> configs = AppConfigStorage.instance.configList();
                    AppConfigStringChoiceActivity.startWithResult(ManageAppConfigActivity.this, AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_title_edit_new"), AppConfigResourceHelper.getString(ManageAppConfigActivity.this, "app_config_header_choose_custom_copy"), configs, RESULT_CODE_CUSTOM_COPY_FROM);
                }
            });

            // End section
            managingView.endSection();
        }

        // Determine global values and categories
        AppConfigStorageItem config = AppConfigStorage.instance.getGlobalConfig();
        if (latestEditValues != null)
        {
            config = latestEditValues;
        }
        ArrayList<String> values = config.valueList();
        ArrayList<String> categories = new ArrayList<>();
        AppConfigBaseModel baseModel = null;
        if (AppConfigStorage.instance.getConfigManager() != null)
        {
            baseModel = AppConfigStorage.instance.getConfigManager().getBaseModelInstance();
            baseModel.applyCustomSettings("Global", config);
            values = AppConfigStorage.instance.getConfigManager().getBaseModelInstance().globalValueList();
            categories = baseModel.getGlobalCategories();
        }

        // Add global editing fields to view (if present)
        if (values.size() > 0)
        {
            if (categories.size() > 0)
            {
                for (String category : categories)
                {
                    generateEditingContent(category, values, config, baseModel);
                }
            }
            else
            {
                generateEditingContent(null, values, config, baseModel);
            }
        }

        // Add build information
        managingView.startSection(AppConfigResourceHelper.getString(this, "app_config_header_list_build_info"));
        managingView.addSectionItem(generateInfoView(AppConfigResourceHelper.getString(this, "app_config_field_build"), "" + buildNr));
        managingView.addSectionItem(generateInfoView(AppConfigResourceHelper.getString(this, "app_config_field_api_level"), "" + Build.VERSION.SDK_INT));
        managingView.endSection();
    }


    // ---
    // Global configuration mutations
    // ---

    private AppConfigStorageItem fetchEditedValues()
    {
        AppConfigStorageItem item = new AppConfigStorageItem();
        for (View view : fieldViews)
        {
            if (view.getTag() == null)
            {
                break;
            }
            if (view instanceof AppConfigEditableCell)
            {
                if (((AppConfigEditableCell)view).isNumberLimit())
                {
                    long number = 0;
                    try
                    {
                        number = Long.parseLong(((AppConfigEditableCell)view).getValue());
                    }
                    catch (Exception ignored)
                    {
                    }
                    item.putLong((String)view.getTag(), number);
                }
                else
                {
                    item.putString((String)view.getTag(), ((AppConfigEditableCell)view).getValue());
                }
            }
            else if (view instanceof AppConfigSwitchCell)
            {
                item.putBoolean((String)view.getTag(), ((AppConfigSwitchCell)view).isChecked());
            }
            else if (view instanceof AppConfigClickableCell)
            {
                item.putString((String)view.getTag(), ((AppConfigClickableCell)view).getText().replace(view.getTag() + ": ", ""));
            }
        }
        return item;
    }

    private void saveGlobalData()
    {
        AppConfigStorageItem item = fetchEditedValues();
        AppConfigStorage.instance.updateGlobalConfig(ManageAppConfigActivity.this, item);
    }
}
