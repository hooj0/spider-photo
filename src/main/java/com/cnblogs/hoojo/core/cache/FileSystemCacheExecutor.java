package com.cnblogs.hoojo.core.cache;

import com.cnblogs.hoojo.core.cache.optimizer.OptimizerCacheManager;
import com.cnblogs.hoojo.core.cache.optimizer.OptimizerCacheService;
import com.cnblogs.hoojo.core.cache.temporary.TemporaryCacheManager;
import com.cnblogs.hoojo.core.cache.temporary.TemporaryCacheService;
import com.cnblogs.hoojo.core.cache.temporary.TemporaryCacheWriter;

/**
 * <b>function:</b> 爬取图文文件缓存执行器
 * 
 * @author hoojo
 * @createDate 2017-5-7 下午4:53:01
 * @file FileSystemCacheService.java
 * @package com.cnblogs.hoojo.core.cache
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class FileSystemCacheExecutor extends AbstractCacheManager implements FileSystemCacheManager {

	private OptimizerCacheManager optimizerCacheManager;
	private TemporaryCacheManager temporaryCacheManager;
	private TemporaryCacheManager temporaryCacheWriter;
	
	public FileSystemCacheExecutor(String site, String spiderName) {
		temporaryCacheManager = new TemporaryCacheService(site, spiderName);
		optimizerCacheManager = new OptimizerCacheService(site);

		temporaryCacheWriter = new TemporaryCacheWriter(site, spiderName);
	}

	public boolean execute() {
		
		// 读取合并后缓存
		temporaryCacheManager.loaderCaches();
		
		if (temporaryCacheManager.isFinish()) {
			// 读取临时缓存，并压缩到合并缓存
			optimizerCacheManager.compositeCaches();
		}
		
		return optimizerCacheManager.isFinish();
	}

	@Override
    public TemporaryCacheManager getCacheWriter() {
	    return temporaryCacheWriter;
    }
}
