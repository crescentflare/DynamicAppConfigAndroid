# DynamicAppConfigAndroid
A useful library to support multiple build configurations in one application build.

For example: be able to make one build with a build selector that contains development, test, acceptance and a production configuration. There would be no need to deliver multiple builds for each environment for testing, it can all be done from one build.

### Features
- Be able to configure several app configurations using JSON
- A built-in app configuration selection activity
- Edit app configurations to customize them from within the app
- Easily access the currently selected configuration (or last stored selection) everywhere
- Dynamic configurations can be disabled to prevent them from being available on Google Play builds

### Integration guide
When using gradle, the library can easily be imported into the build.gradle file of your project. Add the following dependency:

    compile 'com.crescentflare.appconfig:AppConfigLib:0.9.0'

Make sure that jcenter is added as a repository.


**Add activities to manifest**

To enable the build selection menu, add the following activities to your manifest file (they support rotation changes):
        
        <activity android:name="com.crescentflare.appconfig.activity.ManageAppConfigActivity"/>
        <activity android:name="com.crescentflare.appconfig.activity.EditAppConfigActivity"/>
        <activity android:name="com.crescentflare.appconfig.activity.AppConfigStringChoiceActivity"/>
      
        
**Add custom model and manager**

The best way to use the library is to define a custom model (which contains the dynamic setting for the different selections) and a custom manager to return this model. Define these values as public variables or private variables with getters and setters:

    public class ExampleAppConfigModel extends AppConfigBaseModel
    {
        private String name = "Production";
        private String apiUrl = "https://production.example.com/";
    
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
    }

A model always assumes a production configuration (as in, the settings inside can be used safely for a Google Play submission build). A custom manager returning this model would look like this:

    public class ExampleAppConfigManager extends AppConfigBaseManager
    {
        public static ExampleAppConfigManager instance = new ExampleAppConfigManager();
    
        @Override
        public AppConfigBaseModel getBaseModelInstance()
        {
            return new ExampleAppConfigModel();
        }
    
        public static ExampleAppConfigModel currentConfig()
        {
            return (ExampleAppConfigModel)instance.getCurrentConfigInstance();
        }
    }

The manager is a singleton and can be referenced everywhere. The currentConfig method has been made for convenience (to reduce casting code elsewhere). When having a lot of settings, the AppConfigModelCategory annotation can be used to group certain settings together.


**Configuration overrides**

Customized configurations are specified in JSON. For the above model, this file could look like this:

    [
        {
            "name": "Test server",
            "apiUrl": "https://test.example.com/"
        },
        {
            "name": "Production"
        }
    ]
    
As a result, there are 2 configurations to choose from. One for testing and one for production.


**Initialization**

To finish integration, the library needs to be initialized. In your class derived from Application, in the onCreate method, use the following code:

    AppConfigStorage.instance.init(this, ExampleAppConfigManager.instance);
    AppConfigStorage.instance.setLoadingSourceAssetFile("appConfig.json");
    
This will tell the library to use the custom manager class and specifies the file containing the configuration overrides. At this moment no file is loaded yet (and performance is not impacted). It will be loaded when the configuration selection menu is opened.


**Config selection**

In your main activity you can start the app configuration menu to allow yourself or testers to choose a configuration (or make adjustments). Start it with the following code:

    ManageAppConfigActivity.startWithResult(this, 1000);
     
The last parameter (1000) is the result code, which can be checked in onActivityResult. There is also a listener available which is called when a different configuration is selected. Check the example for more details.

### Storage

When existing configurations are edited or custom ones are being added, the changes are saved in the user preferences of the device. Also the last selected configuration is stored inside the preferences. This makes sure that it remembers the correct settings, even if the app is closed silently when the device is running out of memory.

### Automated testing

The library is ready for automated testing using Espresso. The example project provides a demonstration on how to modify the configuration within automated test scripts.

### Security

Because the library can give a lot of control on the product (by making its settings configurable), it's important to prevent any code (either the selection menu itself, or the JSON configuration data like test servers and passwords) from being deployed to Google Play. Take a look at the example project for more information. It uses a simple gradle script to make safe release builds.

### Status

The library should be useful in its basic form, however, there may be bugs. Improvements in features, stability and code structure are welcome.