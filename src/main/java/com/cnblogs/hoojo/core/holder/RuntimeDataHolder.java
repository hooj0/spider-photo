package com.cnblogs.hoojo.core.holder;

import com.cnblogs.hoojo.config.RuntimeOptions;

/**
 * <b>function:</b> 运行时数据
 * @author hoojo
 * @createDate 2017-4-19 上午11:43:11
 * @file RuntimeDataHolder.java
 * @package com.cnblogs.hoojo.spider
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface RuntimeDataHolder {

	public RuntimeOptions getOptions();
	
	public String getThreadName();
}
