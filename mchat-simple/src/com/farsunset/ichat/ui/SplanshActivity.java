package com.farsunset.ichat.ui;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.farsunset.cim.client.android.CIMConnectorManager;
import com.farsunset.cim.client.android.CIMConnectorService;
import com.farsunset.ichat.R;
import com.farsunset.ichat.app.CIMMonitorActivity;
import com.farsunset.ichat.app.Constant;

public class SplanshActivity extends CIMMonitorActivity{
	
	boolean initComplete = false;
	public void onCreate(Bundle savedInstanceState)
	{
		
		
		super.onCreate(savedInstanceState);
		
		//连接服务端
		Intent serviceIntent  = new Intent(SplanshActivity.this, CIMConnectorService.class);
		serviceIntent.putExtra(CIMConnectorManager.CIM_SERVIER_HOST, Constant.CIM_SERVER_HOST);
		serviceIntent.putExtra(CIMConnectorManager.CIM_SERVIER_PORT, Constant.CIM_SERVER_PORT);
		startService(serviceIntent);
		
		
		
		final View view = View.inflate(this, R.layout.activity_splansh, null);
		setContentView(view);
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		
		
	}
	@Override
	public void onConnectionSuccessful() {
		
		Intent intent = new Intent(SplanshActivity.this,LoginActivity.class);  
		startActivity(intent);
		finish();
	}

	
	/**
	 * 当与服务端连接失败的时候，再次重新连接
	 */
	@Override
	public void onConnectionFailed(Exception  e) {
		
	 
		showToask("与服务端连接失败，请检查IP和端口是否设置正确");
		
		super.onDestroy();
		stopService(new Intent(this, CIMConnectorService.class));
		this.finish();
		
	}
	
	
	 @Override
		public void onBackPressed() {
			 
				stopService(new Intent(this, CIMConnectorService.class));
				this.finish();
		}
}
