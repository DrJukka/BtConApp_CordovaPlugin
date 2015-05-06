
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("startButton").addEventListener("click", startConnector);
    document.getElementById("stopButton").addEventListener("click", stopConnector);
    document.getElementById("SendButton").addEventListener("click", SendMessage);
    document.getElementById("ClearMessagesButton").addEventListener("click", ClearMessages);
    document.getElementById("ConnectButton").addEventListener("click", ConnectToDevice);
 //  alert('button is set');
});

function startConnector()
{
    var BtConnectorPlugin = cordova.require("org.thaliproject.p2p.cordova.BtConnectorPlugin.BtConnectorPlugin");

    var failure = function() {
        alert("Error calling BtConnectorPlugin");
    }

    document.getElementById('stopButton').style.display = 'block';
    document.getElementById('startButton').style.display = 'none';

   BtConnectorPlugin.startConnector(ReplyHandler, failure);
}

var ReplyHandler = function(message) {

         var myJson = JSON.parse(message);

         if(myJson.status){
            document.getElementById('StateBox').value = myJson.status;
         }

         if(myJson.remoteName && myJson.remoteAddress){
            document.getElementById('RemNameBox').value = myJson.remoteName;
            document.getElementById('RemAddrBox').value = myJson.remoteAddress;

            if(document.getElementById('RemNameBox').value.length > 1
            && document.getElementById('RemAddrBox').value.length > 1){
                document.getElementById('myRemoteDevice').style.display = 'block';
                document.getElementById('myDeviceSelection').style.display = 'none';
            }
         }

         if(myJson.readMessage){
            addChatLine(document.getElementById('RemNameBox').value, myJson.readMessage);
         }

        if(myJson.writeMessage){
            addChatLine("ME",myJson.writeMessage);
        }

        if(myJson.devicesAvailable){
             document.getElementById('myDeviceSelection').style.display = 'block';
             document.getElementById('myRemoteDevice').style.display = 'none';

            //Remove previously found devices from the list
            var selDevice = document.getElementById("deviceSelector");
            for(o=selDevice.options.length-1;o>=0;o--){
                    selDevice.remove(o);
            }

            var i = 0;
            for(; i < myJson.devicesAvailable.length; i++) {
                var obj = myJson.devicesAvailable[i];
                var option = document.createElement("option");
                option.value = obj.deviceAddress;
                option.text = obj.deviceName;
                selDevice.appendChild(option);
             }

             if(i > 0){
                selDevice.selectedIndex = 0;
             }
        }
    }

function addChatLine(who, message){
    document.getElementById('ReplyBox').value = document.getElementById('ReplyBox').value + "\n" + who + " : " +message;
}

function stopConnector(){

    var BtConnectorPlugin = cordova.require("org.thaliproject.p2p.cordova.BtConnectorPlugin.BtConnectorPlugin");

    var failure = function() {
        alert("Error calling BtConnectorPlugin");
    }

    document.getElementById('stopButton').style.display = 'none';
    document.getElementById('startButton').style.display = 'block';

   BtConnectorPlugin.stopConnector(ReplyHandler, failure);

   document.getElementById('myRemoteDevice').style.display = 'none';
   document.getElementById('myDeviceSelection').style.display = 'none';
}

function ConnectToDevice(){
    var selDevice = document.getElementById("deviceSelector");
    var address = selDevice.options[selDevice.selectedIndex].value;
    var name = selDevice.options[selDevice.selectedIndex].text;

    var failure = function(message) {
        alert("Error connecting : " + message);
    }

    var success = function(message) {
        //alert("Error calling BtConnectorPlugin");
    }

    if(address.length > 0 && name.length > 0){
        document.getElementById('myRemoteDevice').style.display = 'block';
        document.getElementById('myDeviceSelection').style.display = 'none';

        alert("Selected name: " + name + ", address: " + address);

        BtConnectorPlugin.connectToDevice(success, failure,address);
    }else{
        alert("No device selected to connect to");
    }
}

function ClearMessages(){
    document.getElementById('ReplyBox').value = "";
}

function SendMessage(){
    var BtConnectorPlugin = cordova.require("org.thaliproject.p2p.cordova.BtConnectorPlugin.BtConnectorPlugin");

    var failure = function(message) {
        alert("Error calling : " + message);
    }

    var success = function(message) {
        //alert("Error calling BtConnectorPlugin");
    }

    var message = document.getElementById('MessageBox').value;
    BtConnectorPlugin.sendMessage(success, failure,message);
    document.getElementById('MessageBox').value = ""
}
