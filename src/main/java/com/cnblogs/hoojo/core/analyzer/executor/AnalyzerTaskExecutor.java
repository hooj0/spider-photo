package com.cnblogs.hoojo.core.analyzer.executor;

/**
 * <b>function:</b> 分析执行器接口
 * @author hoojo
 * @createDate 2017-3-4 下午5:41:22
 * @file AnalyzerTaskExecutor.java
 * @package com.cnblogs.hoojo.core.analyzer.executor
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface AnalyzerTaskExecutor {

	public void doTask(String url);
	
	public void shutdownTask();
	
	public boolean isFinish();
}
