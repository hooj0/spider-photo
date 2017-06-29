package com.cnblogs.hoojo.core.listening;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cnblogs.hoojo.core.holder.DownloadDataHolder;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.model.DownloadInfo;
import com.google.common.collect.Sets;

/**
 * <b>function:</b> 包装下载数据
 * 
 * @author hoojo
 * @createDate 2017-4-18 下午9:37:03
 * @file ListeningDownloadWrapper.java
 * @package com.cnblogs.hoojo.core.listening
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class ListeningDownloadWrapper extends ApplicationLogging implements ListeningDownloadManager, DownloadDataHolder {

	protected Queue<DownloadInfo> downloading = new ConcurrentLinkedQueue<DownloadInfo>();
	protected Set<DownloadInfo> downloadCompleted = Sets.newLinkedHashSet();
	protected Queue<DownloadInfo> downloadErrored = new ConcurrentLinkedQueue<DownloadInfo>();

	public final Set<DownloadInfo> getDownloadCompleted() {
		return downloadCompleted;
	}

	public final Queue<DownloadInfo> getDownloading() {
		return downloading;
	}

	@Override
	public Queue<DownloadInfo> getDownloadErrored() {
		return downloadErrored;
	}

	@Override
    public DownloadDataHolder getDataHolder() {
	    return this;
    }
}
