package com.cnblogs.hoojo.core.cache.temporary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.cnblogs.hoojo.core.cache.AbstractCacheManager;
import com.cnblogs.hoojo.enums.TaskState;

/**
 * <b>function:</b> 写入爬取文件缓存数据
 * @author hoojo
 * @createDate 2017-5-8 上午11:51:13
 * @file TemporaryCacheWriter.java
 * @package com.cnblogs.hoojo.core.cache.temporary
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TemporaryCacheWriter extends AbstractCacheManager implements TemporaryCacheManager {

	private TaskState state = TaskState.WAIT;
	private File cacheFile;
	private String cacheFileName;
	private String cacheFileLocation;

	private FileWriter writer;
    private BufferedWriter bufferedWriter;

	public TemporaryCacheWriter(String site, String spiderName) {
		optimizeRootDir();

		cacheFileLocation = tempRootDir().getAbsolutePath();
		cacheFileName = site + "_" + spiderName;
		
		createCacheWriter();
		log.debug("【临时写入】临时缓存文件服务：{}", cacheFile.getAbsolutePath());
	}
	
	@Override
	public void push(String worksLink) throws Exception {
		state = TaskState.RUN;
		try {
			if (cacheFile.length() >= CACHE_FLIE_MAX_SIZE) {
				createCacheWriter();
			}
			
			log.debug("向文件[{}]写入爬取图文缓存：{}", cacheFile.getName(), worksLink);
			
	        bufferedWriter.write(worksLink);
	        bufferedWriter.newLine();
	        bufferedWriter.flush();
        } catch (Exception e) {
        	log.error("写入缓存文件数据异常", e);
        	state = TaskState.EXCEPTION;
        	throw e;
        }
	}
	
	@Override
	public void close() throws Exception {
		
		try {
	        if (bufferedWriter != null) {
	        	bufferedWriter.flush();
	        	writer.flush();
	        	bufferedWriter.close();
	        }
	        if (writer != null) {
	        	writer.close();
	        }
	        
	        state = TaskState.FINISH;
        } catch (Exception e) {
	        log.error(e.getMessage(), e);
	        throw e;
        }
	}
	
	@Override
    public boolean isFinish() {
		return (state == TaskState.FINISH || state == TaskState.EXCEPTION);
    }
	
	private void createCacheWriter() {

		this.cacheFile = createCacheFile(cacheFileLocation, cacheFileName);

		try {
			//Thread.currentThread().setName(cacheFile.getName());
		
			writer = new FileWriter(cacheFile);
			bufferedWriter = new BufferedWriter(writer);
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        } 
	}
}
