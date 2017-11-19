package com.crescentflare.appconfig.view;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crescentflare.appconfig.helper.AppConfigResourceHelper;
import com.crescentflare.appconfig.helper.AppConfigViewHelper;

/**
 * Library view: editable cell
 * Simulates a list view cell as an editable text
 */
public class AppConfigEditableCell extends FrameLayout
{
    // ---
    // Members
    // ---

    private TextView labelView;
    private AppCompatEditText editableView;
    private boolean isNumberLimit = false;


    // ---
    // Initialization
    // ---

    public AppConfigEditableCell(Context context)
    {
        super(context);
        init(context, null);
    }

    public AppConfigEditableCell(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public AppConfigEditableCell(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public AppConfigEditableCell(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        // Prepare container
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setPadding(dp(10), dp(12), dp(10), dp(12));
        addView(container);

        // Add label view
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(labelView = new TextView(context));
        labelView.setLayoutParams(layoutParams);
        labelView.setPadding(dp(4), 0, dp(4), 0);

        // Add editable view
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(editableView = new AppCompatEditText(context));
        editableView.setLayoutParams(layoutParams);
    }


    // ---
    // Modify view
    // ---

    public void setDescription(String text)
    {
        labelView.setText(text);
    }

    public void setValue(String value)
    {
        editableView.setText(value);
    }

    public String getValue()
    {
        return editableView.getText().toString();
    }

    public void addTextChangedListener(TextWatcher textWatcher)
    {
        editableView.addTextChangedListener(textWatcher);
    }

    public void setNumberLimit(boolean numbersOnly)
    {
        if (numbersOnly)
        {
            editableView.setInputType(InputType.TYPE_CLASS_NUMBER);
            editableView.setKeyListener(DigitsKeyListener.getInstance(true, false));
        }
        else
        {
            AppCompatEditText defaults = new AppCompatEditText(getContext());
            editableView.setInputType(defaults.getInputType());
            editableView.setKeyListener(defaults.getKeyListener());
        }
        isNumberLimit = numbersOnly;
    }

    public boolean isNumberLimit()
    {
        return isNumberLimit;
    }


    // ---
    // Helper
    // ---

    private int dp(int dp)
    {
        return AppConfigViewHelper.dp(dp);
    }
}
