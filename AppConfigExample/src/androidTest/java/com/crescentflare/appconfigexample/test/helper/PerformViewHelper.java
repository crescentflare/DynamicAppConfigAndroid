package com.crescentflare.appconfigexample.test.helper;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test helper: perform view
 * Utilities to easily activate view actions
 */
public class PerformViewHelper
{
    /**
     * Utility functions
     */
    public static void disableLongClick(int viewId)
    {
        onView(withId(viewId)).perform(disableListViewLongClick());
    }

    /**
     * View action to force a switch setting
     */
    public static ViewAction setSwitch(final boolean enabled)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return ViewMatchers.isAssignableFrom(SwitchCompat.class);
            }

            @Override
            public String getDescription()
            {
                return "Set switch to: " + enabled;
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                SwitchCompat switchView = (SwitchCompat)view;
                switchView.setChecked(enabled);
            }
        };
    }

    /**
     * View action: disable long click (workaround for list views supporting long click which is triggered, even when not intended)
     */
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
