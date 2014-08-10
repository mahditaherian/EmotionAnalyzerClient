package com.mtn.messages;

import com.mtn.entity.GpsLocation;

/**
 * @author Mahdi
 */
public class GpsLocationMessage extends SensorMessage {
    public GpsLocationMessage(GpsLocation location, long time) {
        super(location, time);
    }
}
