package tfg.sensors;

/**
 * Created by Light on 28/11/15.
 */
public class SensorResult {

    private double accVal;
    private int accAccuracy;
    private double mgtVal;
    private int mgtAccuracy;
    private double lightVal;
    private int lightAccuracy;
    private double locLat;
    private double locLong;
    private double locAlt;
    private Battery.BatteryValues batVal;
    private String time;

    public SensorResult() {
        this.accVal = -1;
        this.accAccuracy = -1;
        this.mgtVal = -1;
        this.mgtAccuracy = -1;
        this.lightVal = -1;
        this.lightAccuracy = -1;
        this.locLat = -1;
        this.locLong = -1;
        this.locAlt = -1;
        this.time = "NO INIT";
    }

    public SensorResult(double accVal, int accAccuracy, double mgtVal, int mgtAccuracy, double lightVal,
                        int lightAccuracy, double locLat, double locLong, double locAlt, String time,
                        Battery.BatteryValues batVal) {
        this.accVal = accVal;
        this.accAccuracy = accAccuracy;
        this.mgtVal = mgtVal;
        this.mgtAccuracy = mgtAccuracy;
        this.lightVal = lightVal;
        this.lightAccuracy = lightAccuracy;
        this.locLat = locLat;
        this.locLong = locLong;
        this.locAlt = locAlt;
        this.time = time;
        this.batVal = batVal;
    }

    public double getAccVal() {
        return accVal;
    }

    public int getAccAccuracy() {
        return accAccuracy;
    }

    public double getMgtVal() {
        return mgtVal;
    }

    public int getMgtAccuracy() {
        return mgtAccuracy;
    }

    public double getLightVal() {
        return lightVal;
    }

    public int getLightAccuracy() {
        return lightAccuracy;
    }

    public double getLocLat() {
        return locLat;
    }

    public double getLocLong() {
        return locLong;
    }

    public double getLocAlt() {
        return locAlt;
    }

    public String getTime() {
        return time;
    }

    public Battery.BatteryValues getBatVal(){
        return batVal;
    }
}
