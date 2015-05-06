# BtConApp_CordovaPlugin
Simple test on how easy is would be to use the Bt-library with Cordova 

Since this is just a test, I was including the whole library as source codes into the project, which indeed gives some issues I did not have time to fix. 

If you know fix, then please let me know. Currently after you have added the plug-in into your app, you need to manually do following (was using Android studio for fixing)
- 
with platforms\android\settings.gradle
cahnge 'include ":"' to 'include ":", ':btconnectorlib'' , so it will find the included sub project when compiling

with platforms\android\AndroidManifest.xml
- change 'minSdkVersion="10"'  to  'minSdkVersion="16"' so the project will compile.

Then there is example chat application incldued in example folder, so copy those files for quick tests. 