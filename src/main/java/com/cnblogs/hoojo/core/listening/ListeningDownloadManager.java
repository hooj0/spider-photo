package com.cnblogs.hoojo.core.listening;

import com.cnblogs.hoojo.core.holder.DownloadDataHolder;
import com.cnblogs.hoojo.enums.TaskState;

/**
 * <b>function:</b> 监听下载管理器
 * @author hoojo
 * @createDate 2017-4-19 下午3:48:31
 * @file ListeningDownloadManager.java
 * @package com.cnblogs.hoojo.core.listening
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface ListeningDownloadManager {

	public void start() throws Exception;
	
	public DownloadDataHolder getDataHolder();
	
	public boolean isFinish();
	
	public TaskState getDownloadState();
}
