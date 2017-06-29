package com.cnblogs.hoojo.core.cache.temporary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.cache.AbstractCacheManager;
import com.cnblogs.hoojo.enums.TaskState;

/**
 * <b>function:</b> 读取爬取文件缓存数据
 * @author hoojo
 * @createDate 2017-5-8 下午12:02:41
 * @file TemporaryCacheReader.java
 * @package com.cnblogs.hoojo.core.cache.temporary
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TemporaryCacheReader extends AbstractCacheManager implements TemporaryCacheManager, Runnable {

	public volatile static int NUM = 0;
	private TaskState state = TaskState.WAIT;
	
	private File cacheFile;
	private Set<String> worksLinks;
	
	public TemporaryCacheReader(File cacheFile, Set<String> worksLinks) {
		optimizeRootDir();
		
		NUM++;
		this.cacheFile = cacheFile;
		this.worksLinks = worksLinks;
		
		log.debug("【临时读取】读取临时缓存文件[{}]任务：{}", NUM, cacheFile.getAbsolutePath());
	}
	
	@Override
    public void run() {
		state = TaskState.RUN;
		
	    try {
	    	
	    	String threadName = "临时读取->" + NUM + "-" + cacheFile.getName();
    		String name = Thread.currentThread().getName();
			if (StringUtils.contains(name, "#")) {
				name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "#");;
			}
			Thread.currentThread().setName(threadName);
			
	    	this.pull();
	        state = TaskState.FINISH;
	        
        } catch (Exception e) {
        	log.error("写入缓存文件数据异常", e);
        	state = TaskState.EXCEPTION;
        }
    }
	
	@Override
	public void pull() throws Exception {
		Reader reader = null;
		BufferedReader bufferedReader = null;
		
		try {
			
			if (!cacheFile.exists()) {
				log.error("缓存文件不存在：{}", cacheFile.getAbsolutePath());
				return;
			}
			
			log.debug("【开始】读取临时缓存文件：{}", cacheFile.getAbsolutePath());
			reader = new FileReader(cacheFile);
			bufferedReader = new BufferedReader(reader);
			
			String worksLink = null;
	        while ((worksLink = bufferedReader.readLine()) != null) {
	        	
	        	worksLinks.add(worksLink);
	        	log.trace("读取的临时缓存文件数据：{}", cacheFile.getName(), worksLink, worksLinks.size());
	        }
	        log.debug("【完成】读取临时缓存文件：{}", cacheFile.getAbsolutePath(), worksLinks.size());
        } catch (Exception e) {
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
	}

	@Override
    public boolean isFinish() {
		return (state == TaskState.FINISH || state == TaskState.EXCEPTION);
    }
}
