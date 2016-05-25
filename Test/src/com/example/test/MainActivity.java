package com.example.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Context;

import android.hardware.Sensor;

import android.hardware.SensorEvent;

import android.hardware.SensorEventListener;

import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer;
	private ArrayList<Double> mDeltaValues = null;
	private double mAvgValue;
	private double kFilteringFactor = 0.1;
	private double kThreshold = 1.2;
	private ArrayList<Double> mValues = null;
	private double mBumpTime = 0.0; //seconds
	private String mTag = "MainActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mValues = new ArrayList<Double>(21);
        mDeltaValues = new ArrayList<Double>(20);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    // can be safely ignored for this demo

    }
    
    @Override

    public void onSensorChanged(SensorEvent event) {
    	float x = event.values[0];
    	float y = event.values[1];
    	float z = event.values[2];
    	
        double value = Math.sqrt(x * x + y * y + z * z)/10;

        if (mDeltaValues.size() > 0) {
        	double lastValue = mDeltaValues.get(mDeltaValues.size() - 1).doubleValue();
        	mAvgValue = lastValue - ( (lastValue * kFilteringFactor) +
                                                                      (mAvgValue * (1.0 - kFilteringFactor)) );
        }
       
        if (mValues.size() > 0) {
            mDeltaValues.add(Double.valueOf(value - mValues.get(mValues.size() - 1).doubleValue()));
        }
        
        mValues.add(Double.valueOf(value));
        
        if (mDeltaValues.size() > 20) {
            mDeltaValues.remove(0);
        }
        
        if (mValues.size() > 21) {
        	mValues.remove(0);
        }

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        
        if (mAvgValue > kThreshold && now.getTime()/1000 > mBumpTime + 0.2) {
        	mBumpTime = now.getTime()/1000;
            Log.e(mTag, "Bump, mAvgValue="+mAvgValue);
            Toast.makeText(getApplicationContext(), "BumpTime in seconds="+mBumpTime, 
            		   Toast.LENGTH_SHORT).show();
        }
    
    }
}
