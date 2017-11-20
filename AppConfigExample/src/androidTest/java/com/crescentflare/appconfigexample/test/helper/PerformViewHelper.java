package com.crescentflare.appconfigexample.test.helper;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ListView;

import com.crescentflare.appconfig.view.AppConfigEditableCell;
import com.crescentflare.appconfig.view.AppConfigSwitchCell;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test helper: perform view
 * Utilities to easily activate view actions
 */
public class PerformViewHelper
{
    // ---
    // Utility functions
    // ---

    public static void disableLongClick(int viewId)
    {
        onView(withId(viewId)).perform(disableListViewLongClick());
    }


    // ---
    // View action to force a cell text setting
    // ---

    public static ViewAction setCellText(final String text)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return ViewMatchers.isAssignableFrom(AppConfigEditableCell.class);
            }

            @Override
            public String getDescription()
            {
                return "Set cell text to: " + text;
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                AppConfigEditableCell editView = (AppConfigEditableCell)view;
                editView.setValue(text);
            }
        };
    }


    // ---
    // View action to force a cell switch setting
    // ---

    public static ViewAction setCellSwitch(final boolean enabled)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return ViewMatchers.isAssignableFrom(AppConfigSwitchCell.class);
            }

            @Override
            public String getDescription()
            {
                return "Set cell switch to: " + enabled;
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                AppConfigSwitchCell switchView = (AppConfigSwitchCell)view;
                switchView.setChecked(enabled);
            }
        };
    }


    // ---
    // View action: disable long click (workaround for list views supporting long click which is triggered, even when not intended)
    // ---

    private static ViewAction disableListViewLongClick()
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return ViewMatchers.isAssignableFrom(ListView.class);
            }

            @Override
            public String getDescription()
            {
                return "Disable long click on listview (workaround)";
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                ((ListView)view).setOnItemLongClickListener(null);
            }
        };
    }
}
