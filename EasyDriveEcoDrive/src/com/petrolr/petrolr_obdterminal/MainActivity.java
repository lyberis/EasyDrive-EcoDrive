/*
 * Copyright (C) 2014 Petrolr LLC, a Colorado limited liability company
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 * Written by the Petrolr team in 2014. Based on the Android SDK Bluetooth Chat Example... matthew.helm@gmail.com
 */


package com.petrolr.petrolr_obdterminal;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.ls.easydrive_ecoddrive.DatabaseHandler;
import com.ls.easydrive_ecoddrive.GeneralParms;
import com.ls.easydrive_ecoddrive.Level;
import com.ls.easydrive_ecoddrive.TripDetail;
import com.ls.easydrive_ecoddrive.TripHeader;
import com.ls.easydrive_ecoddrive.UserParms;
import com.ls.easydrive_ecoddrive.AudioPlayer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "EasyDrive EcoDrive";
	
	private DatabaseHandler db;
		
	// Name of the connected device
	private String mConnectedDeviceName = null;
	
	// String buffer for outgoing messages
	private static StringBuffer mOutStringBuffer;
	
	// Local Bluetooth adapter
	static BluetoothAdapter mBluetoothAdapter = null;
	
	// Member object for the chat services
	private static BluetoothChatService mChatService = null;
	
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;
	
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	  
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	EditText edit_rpm,edit_throttle, edit_speed, edit_distance, edit_level;
	Button start_command;
	AsyncTaskRunner runner;
	boolean runflag = false;
	boolean readySend = true;
	boolean playSound = true;
	int statusThrot = 0; 
	int statusRPM = 0;
	ProgressBar progress_RPM, progress_THROTTLE;
	
	MyTimerTask myTask;
	Timer myTimer;	
	
	double sumRPM, sumThrot, sumSpeed;
	int cntRPM, cntThrot, cntSpeed;
	double ldistance, avgRpm, avgTHR;
	int currentVehicleSpeed = 0;
	Date startDate, endDate;
	
	boolean calibrJustEnded = false;
	
	AudioPlayer audPl;
	
	TripHeader tHeader;
	TripDetail tDetail;
	GeneralParms gParms = new GeneralParms();
	UserParms uParms = new UserParms();
	Level level;
	
	int gauge_aa = 0;
	int soundTimerDelay = 8000;
	
	ArrayList<TripHeader> calibAllTripHeadList = new ArrayList<TripHeader>();
	ArrayList<Level> levelRPMlist = new ArrayList<Level>();
	ArrayList<Level> levelTHRlist = new ArrayList<Level>();
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terminal_layout);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		
		db = new DatabaseHandler(this);
		
		try {
			db.createDataBase();
        } catch (IOException mIOException) {
            Log.e("MAIN Activity", mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
		
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Log.d(TAG, "Adapter: " + mBluetoothAdapter);
		
		audPl = new AudioPlayer(); 
		
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		// keep screen always on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		Intent serverIntent = null;
		
		switch (item.getItemId()) {
			case R.id.secure_connect_scan:
				// Launch the DeviceListActivity to see devices and do scan
				serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
				return true;
				
				
			default:
				return super.onOptionsItemSelected(item);
		}		
	}
	
	@Override
	public void onStart() {
		super.onStart();        
        
		edit_rpm = (EditText) findViewById(R.id.edit_rpm);
		edit_rpm.setEnabled(false);
				
		edit_throttle = (EditText) findViewById(R.id.edit_throttle);
		edit_throttle.setEnabled(false);
		
		edit_speed = (EditText) findViewById(R.id.edit_speed);
		edit_speed.setEnabled(false);
		
		edit_distance = (EditText) findViewById(R.id.edit_distance);
		edit_distance.setEnabled(false);
		
		edit_level = (EditText) findViewById(R.id.edit_level);
		edit_level.setEnabled(false);
		
		progress_RPM=(ProgressBar)findViewById(R.id.progress_rpm);		
		progress_THROTTLE=(ProgressBar)findViewById(R.id.progress_throttle);
		
		addListenerOnButton(); 

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		// Otherwise, setup the chat session
		} else {
			if (mChatService == null) setupChat();
		}
		
		start_command.setBackgroundColor(new Color().rgb(172,225,175));
		
		// Database initialization
		initialize_DB();
	}

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}	

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);
		Log.d("BT Handler SETUP ", "" +  mChatService.BTmsgHandler);
		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");   
	}	
	
	public void sendActualMessage(String message) {
		
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, "Δεν συνδέθηκε", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			readySend = false;
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);
			//LogWriter.write_info("Cmd: " + message);
			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			//mOutEditText.setText(mOutStringBuffer);
		}
	}
	
	public void sendMessage(String message) {
		boolean repeatSend = true;
		
		while(repeatSend){
			
			if (readySend){
				sendActualMessage(message);
				repeatSend = false;
			}
			
			if (repeatSend){
				SystemClock.sleep(20);
			}
		}
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.d("Terminal", "onActivityResult...");
		
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE_SECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK)connectDevice(data, true);
				break;
			case REQUEST_CONNECT_DEVICE_INSECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK)connectDevice(data, false);
				break;
			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so set up a chat session
					setupChat();
				} else {
					// User did not enable Bluetooth or an error occurred
					Log.d(TAG, "BT not enabled");
					Toast.makeText(this, "BT NOT ENABLED", Toast.LENGTH_SHORT).show();
					finish();
				}
		}
	}  
	
	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras()
				.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}
	
	private final Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					
					switch (msg.arg1) {
						case BluetoothChatService.STATE_CONNECTED:
							setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
							onConnect();
							break;
						case BluetoothChatService.STATE_CONNECTING:
							setStatus(R.string.title_connecting);
							break;
						case BluetoothChatService.STATE_LISTEN:
						case BluetoothChatService.STATE_NONE:
							setStatus(R.string.title_not_connected);
							break;
					}
					break;
				case MESSAGE_WRITE:
					break;
				case MESSAGE_READ:
					//StringBuilder res = new StringBuilder();
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer               
					String readMessage = new String(readBuf, 0, msg.arg1);
					read_answer(readMessage);			
					break;
				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(), "Σύνδεση με "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
							Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};		  
	
	public void read_answer(String readMessage){
		int value  = 0;
    	int value2 = 0;
    	String cmd = "";
    		
		readMessage = readMessage.replaceAll("\r", "");
		readMessage = readMessage.trim();
		LogWriter.write_info("Ans: " + readMessage);
		
		if (runflag){
			
			if (readMessage.contains("4111")){
					
				cmd = readMessage.substring(readMessage.indexOf("4111"));
				value = Integer.parseInt(cmd.substring(4,6), 16); 
				value = value*100/255;
					
				setThrotProgBar(Integer.valueOf(value).doubleValue(),"Θέση Πεταλούδας (Throttle) : ");
				
				if (currentVehicleSpeed > 0){
					sumThrot = sumThrot + value;
					cntThrot = cntThrot + 1;
					addTripDetail("THR",value);	
				}
				
			}
				
			if (readMessage.contains("410C")){
					
				cmd = readMessage.substring(readMessage.indexOf("410C"));
				value = Integer.parseInt(cmd.substring(4,6), 16); 
				value2 = Integer.parseInt(cmd.substring(6,8), 16);
				value = ((value*256)+value2)/4;
					
				setRpmProgBar(Integer.valueOf(value).doubleValue(),"Στροφές Κινητήρα RPM : ");
				
				if (currentVehicleSpeed > 0){
					sumRPM = sumRPM + value;
					cntRPM = cntRPM + 1;
					addTripDetail("RPM",value);
				}
				
			}
				
			if (readMessage.contains("410D")){
					
				cmd = readMessage.substring(readMessage.indexOf("410D"));
				value = Integer.parseInt(cmd.substring(4,6), 16); 
					
				setSpeedText(value, "Ταχύτητα Οχήματος (Km/h) : ");
				sumSpeed = sumSpeed + value;
				cntSpeed = cntSpeed + 1;
				
				currentVehicleSpeed = value;
				addTripDetail("SPD",value);
							
			}
		
		}
			
		if (readMessage.endsWith(">")){
			readySend = true;
		}
		
	}
	
	public void setThrotProgBar(double value, String s){
		
		if (uParms.getCalibr_Flag().equalsIgnoreCase("YES")){
			return;
		}
		
		Resources res = getResources();
		edit_throttle.setText(s + Double.valueOf(value).toString());
		int valueInt = Double.valueOf(value).intValue();
		progress_THROTTLE.setProgress(valueInt);
		
		if(valueInt <= uParms.getThr_Limit()){
			progress_THROTTLE.setProgressDrawable(res.getDrawable(R.drawable.myprogressbar));
			statusThrot = 0; //Green
		}else if (valueInt > uParms.getThr_Limit() && valueInt <= uParms.getThr_Limit() + uParms.getThr_Limit() * gParms.getThr_Limit_Y()){
			progress_THROTTLE.setProgressDrawable(res.getDrawable(R.drawable.myprogressbar_orange));
			statusThrot = 1; //Orange
		}else{
	    	progress_THROTTLE.setProgressDrawable(res.getDrawable(R.drawable.myprogressbar_red));
	    	statusThrot = 2; //Red
	    }
		
		playSound();
	}
	
	public void setRpmProgBar(double value, String s){
		
		if (uParms.getCalibr_Flag().equalsIgnoreCase("YES")){
			return;
		}
		
		Resources res = getResources();
		edit_rpm.setText(s + Double.valueOf(value).toString());
		int valueInt = Double.valueOf(value).intValue();
		progress_RPM.setProgress(valueInt);
		
		if(valueInt <= uParms.getRpm_Limit()){
			progress_RPM.setProgressDrawable(res.getDrawable(R.drawable.myprogressbar));
			statusRPM = 0; //Green
	    }else if (valueInt > uParms.getRpm_Limit() && valueInt <= uParms.getRpm_Limit() + uParms.getRpm_Limit() * gParms.getRpm_Limit_Y()){
			progress_RPM.setProgressDrawable(res.getDrawable(R.drawable.myprogressbar_orange));		
			statusRPM = 1; //Orange
		}else{
	    	progress_RPM.setProgressDrawable(res.getDrawable(R.drawable.myprogressbar_red));
	    	statusRPM = 2; //Red    	
	    }		
		
		playSound();
	}
	
	public void setSpeedText(double value, String s){
		
		edit_speed.setText(s + Double.valueOf(value).toString());	
		
	}
	
	public void setDistanceText(String s){
		
		if (cntSpeed > 0){
			endDate = new Date();
			double tripDurationSec = Long.valueOf((endDate.getTime()- startDate.getTime())/1000).intValue();
			double distance = Double.valueOf((sumSpeed/cntSpeed) * (tripDurationSec/3600)).doubleValue();
			edit_distance.setText(s + Double.valueOf(round(distance,2)).toString() );
			ldistance = round(distance,2);
		}else{
			edit_distance.setText(s + Double.valueOf(0).toString() );
			ldistance = 0;
		}
	}
	
	public void playSound(){
		
		if(runflag && playSound && uParms.getCalibr_Flag().equalsIgnoreCase("NO")){
			
			if (statusRPM == 2 || statusThrot == 2){
				playSound = false;
				audPl.play(MainActivity.this, R.raw.red);
				myTask = new MyTimerTask();
			    myTimer = new Timer();
			    myTimer.schedule(myTask, soundTimerDelay);
			}else if(statusRPM == 1 || statusThrot == 1){
				playSound = false;
				audPl.play(MainActivity.this, R.raw.orange);
				myTask = new MyTimerTask();
			    myTimer = new Timer();
			    myTimer.schedule(myTask, soundTimerDelay);
			}
    	}
		
	}
	
	public void addListenerOnButton() {
		start_command = (Button) findViewById(R.id.start_command);
		start_command.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				start_command.setEnabled(false);
				
				if (!runflag){
					
					Toast.makeText(getApplicationContext(), "Έναρξη Διαδρομής", Toast.LENGTH_SHORT).show();
					
					if (uParms.getCalibr_Flag().equalsIgnoreCase("NO")){
						audPl.play(MainActivity.this, R.raw.carstarts);
					}
										
					// Insert Trip Header Row 
					prepareNewTrip();
					// Read Levels
					getLevelsTable();
										
					start_command.setBackgroundColor(new Color().rgb(238,130,238));
					start_command.setText("Λήξη Διαδρομής");
					cntRPM   = 0; cntThrot = 0; cntSpeed = 0;
					sumRPM   = 0; sumThrot = 0; sumSpeed = 0;
					avgRpm   = 0; avgTHR   = 0; statusThrot = 0; statusRPM = 0;
					runflag = true;
					
					myTask = new MyTimerTask();
				    myTimer = new Timer();
				    myTimer.schedule(myTask, soundTimerDelay);
				    
					String sleeptime = "10";
			    	runner = new AsyncTaskRunner();
			    	runner.execute(sleeptime);
			    	startDate = new Date();
			    	edit_distance.setText("");
				}else{

					Toast.makeText(getApplicationContext(), "Λήξη Διαδρομής", Toast.LENGTH_SHORT).show();
					
					if (uParms.getCalibr_Flag().equalsIgnoreCase("NO")){
						audPl.play(MainActivity.this, R.raw.handbrake);
					}
					
					start_command.setBackgroundColor(new Color().rgb(172,225,175));
					start_command.setText("Έναρξη Διαδρομής");
					runflag = false;
					runner.cancel(true);
					
					SystemClock.sleep(100);
					
					if (cntThrot > 0){
						setThrotProgBar(round(sumThrot/cntThrot,2), "Μέσος Όρος Throttle : ");
					}
					if (cntRPM > 0){
						setRpmProgBar(round(sumRPM/cntRPM,2), "Μέσος Όρος RPM : ");
					}
					if (cntSpeed > 0){
						setSpeedText(round(sumSpeed/cntSpeed,2), "Μέσος Όρος Ταχύτητας : ");
					}
					setDistanceText("Απόσταση σε Km : ");
					
					// Insert Trip Header Row 
					insertTripHeaderRow();
					/// Check if Calibration period end
					checkCalibrationEnd();
					// calculate trip results
					calculateTripResults();
					
				}
				
				start_command.setEnabled(true);
			   
			}
		});
				
	}
	public void onConnect(){		
		
	}
	
	public void insertTripHeaderRow(){
		
		// Insert Trip Header Row
		db.openDataBase();
        
		avgRpm = round(sumRPM/cntRPM , 2);
		avgTHR = round(sumThrot/cntThrot , 2);
		
		tHeader.setAvg_Rpm(avgRpm);
		tHeader.setAvg_Spd(round(sumSpeed/cntSpeed , 2));
		tHeader.setAvg_Thr(avgTHR);
		tHeader.setCalibr_Flag(uParms.getCalibr_Flag());
		tHeader.setDistance(ldistance);
		String ts = LogWriter.getDate(LogWriter.timestamp(), "dd/MM/yyyy hh:mm:ss.SSS");
		tHeader.setEnd_Date(ts.substring(0, 10));
		tHeader.setEnd_Time(ts.substring(10, 19));
		tHeader.setLevel(uParms.getLevel());
		
        db.addTrip(tHeader);
        
	}
	
	public void checkCalibrationEnd(){
		
		calibrJustEnded = false;
		
		if (uParms.getCalibr_Flag().equalsIgnoreCase("YES")){
			
			// calculate the Sum of trip distances
			db.openDataBase();
	        double distSum = db.getSumDistance(gParms.getValid_Distance(), "YES");
	        
	        // count the total distance number
	        db.openDataBase();
	        int tripNum = db.getNumbOfTrips(gParms.getValid_Distance(), "YES");	
	        
	        if (distSum >= gParms.getCalibr_Dist() && tripNum >= gParms.getCalibr_Num() ){
	        	
	        	endCalibrationMode();
	        	
	        }
			
		}
		
	}
	
	public void endCalibrationMode(){
		
		audPl.play(MainActivity.this, R.raw.success);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Ολοκλήρωση Διαδρομής");
		alertDialogBuilder.setMessage("Συγχαρητήρια!!! \nΜόλις ολοκλήρωσες τη διαδικασία του Calibration."
			                    + "\nΜε τις επόμενες διαδρομές, προσπάθησε να ανέβεις όλο και μεγαλύτερο επίπεδο.");
		alertDialogBuilder.setPositiveButton("Ok", null);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
		// calculate calibration Averages
		db.openDataBase();
		calibAllTripHeadList = db.getAllTripHeader(uParms.getCalibr_Flag());
		
		double totalAvgRPM = 0;
		double totalAvgTHR = 0;
		
		int numValidCalib = 0;
		for (int i = 0; i < calibAllTripHeadList.size(); i++) {
		   	TripHeader th = calibAllTripHeadList.get(i);
		   	if(th.getDistance() >= gParms.getValid_Distance()){
		   		totalAvgRPM = totalAvgRPM + th.getAvg_Rpm();
		   		totalAvgTHR = totalAvgTHR + th.getAvg_Thr();
		   		numValidCalib = numValidCalib + 1;
		   	}
        }
		
		if (numValidCalib > 0) {
			totalAvgRPM = round(totalAvgRPM / numValidCalib , 2);
			totalAvgTHR = round(totalAvgTHR / numValidCalib , 2);
		}
		
		calibrJustEnded = true;
		
		// Calibration ENDs
    	db.openDataBase();
    	uParms.setCalibr_Flag("NO");
    	uParms.setLevel(1);
    	uParms.setRpm_Limit(totalAvgRPM);
    	uParms.setThr_Limit(totalAvgTHR);
    	uParms.setLevel_Rpm(1);
    	uParms.setLevel_Thr(1);
        db.updateUserParm(uParms);	 
        
        // Calculate user Levels and insert them into Levels table      
        double levelRpm = totalAvgRPM;
        double levelThr = totalAvgTHR;
       
        db.openDataBase();
	   	level = new Level("RPM", 1, levelRpm);
	   	db.addLevel(level);
	   	
	   	db.openDataBase();
	   	level = new Level("THR", 1, levelThr);
	   	db.addLevel(level);
	   	
        for (int i = 2; i <= 50; i++) {
		   	levelRpm = round(levelRpm - levelRpm * gParms.getLevel_Prc_RPM() , 2);
		   	db.openDataBase();
		   	level = new Level("RPM", i, levelRpm);
		   	db.addLevel(level);
		   	
		   	levelThr = round(levelThr - levelThr * gParms.getLevel_Prc_THR() , 2);
		   	db.openDataBase();
		   	level = new Level("THR", i, levelThr);
		   	db.addLevel(level);
        }
        
	}
	
	public void prepareNewTrip(){
		
		gauge_aa = 0;
		
		// Get max Trip AA 
		db.openDataBase();
		int aa = db.getMaxTrip();
	
        tHeader = new TripHeader();
        tHeader.setTrip_AA(aa+1);   
        String ts = LogWriter.getDate(LogWriter.timestamp(), "dd/MM/yyyy hh:mm:ss.SSS");
        tHeader.setStart_Date(ts.substring(0, 10));
        tHeader.setStart_Time(ts.substring(10, 19));
        
	}
	
	public void addTripDetail(String gauge_cod, double gauge_value){
		
		// Insert Trip Header Row	    
        gauge_aa++;
        String ts = LogWriter.getDate(LogWriter.timestamp(), "dd/MM/yyyy hh:mm:ss.SSS");
        
        tDetail = new TripDetail();
        tDetail.setTrip_AA(tHeader.getTrip_AA()); 
        tDetail.setGauge_AA(gauge_aa);
        tDetail.setTimestamp(ts);
        tDetail.setGauge_Cod(gauge_cod);
        tDetail.setGauge_Value(gauge_value);
        
        db.openDataBase();
        db.addDetail(tDetail);
        
	}
	
	public void initialize_DB(){
		
		if (gParms.getRpm_Limit_Y() == 0){ 	// first time 
			
			//Select General Parameters
			db.openDataBase();
			gParms = db.getGeneralParms();
			
			if (gParms.getRpm_Limit_Y() == 0){ 	//not found in DB
				
				// initialize general parameters table
				gParms.setRpm_Limit_Y(new Double(0.20));
				gParms.setThr_Limit_Y(new Double(0.20));
				gParms.setLevel_Prc_RPM(new Double(0.01));
				gParms.setLevel_Prc_THR(new Double(0.005));
				gParms.setCalibr_Dist(3);  			
				gParms.setCalibr_Num(2);  			
				gParms.setValid_Distance(1);  		
				
				db.openDataBase();
				db.addGeneralParms(gParms);
	            
				// initialize user parameters table
				uParms = new UserParms(0,0,0,"YES",0,0);
					
				db.openDataBase();
				db.addUserParms(uParms);
							
			} else {
				
				// Select User Parameters
				db.openDataBase();
				uParms = db.getUserParms();
				
			}
		
		}
		
		edit_level.setText("Το Επίπεδο σου : " + uParms.getLevel());
	}
	
	private void getLevelsTable(){
		
		if (uParms.getCalibr_Flag().equalsIgnoreCase("NO") && levelRPMlist.size() == 0){
			
			db.openDataBase();
			levelRPMlist = db.getAllLevels("RPM");
			
			db.openDataBase();
			levelTHRlist = db.getAllLevels("THR");
			
		}
			
	}
	
	private void calculateTripResults(){
		int newRpmLevel = 1;
		int newThrLevel = 1;
		double newRpmLimit = uParms.getRpm_Limit();
		double newThrLimit = uParms.getThr_Limit();
			
		if (uParms.getCalibr_Flag().equalsIgnoreCase("NO") && tHeader.getDistance() >= gParms.getValid_Distance() && calibrJustEnded == false){
			
			for (int i = 0; i < levelRPMlist.size(); i++) {
			   	Level lvl = levelRPMlist.get(i);
			   	if (tHeader.getAvg_Rpm() <= lvl.getLimit()){
			   		newRpmLevel = lvl.getLevel();
			   		newRpmLimit = lvl.getLimit();
			   	}
	        }
			
			for (int i = 0; i < levelTHRlist.size(); i++) {
			   	Level lvl = levelTHRlist.get(i);
			   	if (tHeader.getAvg_Thr() <= lvl.getLimit()){
			   		newThrLevel = lvl.getLevel();
			   		newThrLimit = lvl.getLimit();
			   	}
	        }
			
			int newLevel = newRpmLevel + newThrLevel - 1;
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("Ολοκλήρωση Διαδρομής");
			
			if (newLevel == uParms.getLevel()){
				// same level
				alertDialogBuilder.setMessage("Καλή Προσπάθεια. Με λίγο ακόμα προσοχή, σίγουρα θα καταφέρεις να περάσεις στο επόμενο επίπεδο. "
						                    + "\nΠαραμένεις στο ίδιο επίπεδο.");
				audPl.play(MainActivity.this, R.raw.hmmmm);
			}else {
				
				if (newLevel > uParms.getLevel()){
					//Promote level
					alertDialogBuilder.setMessage("Συγχαρητήρια!!! Τα κατάφερες πολύ καλά. Πέρασες σε επόμενο επίπεδο. Συνέχισε έτσι."
							                    + "\nΠροβιβάζεσαι στο επίπεδο : " + newLevel);
					audPl.play(MainActivity.this, R.raw.clapping);
				} else {
					//Demote level
					alertDialogBuilder.setMessage("Δυστυχώς, η οδήγηση σου δεν ήταν και τόσο οικονομική. Θα πρέπει να προσπαθήσεις περισσότερο."
							                    + "\nΥποβιβάζεσαι στο επίπεδο : " + newLevel);
					audPl.play(MainActivity.this, R.raw.crowdgroan);
				}
				
				db.openDataBase();
		    	uParms.setCalibr_Flag("NO");
		    	uParms.setLevel(newLevel);
		    	uParms.setRpm_Limit(newRpmLimit);
		    	uParms.setThr_Limit(newThrLimit);
		    	uParms.setLevel_Rpm(newRpmLevel);
		    	uParms.setLevel_Thr(newThrLevel);
		        db.updateUserParm(uParms);	
		           
			}
					
			edit_level.setText("Το Επίπεδο σου : " + uParms.getLevel());
		
			alertDialogBuilder.setPositiveButton("Ok", null);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			
		}else {
		
			if (uParms.getCalibr_Flag().equalsIgnoreCase("NO") && calibrJustEnded == false){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder.setTitle("Ολοκλήρωση Διαδρομής");
				alertDialogBuilder.setMessage("Χρησιμοποιήστε την εφαρμογή για μεγαλύτερες διαδρομές. "
					                     	+ "(Όριο = " + gParms.getValid_Distance() + " km)");
				alertDialogBuilder.setPositiveButton("Ok", null);
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
	    
		}
		
	}
	

	private class AsyncTaskRunner extends AsyncTask<String, String, String> {

		  private String resp;

		  @Override
		  protected String doInBackground(String... params) {
			   
			   try {
				   
				   publishProgress(params[0]); // Calls onProgressUpdate()
				   
				   // Do your long operations here and return the result
				   int time = Integer.parseInt(params[0]); 
				   
				   sendMessage("ATD"    + '\r');  	// set all to Defaults
				   sendMessage("ATZ"    + '\r'); 	// reset all
				   sendMessage("ATE0"   + '\r');	// Echo Off
				   sendMessage("ATL0"   + '\r');	// Linefeeds Off
				   sendMessage("ATS0"   + '\r');	// printing of Spaces Off	
				   sendMessage("ATH0"   + '\r');	// Headers Off
				   sendMessage("ATSP00" + '\r');	// Set Protocol to Auto and save it 
				   
				   //sendMessage("0131" 	+ '\r');	// Distance traveled since codes cleared
				   
				   while(runflag){	 
					   sendMessage("010D" + '\r'); //get Vehicle speed	   
					   sendMessage("0111" + '\r'); //get throttle
					   sendMessage("010C" + '\r'); //get RPM
				   }
				   
				   //sendMessage("0131" 	+ '\r');	// Distance traveled since codes cleared
				   
			   } catch (Exception e) {
				   e.printStackTrace();
				   resp = e.getMessage();
			   }
			   return resp;
		  }

		  
		  @Override
		  protected void onPostExecute(String result) {
			   // execution of result of Long time consuming operation
		  }

		  @Override
		  protected void onPreExecute() {
			   // Things to be done before execution of long running operation. For
			   // example showing ProgessDialog				
		  }

		  @Override
		  protected void onProgressUpdate(String... text) {
			   // Things to be done while execution of long running operation is in
			   // progress. For example updating ProgessDialog	   
		  }
		  
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	class MyTimerTask extends TimerTask {
		public void run() {
			playSound = true;
		}
	}
}


