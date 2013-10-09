package com.sassoni.radiobutton;

import java.io.IOException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class MyService extends Service{

	NotificationManager nm;
	MediaPlayer player;
	Context context;
	String station;
	Intent broadcastIntent;
	String url;
	public static final String BROADCAST_ACTION = "com.sassoni.radiobutton.displayevent";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public void onCreate() {
		super.onCreate();

		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);	

		context = this;

		broadcastIntent = new Intent(BROADCAST_ACTION);	

		player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				broadcastIntent.putExtra("action", "Stream error for");
				broadcastIntent.putExtra("station", station);
				sendBroadcast(broadcastIntent);

				player.reset();
				return true;
			}

		} );

		player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {

				player.start();

				broadcastIntent.putExtra("action", "Playing");
				broadcastIntent.putExtra("station", station);
				sendBroadcast(broadcastIntent);

				Intent notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle("RadioButton: Playing " + station)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(contentIntent)
				.build();
				nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				noti.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR; 
				nm.notify(42, noti);

			}
		});

	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		Bundle extras = intent.getExtras(); 
		station = (String) extras.getString("station");
		url = (String) extras.getString("link");

		try {
			player.reset();
			player.setDataSource(url);

			broadcastIntent.putExtra("action", "Buffering");
			broadcastIntent.putExtra("station", station);
			sendBroadcast(broadcastIntent);

			player.prepareAsync();  


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return(START_STICKY);
	}

	@Override
	public void onDestroy() {

		broadcastIntent.putExtra("action", "Stopped");
		broadcastIntent.putExtra("station", station);
		sendBroadcast(broadcastIntent);

		stopPlayer(); 
	}

	private void stopPlayer() {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
			}
			player.reset();

			player.release();
			player = null;
		}	

		if (nm != null){
			nm.cancel(42);
		}
	}

}
