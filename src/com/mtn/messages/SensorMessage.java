package com.mtn.messages;

import com.mtn.entity.StandardSensorEntity;

import java.io.Serializable;

/**
 * @author Mahdi
 */
public abstract class SensorMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private long time;

    private StandardSensorEntity entity;

    public SensorMessage(StandardSensorEntity entity, long time) {
        this.entity = entity;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public StandardSensorEntity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "time=" + time +
                ", entity=" + entity +
                '}';
    }
}
