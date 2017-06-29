package com.cnblogs.hoojo.core.cache.temporary;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.cache.AbstractCacheManager;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.google.common.collect.Sets;

/**
 * <b>function:</b> 抓取图文临时缓存服务
 * 
 * @author hoojo
 * @createDate 2017-5-17 下午6:03:15
 * @file TemporaryCacheService.java
 * @package com.cnblogs.hoojo.core.cache.temporary
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TemporaryCacheService extends AbstractCacheManager implements TemporaryCacheManager {

	private String spiderSite;
	private Set<String> worksLinks;
	
	private ExecutorService temporaryCachePool;

	public TemporaryCacheService(String site, String spiderName) {
		this.spiderSite = site;

		if (!SpiderExecutor.SPIDER_WORKS_LINKS.containsKey(site)) {
			worksLinks = Sets.newConcurrentHashSet();
			SpiderExecutor.SPIDER_WORKS_LINKS.put(site, worksLinks);
		} else {
			worksLinks = SpiderExecutor.SPIDER_WORKS_LINKS.get(site);
		}

		this.temporaryCachePool = Executors.newFixedThreadPool(GlobalConst.TemporaryCacheServiceConst.FIXED_THREAD_POOL_NUM);
	}

	public void loaderCaches() {
		log.info("------------------------开始读取【合并后】的缓存文件-------------------------");
		try {
			File dirFile = optimizeRootDir();

			// 读取已压缩缓存
			for (File optimizeFile : dirFile.listFiles()) {
				if (optimizeFile.length() == 0) {
					optimizeFile.delete();
					System.out.println("空文件，删除文件：" + optimizeFile.getAbsolutePath());
					continue;
				}
				
				String _site = getSite(optimizeFile);
				if (!StringUtils.equalsIgnoreCase(_site, spiderSite)) {
					continue;
				}

				this.temporaryCachePool.submit(new TemporaryCacheReader(optimizeFile, worksLinks));
			}

			if (!temporaryCachePool.isShutdown()) {
				temporaryCachePool.shutdown();
			}
		} catch (Exception e) {
			log.error("读取缓存文件目录数据异常", e);
		}
		log.info("------------------------结束读取【合并后】缓存文件-------------------------");
	}

	public boolean isFinish() {

		while (true) {
			if (temporaryCachePool.isTerminated()) {
				return true;
			}

			try {
				Thread.sleep(GlobalConst.TemporaryCacheServiceConst.SHUTDOWN_POOL_IDLE_MINUTES);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}
}
