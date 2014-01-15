package com.farsunset.ichat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 文件下载器 每秒2次跟新进度
 *
 * @author Administrator
 *
 */
public class FileDownloader {

    private DownloadHandler handler;
	private static FileDownloader fileDownloader;
	private FileDownloadCallBack downloadCallBack;
	private ThreadPoolExecutor downloadExecutor;// 下载线程执行器
	public static final int STATUS_STOP = 1;
	
	public static final long DOWNLOAD_DONE = -1;
	public static final int STATUS_AWAIT = 3;
	public static final int STATUS_NETERROR = 4;
	public static final int STATUS_FAILED = 5;
	public static final int STATUS_RUNING = 0;
	// 每个下载线程的状态记录
	private   HashMap<String, Integer> STATUS_MAP = new HashMap<String, Integer>();

	// 每个文件的大小 byte
	private   HashMap<String, Integer> FILE_SIZE_MAP = new HashMap<String, Integer>();


	// 每个下载线程的进度大小(byte)
	private   HashMap<String,Long> PROGRESS_MAP = new HashMap<String,Long>();

	private FileDownloader() {

	    handler = new DownloadHandler();
		//支持3个线程同时下载
		downloadExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
	}

	public synchronized static FileDownloader getInstance() {
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader();
		}
		return fileDownloader;
	}

	public String download(final String fileUrl, final File file) {
		// 创建线程key
		final String threadKey = UUID.randomUUID().toString().replaceAll("-","");
		
		STATUS_MAP.put(threadKey, STATUS_RUNING);
		// 如果运行的下载线程已满，则等待，同时调用状态回调
		if (downloadExecutor.getActiveCount() == downloadExecutor.getMaximumPoolSize()) {
			STATUS_MAP.put(threadKey, STATUS_AWAIT);
			downloadCallBack.statusChange(threadKey, STATUS_AWAIT);
		}
		// 创建下载任务，当运行的任务线程已经满了，则会等待，直到有下载线程完成
		downloadExecutor.execute(new Runnable() {
			@Override
			public void run() {

				HttpURLConnection conn = null;
				InputStream is = null;
				FileOutputStream fos = null;
				
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("threadKey", threadKey);
				msg.setData(bundle);
				
				try {
					URL fielURL = new URL(fileUrl);
					conn = (HttpURLConnection) fielURL.openConnection();
					
					FILE_SIZE_MAP.put(threadKey, conn.getContentLength());

					if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
						msg.what  = STATUS_NETERROR;
						handler.sendMessage(msg);
						return;
					}
					
					is = conn.getInputStream();
					fos = new FileOutputStream(file);
					
					
					 
					
					handler.sendMessageDelayed(msg, 200);
					
					byte buf[] = new byte[1024];
					int numread;
					long downloadSize = 0;
					
					while ((numread = is.read(buf)) != -1) {
						if (STATUS_MAP.get(threadKey) == STATUS_STOP) {
							
						    msg = new Message();
							msg.what  = STATUS_STOP;
							handler.sendMessage(msg);
							break;
						}
						fos.write(buf, 0, numread);
						downloadSize += numread;

						PROGRESS_MAP.put(threadKey, downloadSize);
					}
				} catch (Exception e) {
					
					msg = new Message();
					msg.what  = STATUS_FAILED;
					handler.sendMessage(msg);
					e.printStackTrace();
				} finally {

					try {
						is.close();
						fos.close();
					} catch (IOException e) {}
					
					conn.disconnect();
				}
			}
		});

		
		return threadKey;
	}
 

	/**
	 * 通过任务名称，停止下载 threadKey download()返回的任务名
	 */
	public void setOnDownloadCallBack(FileDownloadCallBack callBack) {
		downloadCallBack = callBack;
	}

	/**
	 * 通过任务名称，停止下载 threadKey download()返回的任务名
	 */
	public void stop(String threadKey) {
		STATUS_MAP.put(threadKey, STATUS_STOP);
	}
 

	/**
	 * 停止所有下载任务
	 */
	public void stopAll() {
		for (String threadKey : STATUS_MAP.keySet()) {
			stop(threadKey);
		}

	}

	/**
	 * 停止所有下载任务
	 */
	public void shutdown() {
		downloadExecutor.shutdown();
	}
	/**
	 * 销毁下载器
	 */
	public void destroy() {
		stopAll();
		downloadExecutor.shutdown();
		downloadExecutor = null;
		fileDownloader = null;
	}

	class DownloadHandler extends Handler {

		DownloadHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message message) {
			String threadKey = message.getData().getString("threadKey");
			switch (message.what) {
			case STATUS_RUNING:
				
				if(PROGRESS_MAP.get(threadKey)!=null) 
				{
				   downloadCallBack.progress(threadKey, FILE_SIZE_MAP.get(threadKey), PROGRESS_MAP.get(threadKey));
				   PROGRESS_MAP.remove(threadKey);
				   message = this.obtainMessage();
				   message.getData().putString("threadKey", threadKey);
				   this.sendMessageDelayed(message, 200);
				}
				break;
			case STATUS_STOP:
				 
				downloadCallBack.statusChange(threadKey, STATUS_STOP);
				break;
			 
			}

		}
	}

	public static interface FileDownloadCallBack {
		public abstract void progress(String threadKey, long fileSize,long downloadSize);
		public abstract void statusChange(String threadKey, int status);
	}
}
