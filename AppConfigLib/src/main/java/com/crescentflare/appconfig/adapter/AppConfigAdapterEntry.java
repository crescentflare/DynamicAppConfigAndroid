package com.crescentflare.appconfig.adapter;

/**
 * Library adapter: config list adapter entry
 * List view adapter entry for the configuration list, mostly used to handle different view types
 */
public class AppConfigAdapterEntry
{
    /**
     * Enum
     */
    public enum Type
    {
        Configuration,
        BuildInfo,
        Header,
        Footer
    }

    /**
     * Members
     */
    private Type type = Type.Configuration;
    private String name = "";
    private String label = "";


    /**
     * Initialization
     */
    public AppConfigAdapterEntry(Type type, String name, String label)
    {
        this.type = type;
        this.name = name;
        this.label = label;
    }

    public static AppConfigAdapterEntry entryForConfiguration(String configurationName, boolean edited)
    {
        return new AppConfigAdapterEntry(Type.Configuration, configurationName, configurationName + (edited ? " *" : ""));
    }

    public static AppConfigAdapterEntry entryForConfiguration(String configurationName, String customLabel, boolean edited)
    {
        return new AppConfigAdapterEntry(Type.Configuration, configurationName, customLabel + (edited ? " *" : ""));
    }

    public static AppConfigAdapterEntry entryForBuildInfo(String key, String value)
    {
        return new AppConfigAdapterEntry(Type.BuildInfo, "", key + ": " + value);
    }

    public static AppConfigAdapterEntry entryForHeader(String headerName)
    {
        return new AppConfigAdapterEntry(Type.Header, "", headerName);
    }

    public static AppConfigAdapterEntry entryForFooter()
    {
        return new AppConfigAdapterEntry(Type.Footer, "", "");
    }

    /**
     * Getters
     */
    public Type getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }
}
