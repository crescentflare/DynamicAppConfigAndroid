package com.crescentflare.appconfigexample.appconfig;


import com.crescentflare.appconfig.model.AppConfigBaseModel;

/**
 * App config: application configuration
 * A convenience model used by the build environment selector and has strict typing
 * Important: this model should always reflect a production situation
 */
public class ExampleAppConfigModel extends AppConfigBaseModel
{
    /**
     * Members
     */
    private String name = "Production";
    private String apiUrl = "https://production.example.com/";
    private ExampleAppConfigEnum runType = ExampleAppConfigEnum.RunNormally;
    private boolean acceptAllSSL = false;
    private int networkTimeoutSec = 20;


    /**
     * Generated code
     */
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
