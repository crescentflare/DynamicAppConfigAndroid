package com.crescentflare.appconfigexample.appconfig;

/**
 * App config: application configuration log setting
 * An enum used by the application build configuration
 */
public enum ExampleAppConfigLogEnum
{
    LogDisabled("logDisabled"),
    LogNormal("logNormal"),
    LogVerbose("logVerbose");

    private String text;

    ExampleAppConfigLogEnum(String text)
    {
        this.text = text;
    }

    public static ExampleAppConfigLogEnum fromString(String text)
    {
        if (text != null)
            for (ExampleAppConfigLogEnum e : ExampleAppConfigLogEnum.values())
                if (text.equalsIgnoreCase(e.text))
                    return e;
        return LogDisabled;
    }

    public String toString()
    {
        return text;
    }
}
