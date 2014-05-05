package nabak.nabakalarm;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nabak.movingview.MovingView;
import com.nbpcorp.mobilead.sdk.MobileAdListener;
import com.nbpcorp.mobilead.sdk.MobileAdView;

public class alarmReceiver2 extends Activity implements MobileAdListener {
	
	private MobileAdView adView = null;
	
	String mRingTone ="";
	MediaPlayer mMediaPlayer = null;
	Vibrator vibe = null;
	PowerManager.WakeLock wl = null;
	PowerManager pm;
	Calendar calendar;
	// Notification Manager ���
	private GestureDetector mGestures = null;
	private NotificationManager nm = null;
	
	boolean startFlag = false;
	
	//�˶� �޴���
	private AlarmManager mManager = null;
	
	View targetView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				
        setContentView(R.layout.alarm_receiver2);
        
		pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn()) { // ��ũ���� ���� ���� ������ �Ҵ�
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NabakAlarm");
			wl.acquire();
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//										WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
										WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
										WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			
		}
		
		
		mRingTone = getIntent().getStringExtra("ringtone");
		int vibrate = getIntent().getIntExtra("vibrate", 0);
		
		if (mRingTone == null || mRingTone.equals("")) {
			mRingTone = RingtoneManager.getValidRingtoneUri(this).toString();
			if (mRingTone == null) mRingTone = "";
		}
		
		showNotification(R.drawable.nabak_alarm_title, "�ῡ�� �Ͼ �ð��̾�!!", mRingTone, vibrate);
				
		clearAlarm();
		
		 ////////////////////////////////////////////////////////
	    // ����
	    ///////////////////////////////////////////////////////
	    adView = (MobileAdView)findViewById(R.id.adview1);
	    adView.setListener(this);
	    adView.start();
	    /////////////////////////////////////////////////////////
		
	}	
	//
	@Override
	protected void onResume() {
		super.onResume();
        
		MovingView v = (MovingView)findViewById(R.id.mv_image);
		targetView = findViewById(R.id.mv_target);
       
		v.setTarget(targetView, mvListener, true);
		setTargetPosition(targetView);        
	}
	//
    @Override
	protected void onStart() {
    	super.onStart();
    	
    }
    //
    //
    //
    // ��Ƽ��Ƽ�� ���鿡�� �������� ������
    //
    @Override
    protected void onPause() {
    	super.onPause();   	
    }
    //
    // ��Ƽ��Ƽ�� ȭ�鿡�� ������ �����
    //
    @Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onRestart() {
    	super.onRestart();
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	 NotificationManager nm = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
    	 nm.cancelAll();
	    ////////////////////////////////////////////////////////
	    // ����
	    ///////////////////////////////////////////////////////
    	if (adView != null) {
    		adView.destroy();
    		adView = null;
    	}
    	Utility.startFirstAlarm(this);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private void showNotification(int statusBarIconID, 
			String statusBatTextID, String ringtone, int vibrate){
		// Notification ��ü ����/����
		nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notifi = new Notification(R.drawable.nabak_alarm_title, null, System.currentTimeMillis());
		//����ڰ� ���� �� ���� ��� �︮���ϴ� FLAG��
//		notifi.flags |= Notification.FLAG_INSISTENT;
		
		playSound(Uri.parse(mRingTone));
	
		if (vibrate == 1) {	// ������ �����Ǿ� ������ ?
			vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
			long[] pattern = {200, 2000, 100, 1700, 200, 2000, 100, 1700, 200, 2000, 100, 1700};          //  ������, ���� ���̴�.
			vibe.vibrate(pattern, 2);                                 // ������ �����ϰ� �ݺ�Ƚ���� ����  ���� 2�� ��� �ݺ��̴�.
		}

		Intent intent = new Intent(this, alarmCancel.class);
		PendingIntent theappIntent = PendingIntent.getActivity(this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);		
		
		CharSequence from = "�˶��� �����Ϸ��� ������ �ٽ� Ŭ�� �� ��縦 ��ƾ� �������� �մϴ�. HomeKey�� ������ ������!";
		CharSequence message = "NabakAlarm";

		notifi.setLatestEventInfo(this, from, message, theappIntent);
		
		nm.notify(12345, notifi);	
	
		
	}

	private void playSound(Uri alert) {
		if (mMediaPlayer != null) return;
		
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(this, alert);
			final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.setLooping(true);
				mMediaPlayer.start();
			}
		} catch (IOException e) {

		}
	}

	private void clearAlarm() { 
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {   
			@Override
			public void run()   {
				if (mMediaPlayer != null) mMediaPlayer.stop();
				mMediaPlayer = null;
				//if (wl != null) wl.release();
				vibe.cancel();
				nm.cancel(12345);
				finish();
			} 
		}, 600000);
	}
	
	
	@Override
	 public boolean onTouchEvent(MotionEvent event) {
		if (mGestures != null) {
			return mGestures.onTouchEvent(event);
		} else {
			return super.onTouchEvent(event);
		}
	 }
    //
    // onKeyUp() - BackKey ó�� --> UP ��ư�� �����ϰ� ó��
    //
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		return true;
    	} else {
    	   	return super.onKeyUp(keyCode, event);
    	}
    }
	
	////////////////////////////////////////////////////////////////////////////////
	MovingView.MovingViewListener mvListener = new MovingView.MovingViewListener() {
		@Override
		public void onMatchTarget() {
			Toast.makeText(getBaseContext(), "Success", 2000).show();
			if(mMediaPlayer == null){
				
			} else {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				nm.cancel(12345);
			}				
			if(vibe == null){
				
			} else{
				vibe.cancel();
				nm.cancel(12345);
			}
	
			nm.cancel(12345);
			finish();
		}
		
	};
	
	///
		private void setTargetPosition(View tv) {
			int targetMargin = 80;
			
	    	// ���� ��ġ�� ȭ�� ũ�⸦ �˾Ƴ�
	        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	        int dispHeight = wm.getDefaultDisplay().getHeight();
	        int dispWidth = wm.getDefaultDisplay().getWidth();
	        // Target�� ��ġ�� random�ϰ� ����
	        int topMargin = (int)(Math.random()*(dispHeight - tv.getHeight() - targetMargin)); 
	        if (topMargin < targetMargin) topMargin = targetMargin;
	        int leftMargin = (int)(Math.random()*(dispWidth - tv.getWidth() - targetMargin));
	        if (leftMargin < targetMargin) leftMargin = targetMargin;
	        
	        tv.layout(leftMargin, topMargin, leftMargin + tv.getWidth(), topMargin + tv.getHeight());
	        tv.invalidate();
		}
		
		@Override
		public void onReceive(int err) {
			// TODO Auto-generated method stub
			
		}
	
}
	