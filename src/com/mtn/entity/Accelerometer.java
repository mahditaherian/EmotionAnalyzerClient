package com.mtn.entity;

/**
 * @author Mahdi
 */
public class Accelerometer extends StandardSensorEntity {
    double gx, gy, gz;

    public Accelerometer() {
    }

    public Accelerometer(double gx, double gy, double gz) {
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
    }

    public double getGx() {
        return gx;
    }

    public void setGx(double gx) {
        this.gx = gx;
    }

    public double getGy() {
        return gy;
    }

    public void setGy(double gy) {
        this.gy = gy;
    }

    public double getGz() {
        return gz;
    }

    public void setGz(double gz) {
        this.gz = gz;
    }

    @Override
    public String toString() {
        return "Accelerometer{" +
                "gx=" + gx +
                ", gy=" + gy +
                ", gz=" + gz +
                '}';
    }
}
