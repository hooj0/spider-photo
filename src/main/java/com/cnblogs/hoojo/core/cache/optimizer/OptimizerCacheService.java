package com.cnblogs.hoojo.core.cache.optimizer;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.cache.AbstractCacheManager;
import com.cnblogs.hoojo.enums.TaskState;
import com.cnblogs.hoojo.util.MathUtils;
import com.google.common.collect.Lists;

/**
 * <b>function:</b> 合并抓取文件缓存服务
 * @author hoojo
 * @createDate 2017-5-17 下午5:54:19
 * @file OptimizerCacheService.java
 * @package com.cnblogs.hoojo.core.cache.optimizer
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class OptimizerCacheService extends AbstractCacheManager implements OptimizerCacheManager {

	private String spiderSite;
	private TaskState state = TaskState.WAIT;
	private ExecutorService optimizerCachePool;
	
	public OptimizerCacheService(String spiderSite) {
		this.spiderSite = spiderSite;
		
		this.optimizerCachePool = Executors.newFixedThreadPool(GlobalConst.OptimizerCacheServiceConst.FIXED_THREAD_POOL_NUM);
	}

	@Override
	public void compositeCaches() {
		state = TaskState.RUN;
		
		List<File> optimizeFiles = Lists.newArrayList();

		log.info("------------------------开始压缩缓存文件-------------------------");
		try {
			File optimizeRoot = optimizeRootDir();
			
			for (File file : optimizeRoot.listFiles()) {
				if (file.length() == 0) {
					file.delete();
					log.debug("删除空合并文件：" + file.getAbsolutePath());
					continue;
				}
				
				String site = getSite(file);
				if (!StringUtils.equalsIgnoreCase(site, spiderSite)) {
					continue;
				}
				if (file.length() >= CACHE_FLIE_MAX_SIZE) {
					log.debug("文件：{} 超过限定空间 {} 不进行合并/文件大小：{}", file.getName(), MathUtils.humanFileSize(CACHE_FLIE_MAX_SIZE), MathUtils.humanFileSize(file.length()));
					continue;
				}
				
				log.debug("加入到待合并列表，文件名：{}/文件大小：{}", file.getName(), MathUtils.humanFileSize(file.length()));
				optimizeFiles.add(file);
			}

			File tempRoot = tempRootDir();
			for (File tmpFile : tempRoot.listFiles()) {
				if (tmpFile.length() == 0) {
					tmpFile.delete();
					log.debug("删除空临时文件：" + tmpFile.getAbsolutePath());
					continue;
				}
				
				String site = getSite(tmpFile);
				if (!StringUtils.equalsIgnoreCase(site, spiderSite)) {
					continue;
				}
				
				// 读取数据到内存
				optimizerCachePool.submit(new OptimizerCacheReader(site, tmpFile));
				// new TemporaryCacheReader(tmpFile, worksLinks).pull();
			}

			if (!optimizerCachePool.isShutdown()) {
				optimizerCachePool.shutdown();
			}
			
			// 压缩内存数据到文件
			new OptimizerCacheWriter(spiderSite, optimizeFiles, this).run();
			
			state = TaskState.FINISH;
			log.info("------------------------结束压缩缓存文件-------------------------");
		} catch (Exception e) {
			log.error("读取缓存文件目录数据异常", e);
			state = TaskState.EXCEPTION;
		}
	}
	
	public boolean isFinish() {

		while (true) {
			if (optimizerCachePool.isTerminated()) {
				return true;
			}

			try {
				Thread.sleep(GlobalConst.OptimizerCacheServiceConst.SHUTDOWN_POOL_IDLE_MINUTES);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}
	
	public boolean isDone() {
		return optimizerCachePool.isTerminated();
	}

	@Override
    public TaskState getState() {
	    return state;
    }
}
