package nabak.nabakalarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nabak.movingview.MovingView;

public class alarmCancel extends Activity {

//	private int YOURAPP_NOTIFICATION_ID;
	String mRingTone ="";
	MediaPlayer mMediaPlayer = null;
	Vibrator vibe = null;
	PowerManager.WakeLock wl = null;
	PowerManager pm;
	Calendar calendar;
	// Notification Manager 얻기
	private GestureDetector mGestures = null;
	private NotificationManager nm = null;
	
	View targetView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				
        setContentView(R.layout.alarm_cancel);
		//Toast.makeText(context, R.string.app_name, Toast.LENGTH_SHORT).show();
		
		//int vibrate = getIntent().getIntExtra("vibrate", 0);
		
	}	
	
	 @Override
	 public void onDestroy() {

	  NotificationManager nm = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
	  nm.cancelAll();
	  super.onDestroy();
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
	
	@Override
	protected void onResume() {
		super.onResume();
        
		MovingView v = (MovingView)findViewById(R.id.mv_image);
		targetView = findViewById(R.id.mv_target);
       
		v.setTarget(targetView, mvListener, true);
		setTargetPosition(targetView);        
	}
	////////////////////////////////////////////////////////////////////////////////
	MovingView.MovingViewListener mvListener = new MovingView.MovingViewListener() {
		@Override
		public void onMatchTarget() {
			Toast.makeText(getBaseContext(), "Success", 2000).show();
			if(mMediaPlayer == null ){
				
			} else {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				nm.cancel(1234);
			}
			if(vibe == null){
				
			} else{
				vibe.cancel();
				nm.cancel(1234);
			}
			nm.cancel(1234);
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
	
}
