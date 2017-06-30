package com.cnblogs.hoojo.service;

import java.util.List;

import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.log.ApplicationLogging;

/**
 * <b>function:</b> 一键执行任务
 * @author hoojo
 * @createDate 2017-3-2 下午11:24:47
 * @file OneKeyExtractService.java
 * @package com.cnblogs.hoojo.service
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class OneKeyExtractService extends ApplicationLogging {
	
	private List<SpiderExecutor> executors;
	
	public void extractor() {
		
		log.info("-------------------一键任务执行开始-------------------");
		
		for (SpiderExecutor executor : executors) {
			
			log.info("即将运行任务：{}", executor.getSpiderName());
			executor.execute();
		}
		
		log.info("-------------------一键任务执行完成-------------------");
	}

	public void setExecutors(List<SpiderExecutor> executors) {
		this.executors = executors;
	}

	public List<SpiderExecutor> getExecutors() {
		return executors;
	}
}
