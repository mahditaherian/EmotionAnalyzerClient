package com.mtn.messages;

import com.mtn.entity.StandardSensorEntity;

/**
 * @author Mahdi
 */
public class AccelerometerMessage extends SensorMessage {
    public AccelerometerMessage(StandardSensorEntity entity, long time) {
        super(entity, time);
    }



}
