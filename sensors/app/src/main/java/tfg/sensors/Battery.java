package tfg.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import tfg.commons.Lock;

/**
 * Created by Light on 29/11/15.
 */
public class Battery extends BroadcastReceiver {

    private Lock statusLock;
    private BatteryValues batteryStatus;
    Context c;

    public Battery(){
        statusLock = new Lock();
        batteryStatus = BatteryValues.STATUS_UNKNOWN;
    }

    private void setBatteryStatus(BatteryValues batVal){
        try {
            statusLock.lock();
            batteryStatus = batVal;
            statusLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public BatteryValues getBatteryStatus(){
        return batteryStatus;
        /*IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = c.registerReceiver(null, ifilter);

        if(batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == BatteryManager.BATTERY_PLUGGED_USB){
            return BatteryValues.CHARGING_USB;
        }
        else if(batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == BatteryManager.BATTERY_PLUGGED_AC){
            return BatteryValues.CHARGING_AC;
        }
        else{
            return BatteryValues.NOT_CHARGING;
        }*/
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == BatteryManager.BATTERY_PLUGGED_USB){
            setBatteryStatus(BatteryValues.CHARGING_USB);
        }
        else if(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == BatteryManager.BATTERY_PLUGGED_AC){
            setBatteryStatus(BatteryValues.CHARGING_AC);
        }
        else{
            setBatteryStatus(BatteryValues.NOT_CHARGING);
        }
        /*Bundle extras = intent.getExtras();
        if(extras != null) {
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {

                Log.d("SENSOR", "Battery intent received");
                //Log.d("SENSOR", "Intent value: " + status);

                if (extras.getInt(BatteryManager.EXTRA_PLUGGED) == BatteryManager.BATTERY_PLUGGED_AC) {
                    this.setBatteryStatus(BatteryValues.CHARGING_AC);
                } else if (extras.getInt(BatteryManager.EXTRA_PLUGGED) == BatteryManager.BATTERY_PLUGGED_USB) {
                    this.setBatteryStatus(BatteryValues.CHARGING_USB);
                }
            } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                this.setBatteryStatus(BatteryValues.NOT_CHARGING);
            } else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

                Log.d("SENSOR", "Battery intent received");
                //Log.d("SENSOR", "Intent value: " + status);

                if (extras.getInt(BatteryManager.EXTRA_PLUGGED) == BatteryManager.BATTERY_PLUGGED_AC) {
                    this.setBatteryStatus(BatteryValues.CHARGING_AC);
                } else if (extras.getInt(BatteryManager.EXTRA_PLUGGED) == BatteryManager.BATTERY_PLUGGED_USB) {
                    this.setBatteryStatus(BatteryValues.CHARGING_USB);
                } else {
                    this.setBatteryStatus(BatteryValues.NOT_CHARGING);
                }
            }
        }
        else{
            this.setBatteryStatus(BatteryValues.STATUS_UNKNOWN);
        }*/
    }

    public enum BatteryValues {
        NOT_CHARGING, CHARGING_USB, CHARGING_AC, STATUS_UNKNOWN;

        @Override
        public String toString(){
            switch (this){
                case NOT_CHARGING:
                    return "NOT CHARGING";
                case CHARGING_USB:
                    return "CHARGING USB";
                case CHARGING_AC:
                    return "CHARGING AC";
                case STATUS_UNKNOWN:
                default:
                    return "BATTERY UNKNOWN STATUS";
            }
        }

    }
}
