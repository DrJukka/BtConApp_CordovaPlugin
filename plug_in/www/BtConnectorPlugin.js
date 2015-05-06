  
  var exec = require("cordova/exec");

    var BtConnectorPlugin = {
         startConnector: function ( successCallback, errorCallback) {
            exec(
                successCallback, // success callback function
                errorCallback, // error callback function
                'BtConnectorPlugin', // mapped to our native Java class called "BtConnectorPlugin"
                'start-connector', // with this action name
                []
            );
         },
         stopConnector: function ( successCallback, errorCallback) {
               exec(
                successCallback, // success callback function
                errorCallback, // error callback function
                'BtConnectorPlugin', // mapped to our native Java class called "BtConnectorPlugin"
                'stop-connector', // with this action name
                []
                );
         },
         sendMessage: function ( successCallback, errorCallback, message) {
                exec(
                    successCallback, // success callback function
                    errorCallback, // error callback function
                    'BtConnectorPlugin', // mapped to our native Java class called "BtConnectorPlugin"
                    'send-message', // with this action name
                    [message]
                    );
        },
        connectToDevice: function ( successCallback, errorCallback, deviceAddress) {
                exec(
                    successCallback, // success callback function
                    errorCallback, // error callback function
                    'BtConnectorPlugin', // mapped to our native Java class called "BtConnectorPlugin"
                    'connect-device', // with this action name
                    [deviceAddress]
                    );
        }
    };

    module.exports = BtConnectorPlugin;
