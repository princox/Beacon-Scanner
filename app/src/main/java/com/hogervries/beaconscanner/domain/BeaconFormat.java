package com.hogervries.beaconscanner.domain;

/**
 * Beacon Scanner, file created on 11/03/16.
 *
 * Enumeration of beacon formats.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public enum BeaconFormat {

    APPLE_BEACON("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"),
    SAMSUNG_BEACON("m:2-3=0203,i:14-19l,d:10-13,p:9-9");

    private String mFormat;

    BeaconFormat(String format) {
        mFormat = format;
    }

    public String getFormat() {
        return mFormat;
    }
}
