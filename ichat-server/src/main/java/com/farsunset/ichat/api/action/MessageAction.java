 
package com.farsunset.ichat.api.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.aspectj.util.FileUtil;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.farsunset.cim.nio.mutual.Message;
import com.farsunset.framework.web.SuperAction;
import com.farsunset.ichat.cim.push.MessageHandler;
import com.google.gson.Gson;

/** 
 * 消息发送处理，终端可以http 方式 请求 send 接口进行消息发送
 * @author farsunset (3979434@qq.com)
 */
public class MessageAction  extends  SuperAction  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 
	   
    
    
    public String send() throws Exception {
        
		response.setContentType("text/json;charset=UTF-8");
		HashMap<String,Object> datamap = new HashMap<String,Object>();
		datamap.put("code", 200);
		
		try{
	        Message message = mappingParam();
	        
	        fileHandler(message,request);
	        
	        MessageHandler messageManager = new MessageHandler();
	        messageManager.sendMessageToUser(message);
	        datamap.put("mid", message.getMid());
            datamap.put("timestamp", message.getTimestamp());
		}catch(Exception e){
			datamap.put("code", 500);
			e.printStackTrace();
		}
       
        response.getWriter().print(new Gson().toJson(datamap));
        return null;
    }
    
 
    
    
    private Message mappingParam() throws ServletRequestBindingException  
    {
    	  String receiver = ServletRequestUtils.getStringParameter(request,"receiver");
		  String sender = ServletRequestUtils.getStringParameter(request,"sender");
		  String title = ServletRequestUtils.getStringParameter(request, "title");
		  String type = ServletRequestUtils.getStringParameter(request, "type");
		  String content = ServletRequestUtils.getStringParameter(request,  "content");
    	  Message mo = new Message();
          mo.setContent(content);
          mo.setTitle(title);
          mo.setReceiver(receiver);
          mo.setSender(sender);
          mo.setType(type);
          if(StringUtils.isEmpty(receiver))
          {
        	  throw new IllegalArgumentException("receiver 不能为空!");
        	  
          }
          return mo;
    }

    
    /**
     * 文件接收处理，客户端可以发送包含文件的消息。
     * 为了减轻服务器压力 不建议在这里处理文件，建议使用第三方 文件存储服务，存储文件 如 阿里云 OSS  百度云存储 等，
     * @param mo
     * @param request
     * @throws IOException
     */
    private void fileHandler(Message mo, HttpServletRequest request) throws IOException 
    {
    	if(request instanceof MultiPartRequestWrapper){
			MultiPartRequestWrapper pr = (MultiPartRequestWrapper) request;
			if(pr.getFiles("file")!=null)
			{
				File file = pr.getFiles("file")[0];
		         
		        String fileType = request.getParameter("fileType");
		        
		        String path = request.getSession().getServletContext().getRealPath("messageFiles");
		        	String uuid=UUID.randomUUID().toString().replaceAll("-", "");
		 		    File des = new File(path+"/"+uuid);
		 		    FileUtil.copyFile(file, des);
		 		    mo.setFile(path+"/"+uuid);
		 		    mo.setFileType(fileType);
			}
        }
          
    }
 
}
