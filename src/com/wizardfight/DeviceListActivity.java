package com.wizardfight;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private LinearLayout mPairedList;
    private LinearLayout mNewDevicesList;
    private int mNewDevicesCount;
    private String mNoNewDevices;
    private String mNoPairedDevices;
    // Member object for bluetooth services
 	private BluetoothService mChatService = null;
 	private boolean mSearchCanceledByUser = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        setResult(Activity.RESULT_CANCELED);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
			Toast.makeText(this, R.string.bt_not_available,
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

        mNoNewDevices = getResources().getString(R.string.none_found);
        mNoPairedDevices = getResources().getString(R.string.none_paired);

        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        mPairedList = (LinearLayout) findViewById(R.id.paired_devices);
        
        mNewDevicesList = (LinearLayout) findViewById(R.id.new_devices);
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	addPairedDevice(device.getName() + "\n" + device.getAddress());
            }
        } else {
            addPairedDevice(mNoPairedDevices);
        }
    }
    
    @Override
	public void onStart() {
		super.onStart();
		if (mChatService == null) setup();
	}
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }
    
    private void setup() {
    	mChatService = BluetoothService.getInstance();
    	mChatService.init();
    }
    
    private void addPairedDevice(String s ) {
    	TextView v = (TextView) View.inflate(this, R.xml.device_list_item, null);
    	v.setOnClickListener(mDeviceClickListener);
    	v.setText(s);
    	mPairedList.addView(v);
    }
    private void addNewDevice(String s ) {
    	TextView v = (TextView) View.inflate(this, R.xml.device_list_item, null);
    	v.setOnClickListener(mDeviceClickListener);
    	v.setText(s);
    	mNewDevicesList.addView(v);
    }
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
    	mSearchCanceledByUser = false;
        if (D) Log.d(TAG, "doDiscovery()");
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    private final OnClickListener mDeviceClickListener = new OnClickListener() {
        public void onClick(View v) {
        	mSearchCanceledByUser = true;
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();
            
            String info = ((TextView) v).getText().toString();
            if( info.equals(mNoNewDevices) || info.equals(mNoPairedDevices)) {
            	return;
            }
            
            // parse MAC address
            String address = info.substring(info.length() - 17);
            // Get the BluetoothDevice object
            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            mChatService.connect(device);      
            // Start fighting activity
            startFight();
        }
    };
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    addNewDevice(device.getName() + "\n" + device.getAddress());
                    mNewDevicesCount++;
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesCount == 0) {
                	if(!mSearchCanceledByUser)
                		addNewDevice(mNoNewDevices);
                }
            }
        }
    };
    
    private void startFight() {
    	startActivity(new Intent(this, BtFightActivity.class));
    }
}
