package com.cnblogs.hoojo.core.cache.temporary;

/**
 * <b>function:</b> 爬取图文临时缓存管理器
 * @author hoojo
 * @createDate 2017-5-8 上午11:57:56
 * @file TemporaryCacheManager.java
 * @package com.cnblogs.hoojo.core.cache.temporary
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface TemporaryCacheManager {

	void push(String worksLink) throws Exception;

	void close() throws Exception;

	void pull() throws Exception;
	
	void loaderCaches();
	
	boolean isFinish();
}
