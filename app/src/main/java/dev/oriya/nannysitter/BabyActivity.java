package dev.oriya.nannysitter;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.FrameLayout;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class BabyActivity extends AppCompatActivity implements TemperatureSensorServiceListener , LightSensorServiceListener {

    private static final int PERMISSIONS_REQUEST_CODE = 124;
    public static final String TEMPERATURE_NOTIFICATION = "TEMPERATURE_NOTIFICATION";
    public static final String LIGHT_NOTIFICATION = "LIGHT_NOTIFICATION";

    private static final String TAG = Video.class.getSimpleName();

    private Session session;
    private Publisher publisher;
    private Subscriber subscriber;

    private FrameLayout subscriberViewContainer;

    private LightSensorService.SensorServiceBinder mBinderLight;
    private TemperatureSensorService.SensorServiceBinder mBinderTemperature;
    private boolean isTempBound = false;
    private boolean isLightBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby);

        subscriberViewContainer = findViewById(R.id.baby_FRM_container);

        requestPermissions();

//        mBinderTemperature.startSensors();
//        mBinderLight.startSensors();




    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_CODE)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize and connect to the session
            initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN);

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", PERMISSIONS_REQUEST_CODE, perms);
        }
    }

    private void initializeSession(String apiKey, String sessionId, String token) {
        Log.i(TAG, "apiKey: " + apiKey);
        Log.i(TAG, "sessionId: " + sessionId);
        Log.i(TAG, "token: " + token);

        session = new Session.Builder(this, apiKey, sessionId).build();
        session.setSessionListener(sessionListener);
        session.connect(token);
    }

    private Session.SessionListener sessionListener = new Session.SessionListener() {
        @Override
        public void onConnected(Session session) {
            Log.d(TAG, "onConnected: Connected to session: " + session.getSessionId());

            publisher = new Publisher.Builder(BabyActivity.this).build();
            publisher.setPublisherListener(publisherListener);
            publisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            publisher.setPublishAudio(false);


            if (publisher.getView() instanceof GLSurfaceView) {
                ((GLSurfaceView) publisher.getView()).setZOrderOnTop(true);
            }

            session.publish(publisher);

            session.setSignalListener(new Session.SignalListener() {
                @Override
                public void onSignalReceived(Session session, String s, String s1, Connection connection) {
                    Tools.playSong(BabyActivity.this , s1);
                }
            });
        }

        @Override
        public void onDisconnected(Session session) {
            Log.d(TAG, "onDisconnected: Disconnected from session: " + session.getSessionId());
        }

        @Override
        public void onStreamReceived(Session session, Stream stream) {
            Log.d(TAG, "onStreamReceived: New Stream Received " + stream.getStreamId() + " in session: " + session.getSessionId());

            if (subscriber == null) {
                subscriber = new Subscriber.Builder(BabyActivity.this, stream).build();
                subscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                subscriber.setSubscriberListener(subscriberListener);
                session.subscribe(subscriber);
                subscriberViewContainer.addView(publisher.getView());
            }
        }

        @Override
        public void onStreamDropped(Session session, Stream stream) {
            Log.i(TAG, "Stream Dropped");

            if (subscriber != null) {
                subscriber = null;
                subscriberViewContainer.removeAllViews();
            }
        }


        @Override
        public void onError(Session session, OpentokError opentokError) {
            Log.e(TAG, "Session error: " + opentokError.getMessage());
        }

    };

    private PublisherKit.PublisherListener publisherListener = new PublisherKit.PublisherListener() {
        @Override
        public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
            Log.d(TAG, "onStreamCreated: Publisher Stream Created. Own stream " + stream.getStreamId());
        }

        @Override
        public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
            Log.d(TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream " + stream.getStreamId());
        }

        @Override
        public void onError(PublisherKit publisherKit, OpentokError opentokError) {
            Log.e(TAG, "PublisherKit onError: " + opentokError.getMessage());
        }
    };

    SubscriberKit.SubscriberListener subscriberListener = new SubscriberKit.SubscriberListener() {
        @Override
        public void onConnected(SubscriberKit subscriberKit) {
            Log.d(TAG, "onConnected: Subscriber connected. Stream: " + subscriberKit.getStream().getStreamId());
        }

        @Override
        public void onDisconnected(SubscriberKit subscriberKit) {
            Log.d(TAG, "onDisconnected: Subscriber disconnected. Stream: " + subscriberKit.getStream().getStreamId());
        }

        @Override
        public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
            Log.e(TAG, "SubscriberKit onError: " + opentokError.getMessage());
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (session != null) {
            session.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session != null) {
            session.onResume();
        }
    }


    @Override
    public void lightAlarmStateChanged(ALARM_STATE_LIGHT state) {
        if(state==ALARM_STATE_LIGHT.ON){
            session.sendSignal(LIGHT_NOTIFICATION,"the light has changed");
        }
    }

    @Override
    public void temperatureAlarmStateChanged(ALARM_STATE_TEMPERATURE state) {
        if(state==ALARM_STATE_TEMPERATURE.ON){
            session.sendSignal(TEMPERATURE_NOTIFICATION,"the temperature has changed");
        }
    }
    private ServiceConnection mTempConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderTemperature = (TemperatureSensorService.SensorServiceBinder) service;
            mBinderTemperature.registerListener(BabyActivity.this);
            isTempBound = true;
            mBinderTemperature.startSensors( );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinderTemperature.stopSensors();
            isTempBound = false;
        }
    };
    private ServiceConnection mLightConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderLight = (LightSensorService.SensorServiceBinder) service;
            mBinderLight.registerListener(BabyActivity.this);
            isLightBound = true;
            mBinderLight.startSensors( );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinderLight.stopSensors();
            isLightBound = false;
        }
    };
}
interface HostListener{
    void setMinTemp(int temp);
    boolean setMaxTemp(int temp);
}