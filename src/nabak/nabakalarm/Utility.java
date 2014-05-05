package nabak.nabakalarm;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Toast;

public class Utility {
	static final int alarmSetId = 123;

    // 
    // ���� ��� ������ �ѱ������� �Ǵ�
    //
    public static boolean useKoreanLanguage(Context context) {
   	 Locale lc = context.getResources().getConfiguration().locale;
        String language = lc.getLanguage();
        
        if (language.equals("ko")) { // �Ϻ��� : ja,  ���� : en
       	 return true;
        } else {
       	 return false;
        }
    }

	 // DB �� �ִ� ù��° �˶��� ����
    public  static void startFirstAlarm(Context context) {
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
       							|| ((apday == c_day) && (hour == c_hour) && (min <= c_min))){ 
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
       	Intent intent = new Intent(context, alarmReceiver2.class);
       	intent.putExtra("ringtone", m_ring);
       	intent.putExtra("vibrate", m_vib);				        
		//PendingIntent sender = PendingIntent.getBroadcast(afternoonAlarm.this, 0, intent, 0);
       	calendar.add(Calendar.DAY_OF_MONTH, m_day - c_day);
			calendar.set(Calendar.HOUR_OF_DAY, m_hour);
	        calendar.set(Calendar.MINUTE, m_min);
	        calendar.set(Calendar.SECOND, 0);
	
       	PendingIntent sender = PendingIntent.getActivity(context, alarmSetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		AlarmManager mManager = null;
       	mManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
       	mManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, sender);
   
       	/* Toast.makeText(context, "�˶� ���� �ð�" + calendar.get(Calendar.YEAR)+ "�� "
			+ (calendar.get(Calendar.MONTH)+1) + "�� "
			+ calendar.get(Calendar.DAY_OF_MONTH) + "�� "
			//+ calendar.get(Calendar.DAY_OF_WEEK)  + "���� "
			+ calendar.get(Calendar.HOUR_OF_DAY) + "�� "
			+ calendar.get(Calendar.MINUTE)+ "�� ",
			7000).show();
			*/
       }

       db.close();
    } 

  //�˶� ����
  	public static void cancelAlarm(Context context) {
  		Intent intent = new Intent(context, alarmReceiver2.class);
        PendingIntent sender = PendingIntent.getActivity(context, alarmSetId, intent, PendingIntent.FLAG_CANCEL_CURRENT );
          
          // And cancel the alarm.
         AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
         am.cancel(sender);
  		 //Toast.makeText(context, "�˶��� �����ƽ��ϴ�.", Toast.LENGTH_SHORT).show();
  	}

}
