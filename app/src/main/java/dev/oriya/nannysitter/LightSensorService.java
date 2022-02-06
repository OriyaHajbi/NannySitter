package dev.oriya.nannysitter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LightSensorService extends Service implements SensorEventListener {
    private final IBinder mBinder = new SensorServiceBinder();
    private  double treshold = 300;
    private LightSensorServiceListener.ALARM_STATE_LIGHT currentAlarmState = LightSensorServiceListener.ALARM_STATE_LIGHT.OFF;
    private LightSensorServiceListener listener;
    private float firstX = 0;
    private boolean isLocked = false;
    private SensorManager mSensorManager;
    private Sensor mLight;

    public LightSensorService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager = null;
        mLight = null;
        listener = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isLocked == false) {
            isLocked = true;
            firstX = event.values[0];

        } else {
            float absDiffX = Math.abs(firstX - event.values[0]);
            LightSensorServiceListener.ALARM_STATE_LIGHT previousState = currentAlarmState;
            if (absDiffX > treshold ) {
                this.currentAlarmState = LightSensorServiceListener.ALARM_STATE_LIGHT.ON;
            } else {
                this.currentAlarmState = LightSensorServiceListener.ALARM_STATE_LIGHT.OFF;
            }
            if (previousState != currentAlarmState) {
                listener.lightAlarmStateChanged(currentAlarmState);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public class SensorServiceBinder extends Binder {

        void registerListener(LightSensorServiceListener mListener) {
            listener = mListener;
        }

        void unregisterListener() {
            listener = null;
        }

        void startSensors() {
            mSensorManager.registerListener(LightSensorService.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        }

        void stopSensors() {
            mSensorManager.unregisterListener(LightSensorService.this);
        }

        void resetInitalLock() {
            isLocked = false;
        }

    }
}
interface LightSensorServiceListener {

    enum ALARM_STATE_LIGHT {
        ON, OFF

    }
    void lightAlarmStateChanged(ALARM_STATE_LIGHT state);

}
