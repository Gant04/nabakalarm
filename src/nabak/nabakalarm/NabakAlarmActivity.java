package nabak.nabakalarm;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;

import com.nbpcorp.mobilead.sdk.MobileAdListener;
import com.nbpcorp.mobilead.sdk.MobileAdView;

public class NabakAlarmActivity extends TabActivity implements MobileAdListener {
    /** Called when the activity is first created. */
	private MobileAdView adView = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    
        TabHost tabHost = getTabHost();    //tab�� �����´�.
              
        LayoutInflater.from(this).inflate(R.layout.main, tabHost.getTabContentView(), true);
        
        
        /** TabHost �� ���Ե� Tab�� ������ ��� �ٲ۴�, �������� */
        
        tabHost.addTab(tabHost.newTabSpec("����")
                .setIndicator("����",
                		getResources().getDrawable(R.drawable.afteral))
                .setContent(new Intent(this, afternoonAlarm.class)));
        tabHost.addTab(tabHost.newTabSpec("�˶�")
                .setIndicator("�˶�", 
                		getResources().getDrawable(R.drawable.alar))
        		.setContent(new Intent(this, alarm.class)));
        /*
        tabHost.addTab(tabHost.newTabSpec("��ž��ġ")
                .setIndicator("��ž��ġ", 
                		getResources().getDrawable(R.drawable.stopwatch))
                .setContent(R.id.StopWatch));
                */
        
        ////////////////////////////////////////////////////////
	    // ����
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
	    // ����
	    ///////////////////////////////////////////////////////
    	if (adView != null) {
    		adView.destroy();
    		adView = null;
    	}
    }

	@Override
	public void onReceive(int err) {
		// TODO Auto-generated method stub
		// event for receive ad 
	}
    
    
    
}

