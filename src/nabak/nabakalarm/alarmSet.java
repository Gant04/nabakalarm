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

public class alarmSet extends Activity implements MobileAdListener {
	// �˶� ���� ��� ���� ////
	
	private MobileAdView adView = null;
	
		//ringtone ����
		private String strRingTone = "";
	
		//�˶� �޴���
		private AlarmManager mManager = null;
		//�ð� ���� Ŭ����
		private TimePicker mTime;
		//�޷�
		public Calendar calendar;
		//����
		private int mVibrate = 0;
		
		// Time changed flag
		private boolean timeChanged = false;	
		//
		private boolean timeScrolled = false;
		
	    private int mAlarmHour = 12;
	    private int mAlarmMinute = 0;
		//private GregorianCalendar mCalendar;

	    private long db_id = -1;
		///////////////////// notification member value(���� ���� ��� ����) ///////////////////////
		private NotificationManager mNotification;
		
		
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
				
			calendar = Calendar.getInstance();
			
			//���� �Ŵ����� ŉ��
			mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);		
			//�˶� �Ŵ����� ����
			mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			
			setContentView(R.layout.alarmset);
			//
			final WheelView hours = (WheelView) findViewById(R.id.hour);
			hours.setAdapter(new NumericWheelAdapter(0, 23));
			hours.setLabel("hours");
			hours.setCyclic(true);
			
		
			final WheelView mins = (WheelView) findViewById(R.id.mins);
			mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
			mins.setLabel("mins");
			mins.setCyclic(true);
			
			
			((TextView)findViewById(R.id.ringtone)).setOnClickListener(ringSelectBtnListener);
			
			//�� ��ư�� ��� (Ȯ�� ��ư)
			Button button = (Button)findViewById(R.id.set);
			button.setOnClickListener(new View.OnClickListener(){
				
				public void onClick(View v) {
					setAlarm();
				}
			});
			//button.setEnabled(false);
			//�ð� ���� Ŭ������ ���� �ð��� ����
			mTime = (TimePicker)findViewById(R.id.time_picker);
			//mTime.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			//mTime.setCurrentMinute(calendar.get(Calendar.MINUTE));
			int curHours = calendar.get(Calendar.HOUR_OF_DAY);
			int curMinutes = calendar.get(Calendar.MINUTE);
			hours.setCurrentItem(curHours);
			mins.setCurrentItem(curMinutes);
/*			
			mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
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
*/			
			//������峪 ���Ϸ�Ʈ ����� ���� �� �︮��
			AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT //: ���Ϸ�Ʈ ����� ���(��0)
					|| mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){ //: ��������� ���(��1))
				int maxVolume =  mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
				mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
	//			for(int i=1; i<=maxVolume; i++){
	//			      mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING,  AudioManager.ADJUST_RAISE, 0);
	//			  }
			}
			
			//mTime.setOnTimeChangedListener(timeChangedListeners);

			// add listeners
			addChangingListener(mins, "min");
			addChangingListener(hours, "hour");
			
			OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
				
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
				
				public void onScrollingStarted(WheelView wheel) {
					timeScrolled = true;//
				}
			
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
			
			db_id = getIntent().getLongExtra("id", -1);
			if(db_id != -1){
				mAlarmHour = getIntent().getIntExtra("hour", 12);
				mAlarmMinute = getIntent().getIntExtra("min", 33);
				mVibrate = getIntent().getIntExtra("vib", 0);
				strRingTone = getIntent().getStringExtra("ring");
				int day = getIntent().getIntExtra("day", 0);
				// �ð�����
				hours.setCurrentItem(mAlarmHour);
				mins.setCurrentItem(mAlarmMinute);
				
				//����	
				if (mVibrate == 0) {
		        	((CheckBox)findViewById(R.id.alarm_set_vibrate)).setChecked(false);
		        } else {
		        	((CheckBox)findViewById(R.id.alarm_set_vibrate)).setChecked(true);        	
		        }
				// ring tone
				if (strRingTone != null) {
					showRingTone(Uri.parse(strRingTone));
				}
				// ����
				if ((day & 0x0001) == 0x0001) ((CheckBox)findViewById(R.id.sun)).setChecked(true);
				if ((day & 0x0002) == 0x0002) ((CheckBox)findViewById(R.id.mon)).setChecked(true);
				if ((day & 0x0004) == 0x0004) ((CheckBox)findViewById(R.id.tue)).setChecked(true);
				if ((day & 0x0008) == 0x0008) ((CheckBox)findViewById(R.id.wed)).setChecked(true);
				if ((day & 0x0010) == 0x0010) ((CheckBox)findViewById(R.id.thur)).setChecked(true);
				if ((day & 0x0020) == 0x0020) ((CheckBox)findViewById(R.id.fri)).setChecked(true);
				if ((day & 0x0040) == 0x0040) ((CheckBox)findViewById(R.id.sat)).setChecked(true);
				
				//if (day != 0) { button.setEnabled(true); }
			}
			//
////////////////////////////////////////////////////////
//����
///////////////////////////////////////////////////////
adView = (MobileAdView)findViewById(R.id.adview1);
adView.setListener(this);
adView.start();
/////////////////////////////////////////////////////////
		}
		

		protected void onDestroy() {
	    	super.onDestroy();
		    ////////////////////////////////////////////////////////
		    // ����
		    ///////////////////////////////////////////////////////
	    	if (adView != null) {
	    		adView.destroy();
	    		adView = null;
	    	}
	    }
		

		//�˶� ���� ����
		private void setAlarm() {
			// TODO Auto-generated method stub

			//���� ����
			if (((CheckBox)findViewById(R.id.alarm_set_vibrate)).isChecked()) {
	    		mVibrate = 1;
	    	} else{
	    		mVibrate = 0;
	    	}
			//////////////////����////////////////
			int apday = 0;

			if (((CheckBox)findViewById(R.id.sun)).isChecked())  { apday |= 0x0001;}
			if (((CheckBox)findViewById(R.id.mon)).isChecked())  { apday |= 0x0002;}
			if (((CheckBox)findViewById(R.id.tue)).isChecked())  { apday |= 0x0004;}
			if (((CheckBox)findViewById(R.id.wed)).isChecked())  { apday |= 0x0008;}
			if (((CheckBox)findViewById(R.id.thur)).isChecked()) { apday |= 0x0010;}
			if (((CheckBox)findViewById(R.id.fri)).isChecked())  { apday |= 0x0020;}
			if (((CheckBox)findViewById(R.id.sat)).isChecked())  { apday |= 0x0040;}

			if (apday == 0) 
			{	   
				Toast.makeText(getBaseContext(), "������ �����ϼ���", 7000).show();
				return;
			}
			//
	        mAlarmHour = mTime.getCurrentHour();
	        mAlarmMinute = mTime.getCurrentMinute();

			///// db�� ���� /////
	        DBAdapter db = new DBAdapter(alarmSet.this);
	        db.open();
	        // �˶� ����
	        //db id�� -1�̸� ���� ���� �ƴϸ� ������ �ִ����� ����
	        if (db_id == -1) {
	        	db.addAlarm(1, apday, mAlarmHour, mAlarmMinute, mVibrate, strRingTone);
	        } else {
	        	db.modifyAlarm(db_id, 1, apday, mAlarmHour, mAlarmMinute, mVibrate, strRingTone);
	        }
	        
	        db.close();
			////////////////////////////////////////
	        Utility.startFirstAlarm(this);
	        finish();

		}
	
		 // Ringtone Manager ���� ���� �޼��� ����
	    private OnClickListener ringSelectBtnListener = new OnClickListener() {
	    	
			public void onClick(View v) {
	            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
	            startActivityForResult(intent, 123);
	        }
	    };
		//ringtone manager ���Ҹ� ���� �޴���
		protected void onActivityResult(int requestCode, int resultCode, Intent data){
		  	if (requestCode == 123 && resultCode == RESULT_OK) {
	    		Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	    		if (uri != null) {
	    			strRingTone = uri.toString();
	    	        // ringtone �̸��� ǥ��
	    			showRingTone(uri);    	        
	    		}
	    	}
	    }	
		
		private void showRingTone(Uri uri) {
			Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
			if (ringtone != null) {
				String value = "���Ҹ�" + "\n" +  ringtone.getTitle(this);
				SpannableStringBuilder ssb = new SpannableStringBuilder();
				ssb.append(value);
				ssb.setSpan(new ForegroundColorSpan(0xFFf4A460), 3, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				((TextView)findViewById(R.id.ringtone)).setText(ssb);
			} else {
			}
			
		}
		
		/**
		 * Adds changing listener for wheel that updates the wheel label
		 * @param wheel the wheel
		 * @param label the wheel label
		 */
		private void addChangingListener(final WheelView wheel, final String label) {
			wheel.addChangingListener(new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					wheel.setLabel(newValue != 1 ? label + "s" : label);
				}
			});
		}
/*
		 // DB �� �ִ� ù��° �˶��� ����
	     public  void startFirstAlarm(Context context) {
	     	int apday;
	     	int onoff;
	     	int day;
	     	int hour;
	     	int min;
	     	int vib;
	     	String ring;
	     	
	        Calendar calendar = Calendar.getInstance();
	        int c_day = calendar.get(Calendar.DAY_OF_WEEK);	    
	        int c_hour = calendar.get(Calendar.HOUR_OF_DAY);
	        int c_min = calendar.get(Calendar.MINUTE);
	        //minimum  ���� ������ id�� ã�� ����
	        int m_day = 100;
	        int m_hour = 100;
	        int m_min = 100;
	        int m_vib = 0;
	        String m_ring = null;
	        //
	        long m_id;
	     	// ������ ��-��-��
	     	// �ð��� ����� �˶��� DB���� ����
	        DBAdapter db = new DBAdapter(context);
	        if (db == null) return;
	         
	        db.open();
	        Cursor c = db.fetchAllAlarm();
	         
	        if (c.moveToFirst()) {	// ù��°�� �̵�
	        	do {
	        		onoff =  c.getInt(c.getColumnIndex(DBAdapter.ALARM_ON));
	        		if (onoff == 1) {
	        			day =  c.getInt(c.getColumnIndex(DBAdapter.ALARM_APDAY));
	        			hour = c.getInt(c.getColumnIndex(DBAdapter.ALARM_HOUR));
	        			min = c.getInt(c.getColumnIndex(DBAdapter.ALARM_MINUTE));
	        			vib = c.getInt(c.getColumnIndex(DBAdapter.ALARM_VIBRATE));
	        			ring = c.getString(c.getColumnIndex(DBAdapter.ALARM_RINGTONE));
	        			//
	        			for (int i = 0; i < 7; i++) {
	        				if ((day & 0x01) == 0x01) {	        					
	        					apday = i+1;
	        					if ((apday < c_day) 
	        							|| ((apday == c_day) && (hour < c_hour))
	        							|| ((apday == c_day) && (hour == c_hour) && (min < c_min))){ 
	        						apday += 7; 
	        					}
	        					
	        					if (m_day > apday){
	        						m_day = apday;
	        						m_hour = hour;
	        						m_min = min;
	        						m_id = c.getLong(c.getColumnIndex("_id"));
	        						m_vib = vib;
	        						m_ring = ring;
	        					} else if ((m_day == apday ) && (m_hour > hour)){
	        						m_hour = hour;
	        						m_min = min;
	        						m_id = c.getLong(c.getColumnIndex("_id"));
	        						m_vib = vib;
	        						m_ring = ring;
	        					} else if ((m_day == apday) && (m_hour == hour) && (m_min > min)){
	        						m_min = min;
	        						m_id = c.getLong(c.getColumnIndex("_id"));
	        						m_vib = vib;
	        						m_ring = ring;
	        					}
	        				}
	        				day = day >> 1;
	        			}
	        		}
	        	} while (c.moveToNext());
	     	}
	        if( m_day != 100){
	        	Intent intent = new Intent(this, alarmReceiver2.class);
	        	intent.putExtra("ringtone", m_ring);
	        	intent.putExtra("vibrate", m_vib);				        
			//PendingIntent sender = PendingIntent.getBroadcast(afternoonAlarm.this, 0, intent, 0);
	        	calendar.add(Calendar.DAY_OF_MONTH, m_day - c_day);
				calendar.set(Calendar.HOUR_OF_DAY, m_hour);
		        calendar.set(Calendar.MINUTE, m_min);
		        calendar.set(Calendar.SECOND, 0);
		
	        	PendingIntent sender = PendingIntent.getActivity(alarmSet.this, alarmSetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			
	        	mManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	        	mManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, sender);
	        	
	        	Toast.makeText(getBaseContext(), "�˶� ���� �ð�" + calendar.get(Calendar.YEAR)+ "�� "
	        										+ (calendar.get(Calendar.MONTH)+1) + "�� "
	        										+ calendar.get(Calendar.DAY_OF_MONTH) + "�� "
	        										//+ calendar.get(Calendar.DAY_OF_WEEK)  + "���� "
	        										+ calendar.get(Calendar.HOUR_OF_DAY) + "�� "
	        										+ calendar.get(Calendar.MINUTE)+ "�� ",
	        										7000).show();
	        }
	        db.close();
	     } 
*/

		public void onReceive(int err) {
			// TODO Auto-generated method stub
			
		}	
}



