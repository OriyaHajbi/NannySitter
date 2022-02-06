package dev.oriya.nannysitter;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<String> songs;
    private Spinner music_SPN_songs;
    private ArrayAdapter<String> songAdapter;
    private MaterialButton music_BTN_song;
    private Music_Callback music_callback;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_music, container, false);
        initSongs();
        initViews(view);
        music_BTN_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String music = songs.get(music_SPN_songs.getSelectedItemPosition());
                music_callback.playMusic(music);
            }
        });
        return view;
    }

    private void initViews(View view) {
        music_SPN_songs = view.findViewById(R.id.music_SPN_songs);
        music_BTN_song = view.findViewById(R.id.music_BTN_song);
        songAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , getResources().getStringArray(R.array.songs));
        songAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        music_SPN_songs.setAdapter(songAdapter);
    }

    private void initSongs() {
        songs = new ArrayList<String>();
        songs.add("default");
        songs.add("bella chao");
        songs.add("baby laugh");
        songs.add("baby shark");
    }


    public MusicFragment setMusic_callback(Music_Callback music_callback) {
        this.music_callback = music_callback;
        return this;
    }
}