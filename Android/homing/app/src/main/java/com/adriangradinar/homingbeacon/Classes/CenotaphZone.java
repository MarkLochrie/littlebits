package com.adriangradinar.homingbeacon.Classes;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by adriangradinar on 21/04/2016.
 * This is the cenotaph zone, an extension of the zone class but with added functionality to support
 * the playing of multiple streams simultaneously.
 */
public class CenotaphZone extends Zone {

    private static final String TAG = CenotaphZone.class.getSimpleName();

    private String name;
    private ArrayList<MediaPlayer> players = new ArrayList<>();
    private ArrayList<Integer> listOfTracks;
    private ArrayList<MiniBeacon> miniBeacons;

    public CenotaphZone(String name, ArrayList<MiniBeacon> miniBeacons, ArrayList<Integer> tracks, final Context context) {
        super(name, miniBeacons, tracks, context);
        this.name = name;
        this.listOfTracks = tracks;
        Collections.shuffle(listOfTracks);
        this.miniBeacons = miniBeacons;

        //instantiate how many players we need
        for (int i = 0; i < totalPlayers(); i++) {
            MediaPlayer player = MediaPlayer.create(context, listOfTracks.get(i));
            player.setLooping(true);
            this.players.add(player);

        }
        Log.e("Total players", "Total = " + players.size());
    }

    public void playTrack() {
        Log.e(TAG, "play cenotaph called " + players.size());
        for (MediaPlayer mediaPlayer : players) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    public void pauseTrack() {
        for (MediaPlayer mediaPlayer : players) {
            mediaPlayer.pause();
        }
    }

    public void setVolume(float volume) {
        for (MediaPlayer mediaPlayer : players) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    private int totalPlayers() {
        Random rand = new Random();
        return rand.nextInt(2) + 1;
    }
}
