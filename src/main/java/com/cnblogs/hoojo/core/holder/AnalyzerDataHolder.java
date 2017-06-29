package com.cnblogs.hoojo.core.holder;

import java.util.Queue;

import com.cnblogs.hoojo.model.Works;

/**
 * <b>function:</b> 分析数据
 * @author hoojo
 * @createDate 2017-4-18 下午9:24:47
 * @file AnalyzerDataHolder.java
 * @package com.cnblogs.hoojo.core.holder
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface AnalyzerDataHolder {

	public Queue<Works> getSpiderWaiting();

	public Queue<Works> getSpiderCompleted();

	public Queue<Works> getSpidering();
}
