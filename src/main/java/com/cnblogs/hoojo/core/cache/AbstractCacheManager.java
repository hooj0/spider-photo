package com.cnblogs.hoojo.core.cache;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.log.ApplicationLogging;

/**
 * <b>function:</b> 抓取图文缓存抽象基类
 * @author hoojo
 * @createDate 2017-5-9 下午3:39:58
 * @file AbstractSpiderCache.java
 * @package com.cnblogs.hoojo.core.cache
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class AbstractCacheManager extends ApplicationLogging {

	protected static final int CACHE_FLIE_MAX_SIZE = 1024 * 1024 * 2; // 2M
	protected static final String CACHE_FILE_SUFFIX = ".sc";
	
	private int serialNumber = 0;
	
	protected File cacheRootDir() {
		
		File dirFile = new File(SpiderExecutor.SPIDER_CACHE_SAVE_LOCATION + File.separatorChar);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		
		return dirFile;
	}

	protected File optimizeRootDir() {
		
		File dirFile = new File(SpiderExecutor.SPIDER_CACHE_SAVE_LOCATION + File.separatorChar + ".optimize" + File.separatorChar);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		
		return dirFile;
	}
	
	protected File tempRootDir() {
		
		File dirFile = new File(SpiderExecutor.SPIDER_CACHE_SAVE_LOCATION + File.separatorChar + ".temporary" + File.separatorChar);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		
		return dirFile;
	}
	
	protected File removeRootDir() {
		
		File dirFile = new File(SpiderExecutor.SPIDER_CACHE_SAVE_LOCATION + File.separatorChar + ".removed" + File.separatorChar);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		
		return dirFile;
	}
	
	protected File createCacheFile(String location, String fileName) {

		// www.abc.com_20141122-01.sc
		Date now = new Date();
		String timed = DateFormatUtils.format(now, "yyyyMMdd");
		String path = location + File.separatorChar + fileName + "_" + timed;

		File cacheFile = new File(path + CACHE_FILE_SUFFIX);
		while (cacheFile.exists()) {
			serialNumber++;
			cacheFile = new File(path + "-" + serialNumber + CACHE_FILE_SUFFIX);
		}
		log.info("创建抓取图文缓存文件：{}", cacheFile.getAbsoluteFile());
		
		return cacheFile;
	}
	
	protected String getSite(File file) {
		
		String path = file.getName();
		return StringUtils.substringBefore(path, "_");
	}
	
	public void pull() throws Exception {
		throw new UnsupportedOperationException("This is method not support，please call TemporaryCacheReader->pull()");
    }
	
    public void push(String worksLink) throws Exception {
		throw new UnsupportedOperationException("This is method not support，please call TemporaryCacheWriter->push()");
    }

    public void close() throws Exception {
		throw new UnsupportedOperationException("This is method not support，please call TemporaryCacheWriter->close()");
    }
    
    public void compositeCaches() {
    	throw new UnsupportedOperationException("This is method not support，please call OptimizerCacheService->compositeCaches()");
    }

    public void loaderCaches() {
		throw new UnsupportedOperationException("This is method not support，please call TemporaryCacheService->loaderCaches()");
    }
}
