# DynamicAppConfigAndroid
A useful library to support multiple build configurations in one application build.

For example: be able to make one build with a build selector that contains development, test, acceptance and a production configuration. There would be no need to deliver multiple builds for each environment for testing, it can all be done from one build.

### Features
- Be able to configure several build configurations using JSON
- A built-in build configuration selection activity
- Edit build configurations to customize them from within the app
- Easily access the currently selected configuration (or last stored selection) everywhere
- Dynamic configurations can be disabled to prevent them from being available on Google Play builds

### Status
A proof of concept has been made and the library is useful in a limited form (there are some bugs), improvements will be made over time