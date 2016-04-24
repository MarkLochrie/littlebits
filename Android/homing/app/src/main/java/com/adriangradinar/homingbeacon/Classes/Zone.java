package com.adriangradinar.homingbeacon.Classes;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.adriangradinar.homingbeacon.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by adriangradinar on 06/04/2016.
 * This class is used to define a zone. Each zone has its own beacon and reacts accordingly
 */
public class Zone {

    private String name;
    private MediaPlayer mediaPlayer;
    private ArrayList<Integer> listOfTracks;
    private ArrayList<MiniBeacon> miniBeacons;
    private int position = 0;
    private boolean isPaused = false;
    private boolean isPlaying = false;
    private int trackLocation = 0;
    private AssetFileDescriptor afd;
    private int dingFile = R.raw.some_randon_sound;

    private boolean played = false;

    public Zone(String name, ArrayList<MiniBeacon> miniBeacons, final ArrayList<Integer> tracks, final Context context) {
        this.name = name;
        this.miniBeacons = miniBeacons;
        this.listOfTracks = tracks;
        this.mediaPlayer = MediaPlayer.create(context, listOfTracks.get(position));

        //let's shuffle the collection
        Collections.shuffle(listOfTracks);

        //let's loop the
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                //this function is called when one track has finished playing
                if (!played)
                    position++;

                //logic for when the current list of tracks is finished
                if(position == listOfTracks.size()){
                    Collections.shuffle(listOfTracks);
                    position = 0;
                }

                //let's create logic for the next track
                try {
                    if (!played) {
                        afd = context.getResources().openRawResourceFd(dingFile);
                        played = true;
                    } else {
                        afd = context.getResources().openRawResourceFd(listOfTracks.get(position));
                        played = false;
                    }

                    if (afd == null) return;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    afd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MiniBeacon> getMiniBeacons() {
        return miniBeacons;
    }

    public void setMiniBeacons(ArrayList<MiniBeacon> miniBeacons) {
        this.miniBeacons = miniBeacons;
    }

    public ArrayList<Integer> getListOfTracks() {
        return listOfTracks;
    }

    public void setListOfTracks(ArrayList<Integer> listOfTracks) {
        this.listOfTracks = listOfTracks;
    }

    public void playTrack(){
        if(!this.isPlaying){
            this.isPlaying = true;
            if(this.isPaused){
                this.isPaused = false;

                //seek to the location of the track
                this.mediaPlayer.seekTo(this.trackLocation);
            }

            //start the player
            this.mediaPlayer.start();
        }
    }

    public void pauseTrack(){
        this.mediaPlayer.pause();
        this.trackLocation = this.mediaPlayer.getCurrentPosition();
        this.isPaused = true;
        this.isPlaying = false;
    }

    public void setVolume(float volume) {
        this.mediaPlayer.setVolume(volume, volume);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
