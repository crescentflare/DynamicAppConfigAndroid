package com.crescentflare.appconfigexample.appconfig;


import com.crescentflare.appconfig.model.AppConfigBaseModel;
import com.crescentflare.appconfig.model.AppConfigModelCategory;
import com.crescentflare.appconfig.model.AppConfigModelSort;

/**
 * App config: application configuration
 * A convenience model used by the build environment selector and has strict typing
 * Important: this model should always reflect a production situation
 */
public class ExampleAppConfigModel extends AppConfigBaseModel
{
    // ---
    // Members
    // ---

    private String name = "Production";

    @AppConfigModelSort(0)
    @AppConfigModelCategory("API related")
    private String apiUrl = "https://production.example.com/";

    @AppConfigModelSort(1)
    @AppConfigModelCategory("API related")
    private int networkTimeoutSec = 20;

    @AppConfigModelSort(2)
    @AppConfigModelCategory("API related")
    private boolean acceptAllSSL = false;

    @AppConfigModelSort(3)
    private ExampleAppConfigEnum runType = ExampleAppConfigEnum.RunNormally;


    // ---
    // Generated code
    // ---

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl)
    {
        this.apiUrl = apiUrl;
    }

    public ExampleAppConfigEnum getRunType()
    {
        return runType;
    }

    public void setRunType(ExampleAppConfigEnum runType)
    {
        this.runType = runType;
    }

    public boolean isAcceptAllSSL()
    {
        return acceptAllSSL;
    }

    public void setAcceptAllSSL(boolean acceptAllSSL)
    {
        this.acceptAllSSL = acceptAllSSL;
    }

    public int getNetworkTimeoutSec()
    {
        return networkTimeoutSec;
    }

    public void setNetworkTimeoutSec(int networkTimeoutSec)
    {
        this.networkTimeoutSec = networkTimeoutSec;
    }
}
