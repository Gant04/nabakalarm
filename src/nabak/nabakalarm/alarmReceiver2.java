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
	// Notification Manager 얻기
	private GestureDetector mGestures = null;
	private NotificationManager nm = null;
	
	boolean startFlag = false;
	
	//알람 메니저
	private AlarmManager mManager = null;
	
	View targetView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				
        setContentView(R.layout.alarm_receiver2);
        
		pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn()) { // 스크린이 켜져 있지 않으면 켠다
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
		
		showNotification(R.drawable.nabak_alarm_title, "잠에서 일어날 시간이야!!", mRingTone, vibrate);
				
		clearAlarm();
		
		 ////////////////////////////////////////////////////////
	    // 광고
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
    // 액티비티가 전면에서 가려지기 시작함
    //
    @Override
    protected void onPause() {
    	super.onPause();   	
    }
    //
    // 액티비티가 화면에서 완전히 사라짐
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
	    // 광고
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
		// Notification 객체 생성/설정
		nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notifi = new Notification(R.drawable.nabak_alarm_title, null, System.currentTimeMillis());
		//사용자가 원할 때 까지 계속 울리가하는 FLAG값
//		notifi.flags |= Notification.FLAG_INSISTENT;
		
		playSound(Uri.parse(mRingTone));
	
		if (vibrate == 1) {	// 진동이 설정되어 있으면 ?
			vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
			long[] pattern = {200, 2000, 100, 1700, 200, 2000, 100, 1700, 200, 2000, 100, 1700};          //  무진동, 진동 순이다.
			vibe.vibrate(pattern, 2);                                 // 패턴을 지정하고 반복횟수를 지정  숫자 2가 계속 반복이다.
		}

		Intent intent = new Intent(this, alarmCancel.class);
		PendingIntent theappIntent = PendingIntent.getActivity(this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);		
		
		CharSequence from = "알람을 해제하려면 어플을 다시 클릭 후 룰루를 잡아야 해제가능 합니다. HomeKey를 누르지 마세요!";
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
    // onKeyUp() - BackKey 처리 --> UP 버튼과 동일하게 처리
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
			
	    	// 현재 장치의 화면 크기를 알아냄
	        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	        int dispHeight = wm.getDefaultDisplay().getHeight();
	        int dispWidth = wm.getDefaultDisplay().getWidth();
	        // Target의 위치를 random하게 설정
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
	