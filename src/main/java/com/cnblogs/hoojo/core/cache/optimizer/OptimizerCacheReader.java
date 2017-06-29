package com.cnblogs.hoojo.core.cache.optimizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.cache.AbstractCacheManager;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.enums.TaskState;
import com.google.common.collect.Sets;

/**
 * <b>function:</b> 合并图文抓取缓存优化
 * @author hoojo
 * @createDate 2017-5-9 下午12:10:15
 * @file SpiderCacheOptimizer.java
 * @package com.cnblogs.hoojo.core.cache
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class OptimizerCacheReader extends AbstractCacheManager implements OptimizerCacheManager, Runnable {

	private TaskState state = TaskState.WAIT;
	
	private File tempFile;
	private Set<String> spiderWorksLinks;
	private Queue<String> optimizerWorksLinks;
	
	private Reader reader;
	private BufferedReader bufferedReader;
	
	public OptimizerCacheReader(String site, File tempFile) {
		this.tempFile = tempFile;
		
		try {
			if (OPTIMIZER_WORKS_LINK_CACHE.containsKey(site)) {
				optimizerWorksLinks = OPTIMIZER_WORKS_LINK_CACHE.get(site);
			} else {
				optimizerWorksLinks = new ConcurrentLinkedQueue<String>();
				OPTIMIZER_WORKS_LINK_CACHE.put(site, optimizerWorksLinks);
			}
			
			if (!SpiderExecutor.SPIDER_WORKS_LINKS.containsKey(site)) {
				spiderWorksLinks = Sets.newConcurrentHashSet();
				SpiderExecutor.SPIDER_WORKS_LINKS.put(site, spiderWorksLinks);
			} else {
				spiderWorksLinks = SpiderExecutor.SPIDER_WORKS_LINKS.get(site);
			}
			
	        reader = new FileReader(tempFile);
	        bufferedReader = new BufferedReader(reader);
	        
	        log.info("【合并读取】图文缓存临时文件：{}", tempFile.getAbsolutePath());
		} catch (Exception e) {
	        log.error("读取文件创建读入流异常", e);
        }
	}
	
	@Override
    public void run() {
		
		try {
			String threadName = "合并读取-" + tempFile.getName();
    		String name = Thread.currentThread().getName();
			if (StringUtils.contains(name, "#")) {
				name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "#");;
			}
			Thread.currentThread().setName(threadName + "#" + name);
			
	        reader();
        } catch (Exception e) {
        	log.error("读取缓存文件异常", e);
        	state = TaskState.EXCEPTION;
        }
    }
	
	private void reader() throws Exception {
		state = TaskState.RUN;
		
		try {
			log.debug("【合并开始】读取缓存文件：{}", tempFile.getAbsolutePath());
			
			String worksLink = null;
	        while ((worksLink = bufferedReader.readLine()) != null) {
	        	if (!optimizerWorksLinks.contains(worksLink)) {
	        		optimizerWorksLinks.add(worksLink);
	        	}

	        	spiderWorksLinks.add(worksLink);
	        	log.debug("写入缓存队列数据：{}, optimizerCacheSize: {}, spiderCacheSize: {}", worksLink, optimizerWorksLinks.size(), spiderWorksLinks.size());
	        }

	        log.debug("【合并完成】读取缓存文件：{}", tempFile.getAbsolutePath());
	        
        } catch (Exception e) {
	        log.error("写入缓存文件数据异常", e);
	        throw e;
        } finally {
        	
        	try {
        		if (bufferedReader != null) {
        			bufferedReader.close();
        		}
        		if (reader != null) {
        			reader.close();
        		}
            } catch (Exception e) {
            	log.error("关闭写入缓存文件数据异常", e);
            }
        }
		
		try {
			File moveFile = new File(removeRootDir().getAbsolutePath() + File.separatorChar + tempFile.getName() + "." + System.currentTimeMillis());
			tempFile.renameTo(moveFile);
			
			log.info("移动文件[{}] 到 [{}]", tempFile.getAbsolutePath(), moveFile.getAbsolutePath());
        } catch (Exception e) {
        	log.error("移动缓存文件数据异常", e);
        	throw e;
        }
		
		state = TaskState.FINISH;
	}
	
	public TaskState getState() {
		return state;
	}

	@Override
    public boolean isFinish() {
		return (state == TaskState.FINISH || state == TaskState.EXCEPTION);
    }
}
