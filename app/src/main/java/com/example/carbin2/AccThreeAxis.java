package com.example.carbin2;

import android.util.Log;

import java.sql.Timestamp;

public class AccThreeAxis {
    double x, y, z;
    //Timestamp tsTamp;
    String tsTamp;

    public AccThreeAxis(double x, double y, double z, String tsTamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tsTamp = tsTamp;

    }

    public AccThreeAxis() {
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getTsTamp() {
        Log.d("ts", tsTamp);
        return tsTamp.toString();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setTsTamp(String tsTamp) {
        this.tsTamp = tsTamp;
    }
}
