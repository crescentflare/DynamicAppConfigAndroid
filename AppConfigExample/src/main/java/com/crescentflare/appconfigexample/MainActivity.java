package com.crescentflare.appconfigexample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crescentflare.appconfig.activity.ManageAppConfigActivity;

/**
 * The example activity shows a simple screen with a message
 */
public class MainActivity extends AppCompatActivity
{
    /**
     * Constants
     */
    private static final int RESULT_CODE_MANAGE_APP_CONFIG = 1001;

    /**
     * Initialization
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ManageAppConfigActivity.startWithResult(this, RESULT_CODE_MANAGE_APP_CONFIG);
    }

    /**
     * Activity state handling
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
