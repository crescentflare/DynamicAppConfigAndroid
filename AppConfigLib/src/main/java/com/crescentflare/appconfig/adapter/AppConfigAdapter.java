package com.crescentflare.appconfig.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.crescentflare.appconfig.helper.AppConfigResourceHelper;

import java.util.ArrayList;

/**
 * Library adapter: config list adapter
 * List view adapter for the configuration list
 */
public class AppConfigAdapter extends BaseAdapter implements ListAdapter
{
    /**
     * Members
     */
    private Context context;
    private ArrayList<AppConfigAdapterEntry> entries = new ArrayList<>();


    /**
     * Initialization
     */
    public AppConfigAdapter(Context context)
    {
        this.context = context;
    }

    /**
     * Enabled check
     */
    @Override
    public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override
    public boolean isEnabled(int i)
    {
        return entries.get(i).getType() == AppConfigAdapterEntry.Type.Configuration && (entries.get(i).getName().length() > 0 || entries.get(i).getSection() == AppConfigAdapterEntry.Section.Add);
    }

    /**
     * Item handling
     */
    @Override
    public int getCount()
    {
        return entries.size();
    }

    @Override
    public Object getItem(int i)
    {
        return entries.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public int getItemViewType(int position)
    {
        int index = 0;
        for (AppConfigAdapterEntry.Type type : AppConfigAdapterEntry.Type.values())
        {
            if (type == entries.get(position).getType())
            {
                return index;
            }
            index++;
        }
        return index;
    }

    @Override
    public int getViewTypeCount()
    {
        return AppConfigAdapterEntry.Type.values().length;
    }

    /**
     * View handling
     */
    private int dip(int pixels)
    {
        return (int)(context.getResources().getDisplayMetrics().density * pixels);
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
                    AppConfigResourceHelper.getColor(context, "app_config_background"),
                    AppConfigResourceHelper.getColor(context, "app_config_background"),
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
            stateDrawable.addState(new int[]{  android.R.attr.state_focused }, new ColorDrawable(AppConfigResourceHelper.getColor(context, "app_config_background")));
            stateDrawable.addState(new int[]{  android.R.attr.state_pressed }, new ColorDrawable(AppConfigResourceHelper.getColor(context, "app_config_background")));
            stateDrawable.addState(new int[]{  android.R.attr.state_enabled }, new ColorDrawable(Color.WHITE));
            stateDrawable.addState(new int[]{ -android.R.attr.state_enabled }, new ColorDrawable(Color.WHITE));
            drawable = stateDrawable;
        }
        return drawable;
    }

    private View generateSectionDivider(boolean includeBottomDivider)
    {
        //Create container
        LinearLayout dividerLayout = new LinearLayout(context);
        dividerLayout.setOrientation(LinearLayout.VERTICAL);

        //Top line divider (edge)
        View topLineView = new View(context);
        topLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        topLineView.setBackgroundColor(AppConfigResourceHelper.getColor(context, "app_config_section_divider_line"));
        dividerLayout.addView(topLineView);

        //Middle divider (gradient on background)
        View gradientView = new View(context);
        int colors[] = new int[]
        {
                AppConfigResourceHelper.getColor(context, "app_config_section_gradient_start"),
                AppConfigResourceHelper.getColor(context, "app_config_section_gradient_end"),
                AppConfigResourceHelper.getColor(context, "app_config_background")
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
            View bottomLineView = new View(context);
            bottomLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            bottomLineView.setBackgroundColor(AppConfigResourceHelper.getColor(context, "app_config_section_divider_line"));
            dividerLayout.addView(bottomLineView);
        }

        //Return created view
        return dividerLayout;
    }

    private View generateViewForType(AppConfigAdapterEntry.Type type)
    {
        ViewHolder viewHolder = new ViewHolder();
        LinearLayout createdView = null;
        switch (type)
        {
            case Configuration:
                createdView = new LinearLayout(context);
                createdView.setOrientation(LinearLayout.VERTICAL);
                createdView.setBackgroundColor(Color.WHITE);
                createdView.addView(viewHolder.labelView = new TextView(context));
                createdView.addView(viewHolder.dividerView = new View(context));
                viewHolder.labelView.setGravity(Gravity.CENTER_VERTICAL);
                viewHolder.labelView.setMinimumHeight(dip(60));
                viewHolder.labelView.setPadding(dip(12), dip(12), dip(12), dip(12));
                viewHolder.labelView.setTextSize(18);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {
                    viewHolder.labelView.setBackgroundDrawable(generateSelectionBackgroundDrawable());
                }
                else
                {
                    viewHolder.labelView.setBackground(generateSelectionBackgroundDrawable());
                }
                viewHolder.dividerView.setBackgroundColor(AppConfigResourceHelper.getColor(context, "app_config_list_divider_line"));
                viewHolder.dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                ((LinearLayout.LayoutParams)viewHolder.dividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
                break;
            case BuildInfo:
                createdView = new LinearLayout(context);
                createdView.setOrientation(LinearLayout.VERTICAL);
                createdView.setBackgroundColor(Color.WHITE);
                createdView.addView(viewHolder.labelView = new TextView(context));
                createdView.addView(viewHolder.dividerView = new View(context));
                viewHolder.labelView.setPadding(dip(12), dip(12), dip(12), dip(12));
                viewHolder.dividerView.setBackgroundColor(AppConfigResourceHelper.getColor(context, "app_config_list_divider_line"));
                viewHolder.dividerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                ((LinearLayout.LayoutParams)viewHolder.dividerView.getLayoutParams()).setMargins(dip(12), 0, 0, 0);
                break;
            case Header:
                createdView = new LinearLayout(context);
                createdView.setOrientation(LinearLayout.VERTICAL);
                createdView.setBackgroundColor(Color.WHITE);
                createdView.addView(viewHolder.dividerView = generateSectionDivider(true));
                createdView.addView(viewHolder.labelView = new TextView(context));
                viewHolder.labelView.setPadding(dip(12), dip(12), dip(12), dip(12));
                viewHolder.labelView.setTypeface(Typeface.DEFAULT_BOLD);
                viewHolder.labelView.setTextColor(AppConfigResourceHelper.getAccentColor(context));
                break;
            case Footer:
                createdView = new LinearLayout(context);
                createdView.setOrientation(LinearLayout.VERTICAL);
                createdView.setBackgroundColor(Color.WHITE);
                createdView.addView(viewHolder.dividerView = generateSectionDivider(false));
                break;
            default:
                return null;
        }
        createdView.setTag(viewHolder);
        return createdView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        AppConfigAdapterEntry entry = (AppConfigAdapterEntry)getItem(i);
        if (view == null)
        {
            view = generateViewForType(entry.getType());
        }
        if (view != null)
        {
            ViewHolder viewHolder = (ViewHolder)view.getTag();
            if (viewHolder.labelView != null)
            {
                viewHolder.labelView.setText(entry.getLabel());
            }
            if (viewHolder.dividerView != null)
            {
                if (entry.getType() == AppConfigAdapterEntry.Type.Header)
                {
                    AppConfigAdapterEntry prevEntry = null;
                    if (i > 0)
                    {
                        prevEntry = (AppConfigAdapterEntry)getItem(i - 1);
                    }
                    viewHolder.dividerView.setVisibility(prevEntry == null ? View.GONE : View.VISIBLE);
                }
                else if (entry.getType() == AppConfigAdapterEntry.Type.Configuration || entry.getType() == AppConfigAdapterEntry.Type.BuildInfo)
                {
                    AppConfigAdapterEntry nextEntry = null;
                    if (i < entries.size() - 1)
                    {
                        nextEntry = (AppConfigAdapterEntry)getItem(i + 1);
                    }
                    viewHolder.dividerView.setVisibility(nextEntry != null && nextEntry.getType() == entry.getType() ? View.VISIBLE : View.GONE);
                }
            }
        }
        return view;
    }

    /**
     * Update list and notify data change
     */
    public void setEntries(ArrayList<AppConfigAdapterEntry> entries)
    {
        this.entries = entries;
        notifyDataSetChanged();
    }

    /**
     * List view tag to easily access subviews
     */
    public static class ViewHolder
    {
        public TextView labelView = null;
        public View dividerView = null;
    }
}
