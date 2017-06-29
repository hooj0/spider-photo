package com.cnblogs.hoojo.core.cache.optimizer;

import java.util.Map;
import java.util.Queue;

import com.cnblogs.hoojo.enums.TaskState;
import com.google.common.collect.Maps;

/**
 * <b>function:</b> 合并优化缓存接口
 * @author hoojo
 * @createDate 2017-5-10 下午5:08:19
 * @file OptimizerCacheManager.java
 * @package com.cnblogs.hoojo.core.cache.optimizer
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface OptimizerCacheManager {

	public static Map<String, Queue<String>> OPTIMIZER_WORKS_LINK_CACHE = Maps.newConcurrentMap();
	
	public void compositeCaches();

	public TaskState getState();

	public boolean isFinish();
}
