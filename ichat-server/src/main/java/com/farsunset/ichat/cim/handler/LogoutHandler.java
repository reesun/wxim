 
package com.farsunset.ichat.cim.handler;

import org.apache.mina.core.session.IoSession;

import com.farsunset.cim.nio.handle.CIMRequestHandler;
import com.farsunset.cim.nio.mutual.ReplyBody;
import com.farsunset.cim.nio.mutual.SentBody;
import com.farsunset.cim.nio.session.DefaultSessionManager;
import com.farsunset.framework.container.ContextHolder;
 

/**
 * 退出连接实现
 * 
 *  @author 3979434@qq.com 
 */
public class LogoutHandler implements CIMRequestHandler {

	public ReplyBody process(IoSession ios, SentBody message) {

		
		DefaultSessionManager sessionManager  =  ((DefaultSessionManager) ContextHolder.getBean("defaultSessionManager"));
		String account = message.get("account");
		ios =sessionManager.getSession(account);

		if (ios != null) {
			ios.close(true);
			sessionManager.removeSession(account);
		}
		 
		return null;
	}
	
	
}