package com.crescentflare.appconfigexample.appconfig;

/**
 * App config: application configuration run setting
 * An enum used by the application build configuration
 */
public enum ExampleAppConfigEnum
{
    RunNormally("runNormally"),
    RunQuickly("runQuickly"),
    RunStrictly("runStrictly");

    private String text;

    ExampleAppConfigEnum(String text)
    {
        this.text = text;
    }

    public static ExampleAppConfigEnum fromString(String text)
    {
        if (text != null)
            for (ExampleAppConfigEnum e : ExampleAppConfigEnum.values())
                if (text.equalsIgnoreCase(e.text))
                    return e;
        return RunNormally;
    }

    public String toString()
    {
        return text;
    }
}
