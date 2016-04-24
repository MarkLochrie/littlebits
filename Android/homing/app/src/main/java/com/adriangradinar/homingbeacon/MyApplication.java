package com.adriangradinar.homingbeacon;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

/**
 * Created by adriangradinar on 16/03/2016.
 * It creates an Application class to allow for the monitoring of them beacons :)
 */
public class MyApplication extends Application{

    private static final String APP_ID = "media-innovation-studio-s--lfv";
    private static final String APP_TOKEN = "1438476a0440f62a4c3ae56a6663e5eb";

    @Override
    public void onCreate() {
        super.onCreate();
        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), APP_ID, APP_TOKEN);
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);
    }
}