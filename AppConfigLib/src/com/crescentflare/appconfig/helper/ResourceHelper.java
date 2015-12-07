package com.crescentflare.appconfig.helper;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;

import com.crescentflare.appconfig.model.AppConfigBaseModel;
import com.crescentflare.appconfig.model.AppConfigStorageItem;

/**
 * Library helper: resource access
 * A helper library to access app resources for skinning the user interface
 */
public class ResourceHelper
{
    static public int getAccentColor(Context context)
    {
        int identifier = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        if (identifier == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            identifier = android.R.attr.colorAccent;
        }
        if (identifier > 0)
        {
            TypedValue typedValue = new TypedValue();
            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{identifier});
            if (a != null)
            {
                int color = a.getColor(0, 0);
                a.recycle();
                return color;
            }
        }
        return Color.BLACK;
    }
}
