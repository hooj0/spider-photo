package com.cnblogs.hoojo.core.holder;

import java.util.Queue;
import java.util.Set;

import com.cnblogs.hoojo.model.DownloadInfo;

/**
 * <b>function:</b> 下载数据缓存
 * @author hoojo
 * @createDate 2017-4-18 下午9:37:35
 * @file DownloadDataHolder.java
 * @package com.cnblogs.hoojo.service.listening
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface DownloadDataHolder {

	public Queue<DownloadInfo> getDownloading();
	public Set<DownloadInfo> getDownloadCompleted();
	public Queue<DownloadInfo> getDownloadErrored();
}
