package com.hogervries.beaconscanner;

import android.support.annotation.DrawableRes;

/**
 * Trifork Netherlands.
 * GuestApp Social.
 * Mitchell de Vries & Mohammed Ali.
 */
public class LogItem {

    private int logo;
    private String action;
    private String timeStamp;

    public LogItem(int logo, String action, String timeStamp) {
        this.logo = logo;
        this.action = action;
        this.timeStamp = timeStamp;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
