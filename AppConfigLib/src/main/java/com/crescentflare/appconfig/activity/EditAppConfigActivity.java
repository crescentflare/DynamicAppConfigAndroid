package com.crescentflare.appconfig.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crescentflare.appconfig.helper.AppConfigResourceHelper;
import com.crescentflare.appconfig.manager.AppConfigStorage;
import com.crescentflare.appconfig.model.AppConfigBaseModel;
import com.crescentflare.appconfig.model.AppConfigStorageItem;

import java.util.ArrayList;

/**
 * Library activity: editing activity
 * Be able to change or create a new configuration copy
 */
public class EditAppConfigActivity extends AppCompatActivity
{
    /**
     * Constants
     */
    private static final String ARG_CONFIG_NAME = "ARG_CONFIG_NAME";
    private static final String ARG_CREATE_CUSTOM = "ARG_CREATE_CUSTOM";
    private static final int RESULT_CODE_SELECT_ENUM = 1004;

    /**
     * Members
     */
    private ArrayList<View> fieldViews = new ArrayList<>();
    private FrameLayout layout = null;
    private LinearLayout editingView = null;
    private LinearLayout spinnerView = null;


    /**
     * Initialization
     */
    public static Intent newInstance(Context context, String config, boolean createCustom)
    {
        Intent intent = new Intent(context, EditAppConfigActivity.class);
        intent.putExtra(ARG_CONFIG_NAME, config);
        intent.putExtra(ARG_CREATE_CUSTOM, createCustom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Create layout and configure action bar
        super.onCreate(savedInstanceState);
        layout = createContentView();
        setTitle(AppConfigResourceHelper.getString(this, getIntent().getBooleanExtra(ARG_CREATE_CUSTOM, false) ? "app_config_title_edit_new" : "app_config_title_edit"));
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

    public static void startWithResult(Activity fromActivity, String config, boolean createCustom, int resultCode)
    {
        fromActivity.startActivityForResult(newInstance(fromActivity, config, createCustom), resultCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode >= RESULT_CODE_SELECT_ENUM && requestCode < RESULT_CODE_SELECT_ENUM + 1000 && resultCode == RESULT_OK)
        {
            String resultString = data.getStringExtra(AppConfigStringChoiceActivity.ARG_INTENT_RESULT_SELECTED_STRING);
            if (resultString.length() > 0)
            {
                ArrayList<String> modelValues = new ArrayList<>();
                int index = requestCode - RESULT_CODE_SELECT_ENUM;
                if (AppConfigStorage.instance.getConfigManager() != null)
                {
                    modelValues = AppConfigStorage.instance.getConfigManager().getBaseModelInstance().valueList();
                }
                if (index < modelValues.size())
                {
                    TextView foundView = null;
                    for (View view : fieldViews)
                    {
                        if (view instanceof TextView)
                        {
                            if (view.getTag().equals(modelValues.get(index)))
                            {
                                foundView = (TextView)view;
                                break;
                            }
                        }
                    }
                    if (foundView != null)
                    {
                        foundView.setText(modelValues.get(index) + ": " + resultString);
                    }
                }
            }
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

    private Drawable generateSelectionBackgroundDrawable()
    {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //Set up color state list
            int[][] states = new int[][]
            {
                    new int[] {  android.R.attr.state_focused }, // focused
                    new int[] {  android.R.attr.state_pressed }, // pressed
                    new int[] {  android.R.attr.state_enabled }, // enabled
                    new int[] { -android.R.attr.state_enabled }  // disabled
            };
            int[] colors = new int[]
            {
                    AppConfigResourceHelper.getColor(this, "app_config_background"),
                    AppConfigResourceHelper.getColor(this, "app_config_background"),
                    Color.WHITE,
                    Color.WHITE
            };

            //And create ripple drawable effect
            RippleDrawable rippleDrawable = new RippleDrawable(new ColorStateList(states, colors), null, null);
            drawable = rippleDrawable;
        }
        else
        {
            StateListDrawable stateDrawable = new StateListDrawable();
            stateDrawable.addState(new int[]{  android.R.attr.state_focused }, new ColorDrawable(AppConfigResourceHelper.getColor(this, "app_config_background")));
            stateDrawable.addState(new int[]{  android.R.attr.state_pressed }, new ColorDrawable(AppConfigResourceHelper.getColor(this, "app_config_background")));
            stateDrawable.addState(new int[]{  android.R.attr.state_enabled }, new ColorDrawable(Color.WHITE));
            stateDrawable.addState(new int[]{ -android.R.attr.state_enabled }, new ColorDrawable(Color.WHITE));
            drawable = stateDrawable;
        }
        return drawable;
    }

    private View generateSectionDivider(boolean includeBottomDivider)
    {
        //Create container
        LinearLayout dividerLayout = new LinearLayout(this);
        dividerLayout.setOrientation(LinearLayout.VERTICAL);

        //Top line divider (edge)
        View topLineView = new View(this);
        topLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        topLineView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_section_divider_line"));
        dividerLayout.addView(topLineView);

        //Middle divider (gradient on background)
        View gradientView = new View(this);
        int colors[] = new int[]
        {
                AppConfigResourceHelper.getColor(this, "app_config_section_gradient_start"),
                AppConfigResourceHelper.getColor(this, "app_config_section_gradient_end"),
                AppConfigResourceHelper.getColor(this, "app_config_background")
        };
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gradientView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip(8)));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            gradientView.setBackgroundDrawable(drawable);
        }
        else
        {
            gradientView.setBackground(drawable);
        }
        dividerLayout.addView(gradientView);

        //Bottom line divider (edge)
        if (includeBottomDivider)
        {
            View bottomLineView = new View(this);
            bottomLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            bottomLineView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_section_divider_line"));
            dividerLayout.addView(bottomLineView);
        }

        //Return created view
        return dividerLayout;
    }

    private LinearLayout generateHeaderView(String label)
    {
        LinearLayout createdView = new LinearLayout(this);
        TextView labelView;
        createdView.setOrientation(LinearLayout.VERTICAL);
        createdView.setBackgroundColor(Color.WHITE);
        createdView.addView(labelView = new TextView(this));
        labelView.setPadding(dip(12), dip(12), dip(12), dip(12));
        labelView.setTypeface(Typeface.DEFAULT_BOLD);
        labelView.setTextColor(AppConfigResourceHelper.getAccentColor(this));
        labelView.setText(label);
        return createdView;
    }

    private LinearLayout generateButtonView(String action, boolean addDivider)
    {
        return generateButtonView(null, action, addDivider, false);
    }

    private LinearLayout generateButtonView(String label, String setting, boolean addDivider, boolean addTopDivider)
    {
        LinearLayout createdView = new LinearLayout(this);
        TextView labelView;
        View dividerView;
        createdView.setOrientation(LinearLayout.VERTICAL);
        createdView.setBackgroundColor(Color.WHITE);
        if (addTopDivider)
        {
            View topDividerView = null;
            createdView.addView(topDividerView = new View(this));
            topDividerView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_list_divider_line"));
            topDividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ((LinearLayout.LayoutParams)topDividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
        }
        createdView.addView(labelView = new TextView(this));
        labelView.setGravity(Gravity.CENTER_VERTICAL);
        labelView.setMinimumHeight(dip(60));
        labelView.setPadding(dip(12), dip(12), dip(12), dip(12));
        labelView.setTextSize(18);
        labelView.setTag(label);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            labelView.setBackgroundDrawable(generateSelectionBackgroundDrawable());
        }
        else
        {
            labelView.setBackground(generateSelectionBackgroundDrawable());
        }
        labelView.setText(setting);
        if (addDivider)
        {
            createdView.addView(dividerView = new View(this));
            dividerView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_list_divider_line"));
            dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ((LinearLayout.LayoutParams)dividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
        }
        return createdView;
    }

    private LinearLayout generateEditTextView(String label, String setting, boolean limitNumbers, boolean addDivider)
    {
        LinearLayout createdView = new LinearLayout(this);
        TextView labelView;
        AppCompatEditText editView;
        View dividerView;
        LinearLayout.LayoutParams editViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createdView.setOrientation(LinearLayout.VERTICAL);
        createdView.setPadding(0, dip(10), 0, dip(10));
        createdView.addView(labelView = new TextView(this));
        createdView.addView(editView = new AppCompatEditText(this));
        labelView.setPadding(dip(12), 0, dip(12), 0);
        labelView.setText(label);
        editViewLayoutParams.setMargins(dip(8), dip(0), dip(8), 0);
        editView.setLayoutParams(editViewLayoutParams);
        editView.setText(setting);
        editView.setTag(label);
        if (limitNumbers)
        {
            editView.setInputType(InputType.TYPE_CLASS_NUMBER);
            editView.setKeyListener(DigitsKeyListener.getInstance(true, false));
        }
        if (addDivider && false) //Don't make dividers for this type of view
        {
            createdView.addView(dividerView = new View(this));
            dividerView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_list_divider_line"));
            dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ((LinearLayout.LayoutParams)dividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
        }
        return createdView;
    }

    private LinearLayout generateSwitchView(String label, boolean setting, boolean addDivider, boolean addTopDivider)
    {
        LinearLayout createdView = new LinearLayout(this);
        SwitchCompat switchView;
        View dividerView;
        LinearLayout.LayoutParams switchViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createdView.setOrientation(LinearLayout.VERTICAL);
        if (addTopDivider)
        {
            View topDividerView = null;
            createdView.addView(topDividerView = new View(this));
            topDividerView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_list_divider_line"));
            topDividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ((LinearLayout.LayoutParams)topDividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
        }
        createdView.addView(switchView = new SwitchCompat(this));
        switchView.setPadding(dip(12), dip(20), dip(12), dip(20));
        switchView.setLayoutParams(switchViewLayoutParams);
        switchView.setTextSize(18);
        switchView.setText(label);
        switchView.setChecked(setting);
        switchView.setTag(label);
        if (addDivider)
        {
            createdView.addView(dividerView = new View(this));
            dividerView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_list_divider_line"));
            dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            ((LinearLayout.LayoutParams)dividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
        }
        return createdView;
    }

    private FrameLayout createContentView()
    {
        //Create main layout
        layout = new FrameLayout(this);

        //Add editing view for changing configuration
        ScrollView scrollView = new ScrollView(this);
        editingView = new LinearLayout(this);
        editingView.setOrientation(LinearLayout.VERTICAL);
        editingView.setBackgroundColor(AppConfigResourceHelper.getColor(this, "app_config_background"));
        editingView.setVisibility(View.GONE);
        scrollView.addView(editingView);
        layout.addView(scrollView);

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
        TextView progressTextView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, dip(12), 0, 0);
        progressTextView.setLayoutParams(layoutParams);
        progressTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        progressTextView.setText(AppConfigResourceHelper.getString(this, "app_config_loading"));
        spinnerView.addView(progressTextView);
        return layout;
    }

    private void populateContent()
    {
        //Enable content view (and remove all existing content), hide spinner
        spinnerView.setVisibility(View.GONE);
        editingView.setVisibility(View.VISIBLE);
        editingView.removeAllViews();
        fieldViews.clear();

        //Create layout containing editing views
        LinearLayout fieldEditLayout = new LinearLayout(this);
        fieldEditLayout.setOrientation(LinearLayout.VERTICAL);
        fieldEditLayout.setBackgroundColor(Color.WHITE);
        fieldEditLayout.addView(generateHeaderView(getIntent().getBooleanExtra(ARG_CREATE_CUSTOM, false) ? AppConfigResourceHelper.getString(this, "app_config_header_edit_new") : getIntent().getStringExtra(ARG_CONFIG_NAME)));
        editingView.addView(fieldEditLayout);
        editingView.addView(generateSectionDivider(true));

        //Create layout containing buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.setBackgroundColor(Color.WHITE);
        buttonLayout.addView(generateHeaderView(AppConfigResourceHelper.getString(this, "app_config_header_edit_actions")));
        editingView.addView(buttonLayout);
        editingView.addView(generateSectionDivider(false));

        //Add editing fields to view
        AppConfigStorageItem config = AppConfigStorage.instance.getConfigNotNull(getIntent().getStringExtra(ARG_CONFIG_NAME));
        ArrayList<String> values = config.valueList();
        ArrayList<String> modelValues = null;
        if (AppConfigStorage.instance.getConfigManager() != null)
        {
            modelValues = AppConfigStorage.instance.getConfigManager().getBaseModelInstance().valueList();
        }
        if (AppConfigStorage.instance.isCustomConfig(getIntent().getStringExtra(ARG_CONFIG_NAME)) || getIntent().getBooleanExtra(ARG_CREATE_CUSTOM, false))
        {
            String name = getIntent().getStringExtra(ARG_CONFIG_NAME);
            if (getIntent().getBooleanExtra(ARG_CREATE_CUSTOM, false))
            {
                name += " " + AppConfigResourceHelper.getString(this, "app_config_modifier_copy");
            }
            LinearLayout layoutView = generateEditTextView("name", name, false, values.size() > 0);
            fieldEditLayout.addView(layoutView);
            fieldViews.add(layoutView.findViewWithTag("name"));
        }
        if (modelValues != null)
        {
            AppConfigBaseModel baseModel = AppConfigStorage.instance.getConfigManager().getBaseModelInstance();
            Object saveResult = null;
            baseModel.applyCustomSettings(getIntent().getStringExtra(ARG_CONFIG_NAME), config);
            for (int i = 0; i < modelValues.size(); i++)
            {
                final String value = modelValues.get(i);
                if (value.equals("name"))
                {
                    continue;
                }
                LinearLayout layoutView = null;
                final Object result = baseModel.getDefaultValue(value);
                final Object previousResult = saveResult;
                if (result != null)
                {
                    if (result instanceof Boolean)
                    {
                        layoutView = generateSwitchView(value, (Boolean) result, i < modelValues.size() - 1, previousResult != null && previousResult instanceof String);
                    }
                    else if (result.getClass().isEnum())
                    {
                        final int index = i;
                        layoutView = generateButtonView(value, value + ": " + result.toString(), i < modelValues.size() - 1, previousResult != null && previousResult instanceof String);
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
                                            EditAppConfigActivity.this,
                                            AppConfigResourceHelper.getString(EditAppConfigActivity.this, "app_config_title_choose_enum_prefix") + " " + value,
                                            AppConfigResourceHelper.getString(EditAppConfigActivity.this, "app_config_header_choose_enum"),
                                            enumValues,
                                            RESULT_CODE_SELECT_ENUM + index
                                    );
                                }
                            }
                        });
                    }
                    else if (result instanceof Integer || result instanceof Long)
                    {
                        layoutView = generateEditTextView(value, "" + result, true, i < modelValues.size() - 1);
                    }
                    else if (result instanceof String)
                    {
                        layoutView = generateEditTextView(value, (String)result, false, i < modelValues.size() - 1);
                    }
                    if (layoutView != null)
                    {
                        fieldEditLayout.addView(layoutView);
                        fieldViews.add(layoutView.findViewWithTag(value));
                    }
                    saveResult = result;
                }
            }
        }
        else
        {
            boolean previousEditText = false;
            for (int i = 0; i < values.size(); i++)
            {
                String value = values.get(i);
                LinearLayout layoutView = null;
                Object rawValue = config.get(value);
                if (rawValue instanceof Boolean)
                {
                    layoutView = generateSwitchView(value, config.getBoolean(value), i < values.size() - 1, previousEditText);
                }
                else
                {
                    layoutView = generateEditTextView(value, config.getStringNotNull(value), rawValue instanceof Integer || rawValue instanceof Long, i < values.size() - 1);
                    previousEditText = true;
                }
                fieldEditLayout.addView(layoutView);
                fieldViews.add(layoutView.findViewWithTag(value));
            }
        }

        //Add buttons
        if (getIntent().getBooleanExtra(ARG_CREATE_CUSTOM, false))
        {
            LinearLayout createButton = generateButtonView(AppConfigResourceHelper.getString(this, "app_config_action_ok_edit_new"), true);
            buttonLayout.addView(createButton);
            createButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AppConfigStorageItem item = new AppConfigStorageItem();
                    String name = "";
                    for (View view : fieldViews)
                    {
                        if (view.getTag() == null)
                        {
                            break;
                        }
                        if (view.getTag().equals("name"))
                        {
                            if (view instanceof AppCompatEditText)
                            {
                                name = ((AppCompatEditText)view).getText().toString();
                            }
                        }
                        else
                        {
                            if (view instanceof AppCompatEditText)
                            {
                                if (((AppCompatEditText)view).getInputType() == InputType.TYPE_CLASS_NUMBER)
                                {
                                    long number = 0;
                                    try
                                    {
                                        number = Long.parseLong(((AppCompatEditText) view).getText().toString());
                                    }
                                    catch (Exception ignored)
                                    {
                                    }
                                    item.putLong((String) view.getTag(), number);
                                }
                                else
                                {
                                    item.putString((String)view.getTag(), ((AppCompatEditText)view).getText().toString());
                                }
                            }
                            else if (view instanceof SwitchCompat)
                            {
                                item.putBoolean((String) view.getTag(), ((SwitchCompat) view).isChecked());
                            }
                            else if (view instanceof TextView)
                            {
                                item.putString((String)view.getTag(), ((TextView)view).getText().toString());
                            }
                        }
                    }
                    if (name.length() > 0)
                    {
                        AppConfigStorage.instance.putCustomConfig(name, item);
                        AppConfigStorage.instance.synchronizeCustomConfigWithPreferences(EditAppConfigActivity.this, name);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });
        }
        else
        {
            //Updating configuration handler
            LinearLayout saveButton = generateButtonView(AppConfigResourceHelper.getString(this, "app_config_action_ok_edit"), true);
            buttonLayout.addView(saveButton);
            saveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AppConfigStorageItem item = new AppConfigStorageItem();
                    String name = getIntent().getStringExtra(ARG_CONFIG_NAME);
                    for (View view : fieldViews)
                    {
                        if (view.getTag() == null)
                        {
                            break;
                        }
                        if (view.getTag().equals("name"))
                        {
                            if (view instanceof AppCompatEditText)
                            {
                                name = ((AppCompatEditText)view).getText().toString();
                            }
                        }
                        else
                        {
                            if (view instanceof AppCompatEditText)
                            {
                                if (((AppCompatEditText)view).getInputType() == InputType.TYPE_CLASS_NUMBER)
                                {
                                    long number = 0;
                                    try
                                    {
                                        number = Long.parseLong(((AppCompatEditText) view).getText().toString());
                                    }
                                    catch (Exception ignored)
                                    {
                                    }
                                    item.putLong((String) view.getTag(), number);
                                }
                                else
                                {
                                    item.putString((String) view.getTag(), ((AppCompatEditText) view).getText().toString());
                                }
                            }
                            else if (view instanceof SwitchCompat)
                            {
                                item.putBoolean((String)view.getTag(), ((SwitchCompat) view).isChecked());
                            }
                            else if (view instanceof TextView)
                            {
                                item.putString((String)view.getTag(), ((TextView)view).getText().toString());
                            }
                        }
                    }
                    if (name.length() > 0)
                    {
                        if (AppConfigStorage.instance.isCustomConfig(name) || AppConfigStorage.instance.isConfigOverride(name))
                        {
                            AppConfigStorage.instance.removeConfig(getIntent().getStringExtra(ARG_CONFIG_NAME));
                        }
                        AppConfigStorage.instance.putCustomConfig(name, item);
                        AppConfigStorage.instance.synchronizeCustomConfigWithPreferences(EditAppConfigActivity.this, name);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });

            //Restore to defaults or delete handler
            String buttonText = AppConfigResourceHelper.getString(this, AppConfigStorage.instance.isCustomConfig(getIntent().getStringExtra(ARG_CONFIG_NAME)) ? "app_config_action_delete" : "app_config_action_restore");
            LinearLayout deleteButton = generateButtonView(buttonText, true);
            buttonLayout.addView(deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String configName = getIntent().getStringExtra(ARG_CONFIG_NAME);
                    if (AppConfigStorage.instance.isCustomConfig(configName) || AppConfigStorage.instance.isConfigOverride(configName))
                    {
                        AppConfigStorage.instance.removeConfig(configName);
                        AppConfigStorage.instance.synchronizeCustomConfigWithPreferences(EditAppConfigActivity.this, getIntent().getStringExtra(ARG_CONFIG_NAME));
                        setResult(RESULT_OK);
                    }
                    else
                    {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        }
        LinearLayout cancelButton = generateButtonView(AppConfigResourceHelper.getString(this, "app_config_action_cancel"), false);
        buttonLayout.addView(cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }
}
