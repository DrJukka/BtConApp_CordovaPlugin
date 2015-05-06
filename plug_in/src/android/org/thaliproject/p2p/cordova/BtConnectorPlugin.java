package org.thaliproject.p2p.cordova;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thaliproject.p2p.btconnectorlib.BTConnector;
import org.thaliproject.p2p.btconnectorlib.BTConnectorSettings;
import org.thaliproject.p2p.btconnectorlib.ServiceItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by juksilve on 23.1.2015.
 */
public class BtConnectorPlugin extends CordovaPlugin implements BTConnector.Callback, BTConnector.ConnectSelector {
    BtConnectorPlugin that = this;

	public static final String TAG = "BtConnectorPlugin";
    public static final String START_CONNECTOR = "start-connector";
    public static final String STOP_CONNECTOR = "stop-connector";
    public static final String SEND_MESSAGE = "send-message";
    public static final String CONNECT_DEVICE = "connect-device";


    BTConnectedThread mBTConnectedThread = null;

    CallbackContext statusCallback = null;

    BTConnectorSettings conSettings = null;;
    BTConnector mBTConnector = null;

    List<ServiceItem> lastAvailableList = null;

    final String instanceEncryptionPWD = "CHANGEYOURPASSWRODHERE";
    final String serviceTypeIdentifier = "Cordovap2p._tcp";
    final String BtUUID                = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    final String Bt_NAME               = "Thaili_Bluetooth";

    public BtConnectorPlugin()
    {
        conSettings = new BTConnectorSettings();
        conSettings.SERVICE_TYPE = serviceTypeIdentifier;
        conSettings.MY_UUID = UUID.fromString(BtUUID);
        conSettings.MY_NAME = Bt_NAME;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StopConnector();
    }

    public void StopConnector() {
        if (mBTConnectedThread != null) {
            mBTConnectedThread.Stop();
            mBTConnectedThread = null;
        }

        if(mBTConnector != null) {
            mBTConnector.Stop();
            mBTConnector = null;
        }
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.v(TAG,"Init BtConnectorPlugin");
	}
	public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		final int duration = Toast.LENGTH_SHORT;
		// Shows a toast
		Log.v(TAG,"BtConnectorPlugin received:"+ action);

        if (START_CONNECTOR.equals(action)) {
            statusCallback = callbackContext;
            String reply = "{ \"status\": \"Idle\"}";
            PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
            result.setKeepCallback(true);
            statusCallback.sendPluginResult(result);

           StopConnector();

            mBTConnector = new BTConnector(this.cordova.getActivity().getApplicationContext(),that,that,conSettings,instanceEncryptionPWD);
            mBTConnector.Start();

      /*      //just for testiing..
            List<ServiceItem> available = new ArrayList<ServiceItem>();

            available.add(new ServiceItem("Humppaa","Skumppaa","koti1", "Jukka1"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti2", "Jukka2"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti3", "Jukka3"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti4", "Jukka4"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti5", "Jukka5"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti6", "Jukka6"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti7", "Jukka7"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti8", "Jukka8"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti9", "Jukka9"));
            available.add(new ServiceItem("Humppaa","Skumppaa","koti0", "Jukka0"));

            SelectServiceToConnect(available);*/

        }else if (STOP_CONNECTOR.equals(action)){
            StopConnector();

            if(statusCallback != null) {
                String reply = "{ \"status\": \"Idle\",";
                reply = reply +"\"remoteName\": \" \",";
                reply = reply +"\"remoteAddress\": \" \"}";

                PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
               // result.setKeepCallback(true); // not needed anymore
                statusCallback.sendPluginResult(result);
                statusCallback = null;
            }
        }else if (SEND_MESSAGE.equals(action)){

            String message = args.getString(0);
            if (mBTConnectedThread != null) {
                mBTConnectedThread.write(message.getBytes());
                callbackContext.success("wrote" + message);
            }else{
                callbackContext.error("No connected tread available");
            }
        }else if (CONNECT_DEVICE.equals(action)){

            String devAddress = args.getString(0);
            if (mBTConnector != null) {

                ServiceItem selectedDevice = null;
                if(lastAvailableList != null) {
                    for(int i = 0; i < lastAvailableList.size(); i ++){
                        if(lastAvailableList.get(i).deviceAddress.contentEquals(devAddress)){
                            selectedDevice = lastAvailableList.get(i);
                            break;
                        }
                    }
                }

                if(selectedDevice != null) {
                    mBTConnector.TryConnect(selectedDevice);
                    callbackContext.success("Connecting to" + devAddress);
                }else{
                    callbackContext.error("Device not discovered");
                }
            }else{
                callbackContext.error("Connector not ready");
            }
        }
		cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), action, duration);
                toast.show();
            }
        });
	return true;
	}

    @Override
    public void Connected(BluetoothSocket socket, boolean incoming) {
        if(statusCallback != null) {

            String reply = "{ \"remoteName\": \"" + socket.getRemoteDevice().getName() + "\",";
            reply = reply +"\"remoteAddress\": \"" + socket.getRemoteDevice().getAddress() + "\"}";

            PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
            result.setKeepCallback(true);
            statusCallback.sendPluginResult(result);
        }

        if (mBTConnectedThread != null) {
            mBTConnectedThread.Stop();
            mBTConnectedThread = null;
        }

        mBTConnectedThread = new BTConnectedThread(socket,mHandler);
        mBTConnectedThread.start();
    }

    @Override
    public void StateChanged(BTConnector.State newState) {

        if(statusCallback != null) {

            String reply = "{ \"status\": \"" + newState + "\"}";

            PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
            result.setKeepCallback(true);
            statusCallback.sendPluginResult(result);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BTConnectedThread.MESSAGE_WRITE:
                    {
                       byte[] writeBuf = (byte[]) msg.obj;// construct a string from the buffer
                       String writeMessage = new String(writeBuf);

                        if(statusCallback != null) {

                            String reply = "{ \"writeMessage\": \"" + writeMessage + "\"}";

                            PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
                            result.setKeepCallback(true);
                            statusCallback.sendPluginResult(result);
                        }
                    }
                    break;
                case BTConnectedThread.MESSAGE_READ:
                    {
                        byte[] readBuf = (byte[]) msg.obj;// construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);

                        if(statusCallback != null) {

                            String reply = "{ \"readMessage\": \"" + readMessage + "\"}";

                            PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
                            result.setKeepCallback(true);
                            statusCallback.sendPluginResult(result);
                        }
                    }
                    break;
                case BTConnectedThread.SOCKET_DISCONNEDTED: {
                    if (mBTConnectedThread != null) {
                        mBTConnectedThread.Stop();
                        mBTConnectedThread = null;
                    }

                    if(statusCallback != null) {
                        String reply = "{ \"status\": \"Idle\",";
                        reply = reply +"\"remoteName\": \" \",";
                        reply = reply +"\"remoteAddress\": \" \"}";

                        PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
                        result.setKeepCallback(true);
                        statusCallback.sendPluginResult(result);
                    }

                    if(mBTConnector != null) {
                        mBTConnector.Start();
                    }
                }
                break;
            }
        }
    };

    @Override
    public ServiceItem SelectServiceToConnect(List<ServiceItem> available) {

        lastAvailableList = available;
        if(statusCallback != null) {

            String reply = "{\"devicesAvailable\":[";

            for(int i =0; i < available.size(); i++){
                if(i>0){
                    reply = reply + ",";
                }
                reply = reply +"{\"deviceName\":\"" + available.get(i).deviceName + "\", " +"\"deviceAddress\":\"" +available.get(i).deviceAddress + "\"}";
            }
            reply = reply +"]}";

            PluginResult result = new PluginResult(PluginResult.Status.OK, reply);
            result.setKeepCallback(true);
            statusCallback.sendPluginResult(result);
        }

        //lets return null, we will try connecting later.
        return null;
    }
}