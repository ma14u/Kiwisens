package com.innovosens.kiwilibrary.Commands;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.innovosens.kiwilibrary.Constants.BleConstans;
import com.innovosens.kiwilibrary.Constants.Constans;

import java.util.Calendar;


public class CommandManager {
    
    private static final String TAG = "CommandManager";
    private static Context mContext;
    private static CommandManager instance;

    private CommandManager() {
    }

    public static synchronized CommandManager getInstance(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }





    public void getBatteryInfo(){
        byte[] bytes = new byte[6];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 3;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x91;
        bytes[5] = (byte) 0x80;
        broadcastData(bytes);
    }


    public void realTimeAndOnceMeasure(int status, int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x31;
        bytes[5] = (byte) status;
        bytes[6] = (byte) control;//0关  1开
        broadcastData(bytes);
    }


    public void realTimeTemp(int status, int control) {
        byte[] bytes = new byte[7];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0;
        bytes[2] = (byte) 4;
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x86;
        //心率：0X09(单次) 0X0A(实时)  血氧：0X11(单次) 0X12(实时) 血压：0X21 0X22   // body temp 0X80
        bytes[5] = (byte) status;
        bytes[6] = (byte) control;//0关  1开
        broadcastData(bytes);
    }







    /**
     * @brief Broadcast intent with pointed bytes.
     * @param[in] bytes Array of byte to send on BLE.
     */

    private void broadcastData(byte[] bytes) {
        final Intent intent = new Intent(BleConstans.ACTION_SEND_DATA_TO_BLE);
        intent.putExtra(Constans.EXTRA_SEND_DATA_TO_BLE, bytes);
        try {

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
