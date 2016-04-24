package com.adriangradinar.homingbeacon;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.adriangradinar.homingbeacon.Classes.CenotaphZone;
import com.adriangradinar.homingbeacon.Classes.MiniBeacon;
import com.adriangradinar.homingbeacon.Classes.Zone;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TIMES_UNTIL_CHANGE = 5;
    private BeaconManager beaconManager;
    private Region region;

    private ArrayList<MiniBeacon> zoneOneBeacons = new ArrayList<>(Arrays.asList(new MiniBeacon(100, 1)));
    private ArrayList<MiniBeacon> zoneTwoBeacons = new ArrayList<>(Arrays.asList(new MiniBeacon(101, 1), new MiniBeacon(101, 2)));

    private ArrayList<Integer> zoneOneTracks = new ArrayList<>(Arrays.asList(R.raw.ethel_george, R.raw.lizzie_brother));
    private ArrayList<Integer> zoneTwoTracks = new ArrayList<>(Arrays.asList(R.raw.kippers, R.raw.wild_flowers, R.raw.send_parcels_food, R.raw.shrapnel_wound));
    private ArrayList<Zone> zones = new ArrayList<>();

    private MediaPlayer zoneTunePlayer;
    private MediaPlayer noZonePlayer;

    private Beacon lastSeenBeacon;
    private int timesSeen = 0;
    private int notSeen = 0;
    private Zone lastZone = null;

    private Handler handler;
    private int rssi;

    public static float convertRange(int originalStart, int originalEnd, int newStart, int newEnd, int value) {
        double scale = (double) (newEnd - newStart) / (originalEnd - originalStart);
        float val = (float) (newStart + ((value - originalStart) * scale)) / 100;
        if (val > 1.0f)
            return 1.0f;
        else if (val < 0.0f)
            return 0.0f;
        else
            return val;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create the zones
        zones.add(new Zone("ice-zone", zoneOneBeacons, zoneOneTracks, getApplicationContext()));
        zones.add(new CenotaphZone("blueberry & mint zone", zoneTwoBeacons, zoneTwoTracks, getApplicationContext()));

        //the zone tuning - potentially, we could implement this into the zone by default
        zoneTunePlayer = MediaPlayer.create(getApplicationContext(), R.raw.tune_in_three);
        noZonePlayer = MediaPlayer.create(getApplicationContext(), R.raw.no_zone);
        noZonePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                noZonePlayer.start();
            }
        });

        handler = new Handler();

        region = new Region("Homing", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                Log.e(TAG, "times seen: " + timesSeen);

                //disTV.setText("not visible");
                if (!list.isEmpty()) {

                    Log.w(TAG, "beacons seen: " + list.size());

                    notSeen = 0;

                    Beacon firstSeenBeacon = list.get(0);
                    rssi = firstSeenBeacon.getRssi();
                    float volume = convertRange(-35, -100, 100, 10, rssi);

                    if (lastZone != null && lastZone.getName().equals("blueberry & mint zone")) {
                        Log.e(TAG, "volume " + volume);
                        lastZone.setVolume(volume);
                    }

                    if(lastSeenBeacon != null){
                        if(firstSeenBeacon.equals(lastSeenBeacon)){
                            ++timesSeen;

                            if (timesSeen == TIMES_UNTIL_CHANGE) {
                                for(final Zone zone : zones){
                                    for (MiniBeacon miniBeacon : zone.getMiniBeacons()) {
                                        if (miniBeacon.getMajor() == firstSeenBeacon.getMajor()) {
                                            if (lastZone != null) {
                                                if (!zone.getName().equals(lastZone.getName())) {

                                                    Log.d(TAG, "Moved to zone: " + zone.getName());

                                                    //pause the previous zone
                                                    lastZone.pauseTrack();

                                                    //start the new player
                                                    setAndStartZoneTunePlayer(zone);
                                                } else {
                                                    Log.d(TAG, "we've tuned into the same zone... " + zone.getName());
                                                }
                                            } else {
                                                //first time playing in this zone
                                                Log.e(TAG, "first time playing in the zone: " + zone.getName());
                                                setAndStartZoneTunePlayer(zone);
                                            }

                                            //assign this zone as the last seen zone
                                            lastZone = zone;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            timesSeen = 0;
                        }
                    }
                    lastSeenBeacon = firstSeenBeacon;
                }
                else{
                    if (notSeen++ == TIMES_UNTIL_CHANGE) {
                        if (lastZone != null)
                            lastZone.pauseTrack();
                        noZonePlayer.start();
                    }
                    //this is if the list is 0 - what is the desired algorithm?
                }
            }
        });
    }

    private void setAndStartZoneTunePlayer(final Zone zone) {
        zoneTunePlayer.start();
        zoneTunePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //play the new zone
                zone.playTrack();
            }
        });
    }

    private int getNextTimeInterval(){
        Random r = new Random();
        int low = 30;
        int high = 50;
        return r.nextInt(high - low) + low;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //bloody Android permissions
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //connect the beacon manager
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        //disconnect the beacon manager
        beaconManager.stopRanging(region);
        super.onPause();
    }
}
