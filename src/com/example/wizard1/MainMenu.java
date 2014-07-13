package com.example.wizard1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainMenu extends Activity implements SensorEventListener, OnClickListener {
    
	SensorManager manager;
	Sensor sensor;
	Button button;
	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Fullscreen*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_menu);
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        button = (Button)findViewById(R.id.buttonCalibrate);
        button.setOnClickListener(this);
    }
    public void goToGameCreate(View view) {
       // if (isBluetoothOn())
    	Intent i = new Intent(this, WizardFight.class);
    	i.putExtra("calib", calib);
        startActivity(i);
    }
    public void goToListOfBluetooth(View view){
//        startActivity(new Intent(this,listOfBluetooth.class));
    }

    public void goToHelp(View view) {
//        startActivity(new Intent(this, Spellbook.class));
    }

    public void goToSpellbook(View view) {
        startActivity(new Intent(this, Spellbook.class));
    }

    public void Exit(View view) {
        finish();
    }
    
    double calib;
    int calibN;
    boolean isCalibration=false;
    public void calibrate() {
    	if(isCalibration) {
    		manager.unregisterListener(this, sensor);
    		calib /= calibN;
    		Log.d("recognition", ""+calib);
    		button.setText("Calibrate");
    	} else {
    		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    		calib = 0;
    		calibN = 0;
    		button.setText("Calibration...");
    	}
    	isCalibration = !isCalibration;
    }
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		calibN++;
		calib+=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
	}

    private boolean isBluetoothOn() {
        BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
        if ((a == null) || (!a.isEnabled())) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 2);
        }
        if ((a == null) || (!a.isEnabled())) {
            //findViewById(R.id.textErrorBluetooth).setVisibility(View.VISIBLE);
            return false;
        }
        //findViewById(R.id.textErrorBluetooth).setVisibility(View.INVISIBLE);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

            }
        }
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		calibrate();
	}

}