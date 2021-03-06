package com.mtn.entity;

/**
 * @author Mahdi
 */
public class Orientation extends StandardSensorEntity {
    double x,y,z;

    public Orientation() {
    }

    public Orientation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Orientation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
