package com.cnblogs.hoojo.core.spider;

import java.util.Map;
import java.util.Set;

import com.cnblogs.hoojo.core.analyzer.Analyzer;
import com.cnblogs.hoojo.core.cache.FileSystemCacheManager;
import com.cnblogs.hoojo.core.holder.RuntimeDataHolder;
import com.cnblogs.hoojo.core.listening.ListeningDownloadManager;
import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.enums.TaskState;
import com.google.common.collect.Maps;

/**
 * <b>function:</b> 爬取执行器
 * 
 * @author hoojo
 * @createDate 2017-3-4 上午10:55:37
 * @file SpiderExecutor.java
 * @package com.cnblogs.hoojo.core.spider
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface SpiderExecutor extends RuntimeDataHolder {

	public static Map<String, SpiderExecutor> spiderNames = Maps.newLinkedHashMap();
	public static Map<String, Set<String>> SPIDER_WORKS_LINKS = Maps.newConcurrentMap();
	        
	public static final int MIN_PAGE = 0;
	public static final int PAGE_NUM = 100;
	public static final Method METHOD = Method.GET;
	public static final long TIMEOUT = 2 * 60 * 1000L;

	public static final String DEFAULT_SAVE_LOCATION = "E:\\API\\SpiderPhoto";
	public static final String SPIDER_CACHE_SAVE_LOCATION = "E:\\API\\SpiderPhoto\\.cache";
	public static final int MAX_DOWNLOAD_TASK_NUM = 6;
	public static final boolean OVERRIDE = false;
	public static final PathMode PATH_MODE = PathMode.SITE_TYPE;
	public static final NamedMode NAMED_MODE = NamedMode.DATE_TITLE_AUTHOR;
	public static final NamedMode FILE_NAME_MODE = NamedMode.DEFAULT;
	
	public void execute();
	
	public String getSpiderName();
	public TaskState getSpiderState();
	
	public Analyzer getAnalyzer();
	
	public ListeningDownloadManager getListeningDownloadManager();
	public FileSystemCacheManager getCacheManager();
}
