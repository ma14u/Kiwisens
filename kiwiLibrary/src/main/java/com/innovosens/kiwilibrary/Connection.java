package com.innovosens.kiwilibrary;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class Connection {

    public static BluetoothLeService mBluetoothLeService;

    private static final String TAG = Connection.class.getSimpleName();

   public static String mDeviceAddress = "";

    private static CommandManager manager;

    private static LocationManager locationManager;


    // Code to manage Service lifecycle.
    private static final ServiceConnection mServiceConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {

                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.d(TAG, "onServiceConnected");

            if (!TextUtils.isEmpty(mDeviceAddress)) {
                mBluetoothLeService.connect(mDeviceAddress);
                App.isConnecting = true;
               // invalidateOptionsMenu();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public static void bindBleService(Context context,String deviceAddress) {
        try {

            manager = CommandManager.getInstance(context);

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            mDeviceAddress = deviceAddress;
            Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
            context.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);


            registerReciever(context);

        }catch (Exception ex)
        {

        }

    }

    public static void registerReciever(Context context)
    {

        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private static BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                App.isConnecting = false;

                manager.realTimeTemp(0X80, 1);


            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED: ");

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent
                        .getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

                List<Integer> datas = DataHandlerUtils.bytesToArrayList(txValue);


                if (datas.get(0) == 0xAB && datas.get(3) == 0xFF&& datas.get(4) == 0x86 && datas.get(5) == 0X80) {

                    //temp

                    try {

                        int hrValue = datas.get(6);
                        int hrValue1 = datas.get(7);


                        Log.e("hrValue",":"+hrValue);
                        Log.e("hrValue1",":"+hrValue1);


                    } catch (Exception e) {


                    }


                }

            }

        }
    };



    public static void getTemperature()
    {
        manager.realTimeTemp(0X80, 1);

    }


}
