package com.adriangradinar.homingbeacon.Classes;

/**
 * Created by adriangradinar on 21/04/2016.
 */
public class MiniBeacon {

    private int major;
    private int minor;

    public MiniBeacon(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }
}

