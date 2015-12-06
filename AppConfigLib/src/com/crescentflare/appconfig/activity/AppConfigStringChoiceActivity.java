package com.crescentflare.appconfig.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crescentflare.appconfig.adapter.AppConfigChoiceAdapter;

import java.util.ArrayList;

/**
 * Library activity: selection activity
 * Select an item from a given set of strings, used when needed to make a choice out of a limited set of options
 */
public class AppConfigStringChoiceActivity extends AppCompatActivity
{
    /**
     * Constants
     */
    public static final String ARG_INTENT_RESULT_SELECTED_INDEX = "ARG_INTENT_RESULT_SELECTED_INDEX";
    public static final String ARG_INTENT_RESULT_SELECTED_STRING = "ARG_INTENT_RESULT_SELECTED_STRING";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_SELECTION_TITLE = "ARG_SELECTION_TITLE";
    private static final String ARG_CHOICES = "ARG_CHOICES";
    private static final int SECTION_DIVIDER_COLOR = 0xFFB0B0B0;
    private static final int SECTION_DIVIDER_GRADIENT_START = 0xFFC0C0C0;
    private static final int SECTION_DIVIDER_GRADIENT_END = 0xFFE8E8E8;

    /**
     * Members
     */
    private ListView listView = null;
    private AppConfigChoiceAdapter adapter = null;


    /**
     * Initialization
     */
    public static Intent newInstance(Context context, String title, String selectionTitle, ArrayList<String> options)
    {
        Intent intent = new Intent(context, AppConfigStringChoiceActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_SELECTION_TITLE, selectionTitle);
        intent.putExtra(ARG_CHOICES, options);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Add listview as content view
        super.onCreate(savedInstanceState);
        listView = new ListViewCompat(this);
        listView.setBackgroundColor(SECTION_DIVIDER_GRADIENT_END);
        setTitle(getIntent().getStringExtra(ARG_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(listView);

        //Add header and footer
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.addHeaderView(generateHeader());
        listView.addFooterView(generateFooter());

        //Set adapter
        adapter = new AppConfigChoiceAdapter(this);
        listView.setAdapter(adapter);
        adapter.setChoices(getIntent().getStringArrayListExtra(ARG_CHOICES));

        //Listview click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (parent.getItemAtPosition(position) != null)
                {
                    Intent intent = AppConfigStringChoiceActivity.this.getIntent();
                    intent.putExtra(ARG_INTENT_RESULT_SELECTED_INDEX, position);
                    intent.putExtra(ARG_INTENT_RESULT_SELECTED_STRING, (String) parent.getItemAtPosition(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    public static void startWithResult(Activity fromActivity, String title, String selectionTitle, ArrayList<String> options, int resultCode)
    {
        fromActivity.startActivityForResult(newInstance(fromActivity, title, selectionTitle, options), resultCode);
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
     * View handling
     */
    private int dip(int pixels)
    {
        return (int)(getResources().getDisplayMetrics().density * pixels);
    }

    private int getAccentColor()
    {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    private View generateHeader()
    {
        LinearLayout createdView = new LinearLayout(this);
        TextView labelView = new TextView(this);
        createdView.setOrientation(LinearLayout.VERTICAL);
        createdView.setBackgroundColor(Color.WHITE);
        createdView.addView(labelView);
        labelView.setPadding(dip(12), dip(12), dip(12), dip(12));
        labelView.setTypeface(Typeface.DEFAULT_BOLD);
        labelView.setTextColor(getAccentColor());
        labelView.setText(getIntent().getStringExtra(ARG_SELECTION_TITLE));
        return createdView;
    }

    private View generateFooter()
    {
        //Create container
        LinearLayout dividerLayout = new LinearLayout(this);
        dividerLayout.setOrientation(LinearLayout.VERTICAL);

        //Top line divider (edge)
        View topLineView = new View(this);
        topLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        topLineView.setBackgroundColor(SECTION_DIVIDER_COLOR);
        dividerLayout.addView(topLineView);

        //Middle divider (gradient on background)
        View gradientView = new View(this);
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ SECTION_DIVIDER_GRADIENT_START, SECTION_DIVIDER_GRADIENT_END, SECTION_DIVIDER_GRADIENT_END });
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

        //Return created view
        return dividerLayout;
    }
}
