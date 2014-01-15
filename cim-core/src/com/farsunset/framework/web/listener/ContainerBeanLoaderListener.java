package com.farsunset.framework.web.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoader;

import com.farsunset.framework.container.ContextHolder;
/**
 * spring加载器,要使用 ContextHolder 必须在web.xml使用它替代spring默认加载
 * @author xiajun
 *
 */
public class ContainerBeanLoaderListener  implements ServletContextListener{

	ContextLoader contextLoader;
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		 if(contextLoader != null)
	            contextLoader.closeWebApplicationContext(event.getServletContext());
		 ContextHolder.setContext(null);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		 
		if(contextLoader == null){
			contextLoader = new ContextLoader();
		}
        
		ContextHolder.setContext(contextLoader.initWebApplicationContext(event.getServletContext()));
	}
	   
		
}
