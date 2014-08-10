package com.mtn.messages;

import com.mtn.entity.StandardSensorEntity;

/**
 * @author Mahdi
 */
public class OrientationMessage extends SensorMessage {
    public OrientationMessage(StandardSensorEntity entity, long time) {
        super(entity, time);
    }
}
