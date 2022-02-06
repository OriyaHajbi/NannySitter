package dev.oriya.nannysitter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class Tools {
    public static MediaPlayer music;

    public static void myToast(Activity activity, String msg){
        Toast.makeText(activity,msg , Toast.LENGTH_SHORT).show();
    }
    public static void playSong(Activity activity , String song){
        if (music != null){
            music.release();
            music=null;
        }
        Log.d("songggggggg" , song);


        if (song.equals("default"))
            music = MediaPlayer.create(activity ,R.raw.calm);
        else if (song.equals("bella chao"))
            music = MediaPlayer.create(activity ,R.raw.bellachao);
        else if (song.equals("baby shark"))
            music = MediaPlayer.create(activity ,R.raw.babyshark);
        else if (song.equals("baby laugh"))
            music = MediaPlayer.create(activity ,R.raw.babylaugh);

//        switch (song){
//            case "default":
//                music = MediaPlayer.create(activity ,R.raw.calm);
//            case "bella chao":
//                music = MediaPlayer.create(activity ,R.raw.bellachao);
//            case "baby shark":
//                music = MediaPlayer.create(activity ,R.raw.babyshark);
//            case "baby laugh":
//                music = MediaPlayer.create(activity ,R.raw.babylaugh);
//        }
        if (music != null)
            music.start();
    }
}
