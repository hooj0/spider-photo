package com.cnblogs.hoojo.core.cache;

import com.cnblogs.hoojo.core.cache.temporary.TemporaryCacheManager;

/**
 * <b>function:</b> 文件系统缓存
 * @author hoojo
 * @createDate 2017-5-10 下午5:46:08
 * @file CacheManager.java
 * @package com.cnblogs.hoojo.core.cache
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface FileSystemCacheManager {

	boolean execute();
	
	TemporaryCacheManager getCacheWriter();
}
