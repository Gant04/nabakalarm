package nabak.nabakalarm;

import java.util.Calendar;

import com.nbpcorp.mobilead.sdk.MobileAdListener;
import com.nbpcorp.mobilead.sdk.MobileAdView;

import kankan.wheel.widget.NumericWheelAdapter;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class afternoonAlarm extends Activity implements MobileAdListener{
/// 알람 관련 멤버 변수 ////
	
	private MobileAdView adView = null;
	//ringtone 변수
	private String strRingTone = "";
	
	int afternoonAlarmId = 123456;
	//요일
//	private String m_week;
	//현재 시간
//	private long nowTime;
	//알람 메니저
	private AlarmManager mManager = null;
	//시각 설정 클래스
	private TimePicker mTime;
	//달력
	public Calendar calendar;
	//진동
	private int mVibrate = 0;
	
	// Time changed flag
	private boolean timeChanged = false;	
	//
	private boolean timeScrolled = false;
	
//	private Ringtone ringtone = null;
	
//	MediaPlayer mMediaPlayer = null;
//	
//	Vibrator vibrate = null;

    private int mAlarmHour = 12;
    private int mAlarmMinute = 0;
	//private GregorianCalendar mCalendar;

	///////////////////// notification member value(통지 관련 멤버 변수) ///////////////////////
	private NotificationManager mNotification;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		calendar = Calendar.getInstance();
		
		//통지 매니저를 흭득
		mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);		
		//알람 매니저를 취즉
		mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		setContentView(R.layout.afternoon_alarm);
		
		final WheelView hours = (WheelView) findViewById(R.id.hour);
		hours.setAdapter(new NumericWheelAdapter(0, 23));
		hours.setLabel("hours");
		hours.setCyclic(true);
		
	
		final WheelView mins = (WheelView) findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		mins.setLabel("mins");
		mins.setCyclic(true);
		
		((TextView)findViewById(R.id.ringtone)).setOnClickListener(ringSelectBtnListener);
//		((TextView)findViewById(R.id.alarm_set_l2_value)).setOnClickListener(ringSelectBtnListener);
//		((TextView)findViewById(R.id.alarm_set_l2_click)).setOnClickListener(ringSelectBtnListener);
		
		//진동	
		if (mVibrate == 0) {
        	((CheckBox)findViewById(R.id.alarm_set_vibrate)).setChecked(false);
        } else {
        	((CheckBox)findViewById(R.id.alarm_set_vibrate)).setChecked(true);        	
        }

		//현재 시각을 취즉
		//Log.i("낮잠 고만 자자!!", calendar.getTime().toString());
		
		//셋 버튼을 등록 (확인 버튼)
		Button button = (Button)findViewById(R.id.set);
		button.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			setAlarm();
		}
		});
		//알람 해제 버튼
		button = (Button)findViewById(R.id.cancel);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancelAlarm();
			}
		});
		
		//시각 설정 클래스로 현재 시각을 설정
		mTime = (TimePicker)findViewById(R.id.time_picker);
		//mTime.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		//mTime.setCurrentMinute(calendar.get(Calendar.MINUTE));
		int curHours = calendar.get(Calendar.HOUR_OF_DAY);
		int curMinutes = calendar.get(Calendar.MINUTE);
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);
		mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker  view, int hourOfDay, int minute) {
				calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		        calendar.set(Calendar.MINUTE, minute);
		        calendar.set(Calendar.SECOND, 0);
		        mAlarmHour = hourOfDay;
		        mAlarmMinute = minute;
				if (!timeChanged) {
					hours.setCurrentItem(hourOfDay, true); //
					mins.setCurrentItem(minute, false);//
				}
				
			}
		});
		
		//진동모드나 사일런트 모드일 때도 벨 울리게
		AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT //: 사일런트 모드일 경우(값0)
				|| mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){ //: 진동모드일 경우(값1))
			int maxVolume =  mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
			//for(int i=1; i<=maxVolume; i++){
			//      mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,  AudioManager.ADJUST_RAISE, 0);
			//  }
		}
		
		//mTime.setOnTimeChangedListener(timeChangedListeners);

		// add listeners
		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");
		
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					timeChanged = true;	//
					mTime.setCurrentHour(hours.getCurrentItem());
					mTime.setCurrentMinute(mins.getCurrentItem());
					timeChanged = false;
				}
			}
		};
		
		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;//
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				timeChanged = true; //
				mTime.setCurrentHour(hours.getCurrentItem());
				mTime.setCurrentMinute(mins.getCurrentItem());
				timeChanged = false;
			}
		};
		
		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
	
	    ////////////////////////////////////////////////////////
	    // 광고
	    ///////////////////////////////////////////////////////
	    adView = (MobileAdView)findViewById(R.id.adview1);
	    adView.setListener(this);
	    adView.start();
	    /////////////////////////////////////////////////////////		
		
	}
	
    @Override
	protected void onDestroy() {
    	super.onDestroy();
	    ////////////////////////////////////////////////////////
	    // 광고
	    ///////////////////////////////////////////////////////
    	if (adView != null) {
    		adView.destroy();
    		adView = null;
    	}
    }
	
    
    
	//알람 최종 설정
	private void setAlarm() {
		// TODO Auto-generated method stub
		/*
		Log.d("alarm", "낮잠에서 일어 나야 할시간 = " + calendar.getTime().toString() + " : " + calendar.getTimeInMillis());
		Log.d("alarm", " 현재시간 = " + Calendar.getInstance().getTime().toString() + " : " + Calendar.getInstance().getTimeInMillis());
		
		if(Calendar.getInstance().getTimeInMillis() >= calendar.getTimeInMillis()){
			Toast.makeText(afternoonAlarm.this, "입렵한 시간은 현재 시간 보다 이전 시간 입니다.", Toast.LENGTH_SHORT).show();
			return ;
		}
		*/
		//진동 설정
		if (((CheckBox)findViewById(R.id.alarm_set_vibrate)).isChecked()) {
    		mVibrate = 1;
    	} else{
    		mVibrate = 0;
    	}
		//alarmReceiver 클래스로 보내주기 ringtone, vibrate
		//Intent intent = new Intent(getApplicationContext(), alarmReceiver.class);
		Intent intent = new Intent(this, alarmReceiver.class);
		intent.putExtra("ringtone", strRingTone);
		intent.putExtra("vibrate", mVibrate);
		
		//PendingIntent sender = PendingIntent.getBroadcast(afternoonAlarm.this, 0, intent, 0);
		
		PendingIntent sender = PendingIntent.getActivity(afternoonAlarm.this, afternoonAlarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		mManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		mManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, sender);
		
		Toast.makeText(afternoonAlarm.this, "알람 설정 시간 " + calendar.getTime().toString(), Toast.LENGTH_LONG).show();
		((TextView)findViewById(R.id.tell_time)).setText("" + mAlarmHour + " : " + mAlarmMinute);
	}
	
	//알람 해제
	private void cancelAlarm() {
		// TODO Auto-generated method stub
		
		((TextView)findViewById(R.id.tell_time)).setText("");
		Intent intent = new Intent(afternoonAlarm.this, alarmReceiver.class);
        PendingIntent sender = PendingIntent.getActivity(afternoonAlarm.this,
        		afternoonAlarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT );
        
        // And cancel the alarm.
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
		mManager.cancel(sender);
		mNotification.cancel(1234);
		Toast.makeText(afternoonAlarm.this, "알람이 해제됐습니다.", Toast.LENGTH_SHORT).show();
	}
	
	 // Ringtone Manager 에게 선택 메세지 전송
    private OnClickListener ringSelectBtnListener = new OnClickListener() {
        @Override
		public void onClick(View v) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
            startActivityForResult(intent, 123456);
        }
    };
	//ringtone manager 벨소리 선택 메니져
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	  	if (requestCode == 123456 && resultCode == RESULT_OK) {
    		Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
    		if (uri != null) {
    		//	RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, uri);
    			strRingTone = uri.toString();
    	        // ringtone 이름을 표시
    			Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
//    	        ((TextView)findViewById(R.id.alarm_set_l2_value)).setText(ringtone.getTitle(this));
    	        String value = "벨소리" + "\n" +  ringtone.getTitle(this);
    			SpannableStringBuilder ssb = new SpannableStringBuilder();
    			ssb.append(value);
    			ssb.setSpan(new ForegroundColorSpan(0xFFf4A460), 3, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	        ((TextView)findViewById(R.id.ringtone)).setText(ssb);
    		}
    	}
    }	
	
	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * @param wheel the wheel
	 * @param label the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

	@Override
	public void onReceive(int arg0) {
		// TODO Auto-generated method stub
		
	}

}