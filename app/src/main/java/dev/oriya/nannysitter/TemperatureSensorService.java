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
import android.util.Log;

import androidx.annotation.Nullable;

public class TemperatureSensorService extends Service implements SensorEventListener , HostListener {

    private final IBinder mBinder = new SensorServiceBinder();

    private double minTemp=-1;
    private double maxTemp=-1;
    private double threshold = 3;
    private TemperatureSensorServiceListener.ALARM_STATE_TEMPERATURE currentAlarmState = TemperatureSensorServiceListener.ALARM_STATE_TEMPERATURE.OFF;
    private TemperatureSensorServiceListener listener;
    private float firstX = 0;
    private boolean isLocked = false;
    private SensorManager mSensorManager;
    private Sensor mTemperature;

    //temp user callback
    private User_Callback user_callback;

    public TemperatureSensorService setUser_callback(User_Callback user_callback) {
        this.user_callback = user_callback;
        return this;
    }

    public TemperatureSensorService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        setTemperature();
    }

    private void setTemperature() {
            user_callback = new User_Callback() {
                @Override
                public void userExistAndGetUserData(User user) {
                    threshold = user.getTemperatureTreshold();
                }

                @Override
                public void userDoesNotExist() {
                    threshold = 3;
                }
            };
            myDB.getInstance().setUser_callback(user_callback);
            myDB.getInstance().LoadUser();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager = null;
        mTemperature = null;
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
            if(minTemp==-1) {
                minTemp=firstX- threshold;
                if(maxTemp==-1) {
                    maxTemp=firstX+ threshold;
                }
            }

            float absDiffX = Math.abs(firstX - event.values[0]);

            TemperatureSensorServiceListener.ALARM_STATE_TEMPERATURE previousState = currentAlarmState;
            if (absDiffX > maxTemp||absDiffX<minTemp ) {
                this.currentAlarmState = TemperatureSensorServiceListener.ALARM_STATE_TEMPERATURE.ON;
            } else {
                this.currentAlarmState = TemperatureSensorServiceListener.ALARM_STATE_TEMPERATURE.OFF;
            }

            if (previousState != currentAlarmState) {
                listener.temperatureAlarmStateChanged(currentAlarmState);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void setMinTemp(int min) {
        this.minTemp=min;
    }

    @Override
    public boolean setMaxTemp(int max) {
        if(max>this.minTemp) {
            this.maxTemp = max;
            return true;
        }
        return false;
    }

    public class SensorServiceBinder extends Binder {
        void registerListener(TemperatureSensorServiceListener mListener) {
            listener = mListener;
        }

        void unregisterListener() {
            listener = null;
        }

        public boolean changeMaxTemp(int temp){
            return setMaxTemp(temp);
        }
        public void changeMinTemp(int temp){
            setMinTemp(temp);
        }

        void startSensors() {
            mSensorManager.registerListener(TemperatureSensorService.this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        }

        void stopSensors() {
            mSensorManager.unregisterListener(TemperatureSensorService.this);
        }

        void resetInitialLock() {
            isLocked = false;
        }

    }
}

interface TemperatureSensorServiceListener {
    enum ALARM_STATE_TEMPERATURE {
        ON, OFF
    }

    void temperatureAlarmStateChanged(ALARM_STATE_TEMPERATURE state);

}