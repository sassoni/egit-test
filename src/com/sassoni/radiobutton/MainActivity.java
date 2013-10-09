package com.sassoni.radiobutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

	Intent svc;
	TextView notificationMsg;
	String myStationName = "My station";
	String myStationUrl = "http://streamx2.greekradios.gr:8000/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		notificationMsg = (TextView) findViewById(R.id.textView1);
		svc = new Intent(this, MyService.class);
		registerReceiver(broadcastReceiver, new IntentFilter(MyService.BROADCAST_ACTION));
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context content, Intent intent) {

			String action = intent.getStringExtra("action");
			String station = intent.getStringExtra("station");

			notificationMsg.setText(action + "\n" + station + " ...");
		}
	};

	public void startOrStop(View btn) {

		if (btn.getContentDescription().equals("start")){
			svc.putExtra("station", myStationName);
			svc.putExtra("link", myStationUrl);
			startService(svc);
			btn.setContentDescription("stop");
		}
		else {
			stopService(svc);
			btn.setContentDescription("start");
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(broadcastReceiver);
	}
	

}
