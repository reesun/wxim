 
package com.farsunset.cim.client.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 * 与服务端连接服务
 * @author 3979434
 *
 */
public class CIMConnectorService extends Service {

   
    CIMConnectorManager manager;

    private IBinder binder=new CIMConnectorService.LocalBinder();
    @Override
    public void onCreate() {
       
    	manager = CIMConnectorManager.getManager(this.getApplicationContext());
    	manager.createExceptionCaughtHandler();
    	manager.createMessageReceivedHandler();
    	manager.createMessageSentFailedHandler();
    	manager.createMessageSentSuccessfulHandler();
    	manager.createReplyReceivedHandler();
    	manager.createSessionClosedHandler();
    	manager.createSessionCreatedHandler();
    	manager.createConnectionFailedHandler();
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	
    	
    	if(intent!=null)
    	{
	    	String host = intent.getStringExtra(CIMConnectorManager.CIM_SERVIER_HOST);
	    	int port = intent.getIntExtra(CIMConnectorManager.CIM_SERVIER_PORT, 28888);
	    	if(host!=null)
	    	{
	    	   manager.connect(host,port);
	    	}
    	}
    }
   
    public void connect(String host,int port)
    {
    	manager.connect(host,port);
    }

    @Override
    public void onDestroy() {
    	manager.destroy();
    }

    public void stop() {
    	this.stopSelf();
    }
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

    public class LocalBinder extends Binder{
    	
    	public CIMConnectorService getService()
    	{
            return CIMConnectorService.this;
        }
    }
	
 
}
