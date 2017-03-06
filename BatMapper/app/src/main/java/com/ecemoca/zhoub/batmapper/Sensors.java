package com.ecemoca.zhoub.batmapper;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahar on 3/1/2017.
 */
public class Sensors implements Runnable {
    private Context mContext;
    private SensorManager mSensorManager = null;
    private SensorEventListener mListener;
    private List<Sensor> currentDevice = new ArrayList<>();
    private float[] orientationVals = new float[3];
    private float[] accVals = new float[3];
    private float[] accLinearVals = new float[3];

    private float height;
    private float step;
    private int sensorScanRate;




    public Sensors(Context ctx, int sensorScanRate){
        mContext = ctx;
        this.sensorScanRate = sensorScanRate;
    }


    public void run() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR));
        else
            Toast.makeText(mContext, "Game rotation vector sensor not found!", Toast.LENGTH_LONG).show();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));


        mListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {

                    if (!Thread.currentThread().isInterrupted()){

                        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                            // Convert the rotation-vector to a 4x4 matrix.
                            float[] mRotationMatrix = new float[16];
                            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                            SensorManager.getOrientation(mRotationMatrix, orientationVals);
                            // Optionally convert the result from radians to degrees
                            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
                            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
                            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

                        }

                    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                        // Convert the rotation-vector to a 4x4 matrix.
                        float[] mRotationMatrix = new float[16];
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);
                        SensorManager.getOrientation(mRotationMatrix, orientationVals);
                        // Optionally convert the result from radians to degrees
                        orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
                        orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
                        orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);
                    }
                    if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {
                        accVals[0] = event.values[0];
                        accVals[1] = event.values[1];
                        accVals[2] = event.values[2];

                    }
                    if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)) {
                        accLinearVals[0] = event.values[0];
                        accLinearVals[1] = event.values[1];
                        accLinearVals[2] = event.values[2];
                    }
                    if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)) {
                        step += event.values[0];


                    }

                    if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)) {
                        float p = event.values[0];
                        height = (float) ((1 - Math.pow(p / 1013.25, 0.190284)) * 44307.69);

                    }


                }

        }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        if ( !Thread.currentThread().isInterrupted()){
            for (Sensor insert : currentDevice) {
                mSensorManager.registerListener(mListener, insert, sensorScanRate, 100);
            }
        }
    }
    public float[] getOrientationVals(){
        return orientationVals;
    }
    public float getStep(){
        return step;
    }
    public float getHeight(){
        return height;
    }
    public float[] getAccVals(){
        return accVals;
    }
    public float[] getAccLinearVals(){
        return accLinearVals;
    }

    public void stop(){

        for (Sensor insert : currentDevice) {
           mSensorManager.unregisterListener(mListener, insert);

        }
        currentDevice.clear();
        mListener = null;

    }


}
