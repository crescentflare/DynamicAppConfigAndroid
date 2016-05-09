package com.crescentflare.appconfig.adapter;

/**
 * Library adapter: config list adapter entry
 * List view adapter entry for the configuration list, mostly used to handle different view types
 */
public class AppConfigAdapterEntry
{
    /**
     * Enums
     */
    public enum Type
    {
        Configuration,
        BuildInfo,
        Header,
        Footer
    }

    public enum Section
    {
        Unknown,
        LastSelected,
        Predefined,
        Custom,
        Add
    }

    /**
     * Members
     */
    private Type type = Type.Configuration;
    private Section section = Section.Unknown;
    private String name = "";
    private String label = "";


    /**
     * Initialization
     */
    public AppConfigAdapterEntry(Type type, Section section, String name, String label)
    {
        this.type = type;
        this.section = section;
        this.name = name;
        this.label = label;
    }

    public static AppConfigAdapterEntry entryForConfiguration(Section section, String configurationName, boolean edited)
    {
        return new AppConfigAdapterEntry(Type.Configuration, section, configurationName, configurationName + (edited ? " *" : ""));
    }

    public static AppConfigAdapterEntry entryForConfiguration(Section section, String configurationName, String customLabel, boolean edited)
    {
        return new AppConfigAdapterEntry(Type.Configuration, section, configurationName, customLabel + (edited ? " *" : ""));
    }

    public static AppConfigAdapterEntry entryForBuildInfo(String key, String value)
    {
        return new AppConfigAdapterEntry(Type.BuildInfo, Section.Unknown, "", key + ": " + value);
    }

    public static AppConfigAdapterEntry entryForHeader(String headerName)
    {
        return new AppConfigAdapterEntry(Type.Header, Section.Unknown, "", headerName);
    }

    public static AppConfigAdapterEntry entryForFooter()
    {
        return new AppConfigAdapterEntry(Type.Footer, Section.Unknown, "", "");
    }

    /**
     * Getters
     */
    public Type getType()
    {
        return type;
    }

    public Section getSection()
    {
        return section;
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
