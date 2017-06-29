package com.cnblogs.hoojo.core.cache.optimizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.cache.AbstractCacheManager;
import com.cnblogs.hoojo.enums.TaskState;

/**
 * <b>function:</b> 合并优化缓存写入文件
 * @author hoojo
 * @createDate 2017-5-10 下午3:05:48
 * @file OptimizerCacheWriter.java
 * @package com.cnblogs.hoojo.core.cache
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class OptimizerCacheWriter extends AbstractCacheManager implements OptimizerCacheManager, Runnable {

	private TaskState state = TaskState.WAIT;
	
	private String site;
	private File optimizeFile;
	
	private Queue<String> worksLinks;
	private List<File> optimizeFiles;
	
	private FileWriter writer;
    private BufferedWriter bufferedWriter;
    
    private OptimizerCacheService cacheCervice;
    
	public OptimizerCacheWriter(String site, List<File> optimizeFiles, OptimizerCacheService cacheCervice) {
		this.site = site;
		this.optimizeFiles = optimizeFiles;
		
		this.cacheCervice = cacheCervice;
		
		try {
			if (OPTIMIZER_WORKS_LINK_CACHE.containsKey(site)) {
				worksLinks = OPTIMIZER_WORKS_LINK_CACHE.get(site);
			} else {
				worksLinks = new ConcurrentLinkedQueue<String>();
				OPTIMIZER_WORKS_LINK_CACHE.put(site, worksLinks);
			}
			
	        optimizeFile = createWriter(optimizeFiles);
	        log.info("【合并写入】图文缓存目标文件：{}", optimizeFile.getAbsolutePath());
		} catch (Exception e) {
	        log.error("合并文件创建读入流异常", e);
        }
	}
	
	@Override
    public void run() {
	    
		try {
			/*String threadName = "合并写入-" + optimizeFile.getName();
    		String name = Thread.currentThread().getName();
			if (StringUtils.contains(name, "#")) {
				name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "#");
			}
			Thread.currentThread().setName(threadName);
			*/
			optimizer();
        } catch (Exception e) {
        	log.error("写入缓存文件[{}]数据异常", optimizeFile.getAbsolutePath(), e);
        }
    }
	
	private void optimizer() throws Exception {
		state = TaskState.RUN;
		
		try {
		
			log.debug("【合并开始】读取缓存队列数据：{}", worksLinks.size());
	        while (true) {
	        	
	        	if (worksLinks.isEmpty()) {
	        		log.info("队列为空，即将退出文件合并任务！");
	        		
	        		state = TaskState.PAUSE;
	        		// 判断移动完成
	        		if (cacheCervice.isDone()) {
	        			break;
	        		} else {
	        			Thread.sleep(GlobalConst.OptimizerCacheServiceConst.CACHE_QUQUE_IDLE_MINUTES);
	        		}
	        	} else {
	        		
	        		String worksLink = worksLinks.remove();
	        		
	        		bufferedWriter.write(worksLink);
	        		bufferedWriter.newLine();
	        		bufferedWriter.flush();
	        		log.debug("向缓存文件[{}]写入数据：{}", optimizeFile.getName(), worksLink, worksLinks.size());
	        		
	        		if (optimizeFile.length() > CACHE_FLIE_MAX_SIZE) {
	        			boolean flag = optimizeFiles.remove(optimizeFile);
	        			log.debug("文件[{}]内容超过最大限制，删除文件是否成功：{}，/({})", optimizeFile.getName(), flag);
	        			
	        			this.close();
	        			optimizeFile = this.createWriter(optimizeFiles);
	        			log.debug("创建新文件存储合并图文缓存：{}", optimizeFile.getAbsolutePath());
	        		}
	        	}
	        }
	        
	        log.debug("【合并完成】读取缓存队列数据：{}", worksLinks.size());
        } catch (Exception e) {
        	state = TaskState.EXCEPTION;
	        throw e;
        } finally {
        	try {
        		this.close();
            } catch (Exception e) {
            	log.error("关闭写入缓存文件数据异常", e);
            }
        }
		
		state = TaskState.FINISH;
	}
	
	private File createWriter(List<File> optimizeFiles) throws Exception {
		
		try {
			File optimizeFile = null;
	        for (File file : optimizeFiles) {
	        	if (file.length() < CACHE_FLIE_MAX_SIZE) {
	        		optimizeFile = file;
	        		break;
	        	} 
	        }
	        
	        if (optimizeFile == null) {
	        	optimizeFile = createCacheFile(optimizeRootDir().getAbsolutePath(), site);
	        }
	        
        	writer = new FileWriter(optimizeFile, true); // 追加缓存内容
    		bufferedWriter = new BufferedWriter(writer);
    		
    		return optimizeFile;
        } catch (Exception e) {
	        throw e;
        }
	}
	
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
        } catch (Exception e) {
	        log.error(e.getMessage(), e);
	        throw e;
        }
	}

	@Override
    public TaskState getState() {
	    return state;
    }

	@Override
    public boolean isFinish() {
	    return (state == TaskState.FINISH || state == TaskState.EXCEPTION);
    }
}
