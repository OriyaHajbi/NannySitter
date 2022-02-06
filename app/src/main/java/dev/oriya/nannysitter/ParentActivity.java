package dev.oriya.nannysitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
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


interface Music_Callback {

    void playMusic(String musicName);

}
public class ParentActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 124;
    public static final String SONG_NAME = "SONG_NAME";
    private static final String TAG = Video.class.getSimpleName();

    private Session session;
    private Publisher publisher;
    private Subscriber subscriber;

    private FrameLayout parent_FRM_songs;
    private LinearLayout parent_LN_songs;
    private boolean bUp = false;
    private boolean mMuted = true;
    private MaterialButton bMute;
    private MaterialButton bSong;


    private FrameLayout publisherViewContainer;
    private int counter=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        initViews();

        requestPermissions();
        createNotificationChannel();

        MusicFragment musicFragment = new MusicFragment();
        Music_Callback music_callback = new Music_Callback() {
            @Override
            public void playMusic(String musicName) {
                session.sendSignal(SONG_NAME, musicName);
            }
        };
        musicFragment.setMusic_callback(music_callback);
        getSupportFragmentManager().beginTransaction().replace(R.id.parantV_FRM_music, musicFragment).commit();

        allBTNClicked();


    }

    private void allBTNClicked() {
        bSong.setOnClickListener(v -> onUpDownClicked());
        bMute.setOnClickListener(v -> onLocalAudioMuteClicked());
    }

    private void initViews() {
        parent_FRM_songs = findViewById(R.id.parantV_FRM_music);
        publisherViewContainer = findViewById(R.id.parent_FRM_container);
        bSong = findViewById(R.id.parentV_BTN_song);
        bSong.setVisibility(View.VISIBLE);
        bMute = findViewById(R.id.parentV_BTN_mute);
        bMute.setVisibility(View.VISIBLE);
        parent_LN_songs = findViewById(R.id.parent_LN_music);
        parent_LN_songs.setVisibility(View.GONE);
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

            publisher = new Publisher.Builder(ParentActivity.this).build();
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
                    notification(s1);
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
                subscriber = new Subscriber.Builder(ParentActivity.this, stream).build();
                subscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
                subscriber.setSubscriberListener(subscriberListener);
                session.subscribe(subscriber);
                publisherViewContainer.addView(publisher.getView());
            }
        }

        @Override
        public void onStreamDropped(Session session, Stream stream) {
            Log.i(TAG, "Stream Dropped");

            if (subscriber != null) {
                subscriber = null;
                publisherViewContainer.removeAllViews();
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
    public void onUpDownClicked() {
        bUp = !bUp;
        // Stops/Resumes sending the local audio stream.
        if(publisher!=null){
            if (bUp)
                parent_LN_songs.setVisibility(View.GONE);
            else
                parent_LN_songs.setVisibility(View.VISIBLE);
            int res = bUp ? R.drawable.ic_baseline_arrow_circle_up_24 : R.drawable.ic_baseline_arrow_circle_down_24;
            bSong.setIconResource(res);}
    }
    public void onLocalAudioMuteClicked() {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        if(publisher!=null){
            publisher.setPublishAudio(!mMuted);
            int res = mMuted ? R.drawable.ic_baseline_mic_24 : R.drawable.ic_baseline_mic_off_24;
            bMute.setIconResource(res);}
    }

    public void notification (String msg)
    {
// notification for temperature and light change
        Intent intent = new Intent(this, BabyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, OpenTokConfig.CHANNEL_ID)
                .setSmallIcon(R.drawable.babyboy)
                .setContentTitle("NannySitter")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
// notificationId is a unique int for each notification that you must define
        notificationManager.notify(counter++, builder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(OpenTokConfig.CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}