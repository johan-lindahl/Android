package com.example.userlockout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener 
{	
	private static final String TAG = "MainActivity";
	private static final String ACTION_STARTED = "com.example.started";
			
	private static int _sInstanceCounter = 0;
	private static boolean _sExit = false;

	private Button btnExit;	
	
	// Boradcast receiver, detect multiple instances of the app and terminates
	private BroadcastReceiver _receiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			Log.d(TAG, "onReceive : " + intent.getAction());			
			if( intent.getAction().equals( ACTION_STARTED ))
			{
				String startedActivity = intent.getStringExtra("activity");				
				Log.d(TAG, "Started: " + startedActivity + ", this: " + MainActivity.this.toString());
				
				if(!MainActivity.this.toString().equals(startedActivity))
				{
					Log.d(TAG, "Exising, another thread was started");
					MainActivity.this.finish();
					return;				
				}
			}			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		_sInstanceCounter ++;		
		Log.d(TAG,  "onCreate : " + _sInstanceCounter);

		// Init the UI
		setContentView(R.layout.activity_main);		
		btnExit = (Button)findViewById(R.id.buttonExit);
		btnExit.setOnClickListener(this);		
				
		// Register the Broadcast receiver, used to detect multiple instances of the app				
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_STARTED);
		registerReceiver(_receiver, filter);
	}
		
	@Override
	public void onDestroy()
	{
		super.onDestroy();		
		unregisterReceiver(_receiver);
		
		_sInstanceCounter --;		
		Log.d(TAG,  "onDestroy : " + _sInstanceCounter);
		
		if( _sExit )
		{
			Log.d(TAG, "Exiting...");

			// Disable ourself as HOME screen and launch the default HOME
			disableMainActivity(this);
			Intent selector = new Intent("android.intent.action.MAIN");
			selector.addCategory("android.intent.category.HOME");
			startActivity(selector);
	        System.exit(0);
	        return;
		}		
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		// Set full screen and hide buttons
		getWindow().getDecorView().findViewById(android.R.id.content).setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN );
		
		// Broadcast activity started event so other running instances could exit
		Intent intent = new Intent(ACTION_STARTED);
		intent.putExtra("activity", this.toString());
		sendBroadcast(intent);		
	}
		
	@Override
	public void onPause()
	{
		super.onPause();
					
		if( !_sExit )
		{
			Log.d(TAG, "Relaunching, the recent button was probably pressed");
			Intent selector = new Intent("android.intent.action.MAIN");
			selector.addCategory("android.intent.category.HOME");
			selector.setFlags(Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			startActivity(selector);						
			System.gc();
		}	
	}
	
    @Override
    public void onAttachedToWindow() 
    {
    	 this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | 
    	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
    	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
    	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
    	            WindowManager.LayoutParams.FLAG_FULLSCREEN |     	            
    	            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
    	            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
    	            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    	 
    	 super.onAttachedToWindow();  	 
    }

    // Disable the back button
	@Override
	public void onBackPressed() 
	{
		getWindow().getDecorView().findViewById(android.R.id.content).setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN );
	}
		   
    // Disable long click on home button
    public void onWindowFocusChanged(boolean hasFocus) 
    {
    	super.onWindowFocusChanged(hasFocus);
     
    	if(!hasFocus) {
  
    		Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    		sendBroadcast(closeDialog);
    	}
    }
    
	@Override
	public void onClick(View v) 
	{
		_sExit = true;
		Intent intent = new Intent(this, ExitActivity.class);
		startActivity(intent);
		finish();		
	}
	
	private void disableMainActivity(Activity activity)
	{
		PackageManager pm = activity.getPackageManager();
		pm.setComponentEnabledSetting(
		        new ComponentName(activity, MainActivity.class),
		        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
		        PackageManager.DONT_KILL_APP);		
	}  
	
	public static void enableMainActivity(Activity activity)
	{
		PackageManager pm = activity.getPackageManager();
		pm.setComponentEnabledSetting(
		        new ComponentName(activity, MainActivity.class),
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);		

	}

	
}
