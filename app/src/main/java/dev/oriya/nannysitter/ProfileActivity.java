package dev.oriya.nannysitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;

    private ImageView profile_IMG_profImage;
    private MaterialTextView profile_LBL_fullName;
    private MaterialTextView profile_LBL_email;
    private ImageButton profile_BTN_setting;

    //song
    private MaterialTextView profile_LBL_cardSong;
    private ImageButton profile_BTN_song;
    private ArrayList<String> songs;


    //sensors
    private SeekBar profile_SNS_volume;
    private SeekBar profile_SNS_light;
    private SeekBar profile_SNS_temperature;


    private ScrollView profile_LY_setting;
    private EditText profile_LBL_firstName;
    private EditText profile_LBL_lastName;

    private Spinner profile_SPN_songs;
    private ArrayAdapter<String> songAdapter;

    private MaterialButton profile_BTN_Save;

    private RelativeLayout profile_LY_watch;
    private MaterialButton profile_BTN_watch;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        findViews();
        initSongs();
        initDataFromFireBase();
        allBTNClicked();



    }

    private void allBTNClicked() {
        profile_BTN_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                profile_LY_btns.setVisibility(View.GONE);
                profile_LY_setting.setVisibility(View.VISIBLE);
                seekBarEnabled(true);
            }
        });

        profile_BTN_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String song = profile_LBL_cardSong.getText().toString();
                Tools.playSong(ProfileActivity.this, song);
            }
        });

        profile_BTN_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_LY_setting.setVisibility(View.GONE);
//                profile_LY_btns.setVisibility(View.VISIBLE);
                seekBarEnabled(false);
                profile_LBL_cardSong.setText(songs.get(profile_SPN_songs.getSelectedItemPosition()));
            }
        });

        profile_BTN_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ParentActivity.class));
                finish();
            }
        });
    }

    private void initDataFromFireBase() {
        profile_LBL_email.setText(mAuth.getCurrentUser().getEmail());
        String name = mAuth.getCurrentUser().getDisplayName();
//        if (name.isEmpty() || name.equals(null))
//            name="Full name";
        profile_LBL_fullName.setText(name);
        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(profile_IMG_profImage);
    }

    private void initSongs() {
         songs = new ArrayList<String>();
         songs.add("default");
         songs.add("bella chao");
         songs.add("baby laugh");
         songs.add("baby shark");
    }

    private void seekBarEnabled(boolean b) {
        profile_SNS_temperature.setEnabled(b);
        profile_SNS_light.setEnabled(b);
        profile_SNS_volume.setEnabled(b);
    }

    private void findViews() {
        profile_IMG_profImage = findViewById(R.id.profile_IMG_profImage);
        profile_LBL_fullName = findViewById(R.id.profile_LBL_fullName);
        profile_LBL_email = findViewById(R.id.profile_LBL_email);
        profile_BTN_setting = findViewById(R.id.profile_BTN_setting);
        profile_LBL_cardSong = findViewById(R.id.profile_LBL_cardSong);
        profile_SNS_volume = findViewById(R.id.profile_SNS_volume);
        profile_SNS_light = findViewById(R.id.profile_SNS_light);
        profile_SNS_temperature = findViewById(R.id.profile_SNS_temperature);
        profile_LBL_firstName = findViewById(R.id.profile_LBL_firstName);
        profile_LBL_lastName = findViewById(R.id.profile_LBL_lastName);
        profile_BTN_Save = findViewById(R.id.profile_BTN_Save);
        profile_LY_setting = findViewById(R.id.profile_LY_setting);
        profile_LY_setting.setVisibility(View.GONE);
        profile_BTN_song = findViewById(R.id.profile_BTN_song);
        profile_SPN_songs = findViewById(R.id.profile_SPN_songs);
        songAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , getResources().getStringArray(R.array.songs));
        songAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        profile_SPN_songs.setAdapter(songAdapter);
        seekBarEnabled(false);
        profile_BTN_watch = findViewById(R.id.profile_BTN_watch);
        profile_LY_watch = findViewById(R.id.profile_LY_watch);

    }
}