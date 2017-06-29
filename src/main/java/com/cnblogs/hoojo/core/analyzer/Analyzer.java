package com.cnblogs.hoojo.core.analyzer;

import java.util.List;

import com.cnblogs.hoojo.core.holder.AnalyzerDataHolder;
import com.cnblogs.hoojo.core.holder.RuntimeDataHolder;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;

/**
 * <b>function:</b> 分析器，分析请求内容
 * 
 * @author hoojo
 * @createDate 2017-3-4 下午5:19:54
 * @file Analyzer.java
 * @package com.cnblogs.hoojo.core.analyzer
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface Analyzer extends RuntimeDataHolder {

	public static final int MAX_ANALYZER_TASK_NUM = 6;
	public static final int MIN_ANALYZER_TASK_NUM = 3;
	
	public static final int MAX_ANALYZER_POOL_NUM = 6;
	public static final int MAX_ANALYZER_POOL_THREAD_NUM = 20;

	public WorksQueue analyzer(String url) throws Exception;

	public List<String> analyzer(String link, Works works) throws Exception;
	
	public AnalyzerDataHolder getDataHolder();
}
