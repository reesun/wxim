 
package com.farsunset.ichat.cim.push;

 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.cim.nio.session.DefaultSessionManager;
import com.farsunset.framework.container.ContextHolder;
 

/**
 * ��Ϣ����ʵ����
 * 
 * @author farsunset (3979434@qq.com)
 */
public class MessageHandler {


	private final Log log = LogFactory.getLog(getClass());

	private DefaultSessionManager sessionManager;
	
	 

	/**
	 * Constructor.
	 */
	public MessageHandler() {
		
		sessionManager =  (DefaultSessionManager) ContextHolder.getBean("defaultSessionManager");
	}
	
	
 
    /**
     * ���û�������Ϣ
     * @param msg
     */
	public void sendMessageToUser(Message msg) {
		log.debug("sendNotifcationToUser()...");
		IoSession session = sessionManager.getSession(msg.getReceiver());
		
		if (session != null && session.isConnected()) {
			//������Ϣ
			session.write(msg);
		} 
	}

 
}
