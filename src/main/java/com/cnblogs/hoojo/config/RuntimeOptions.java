package com.cnblogs.hoojo.config;

import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;

/**
 * <b>function:</b> 运行时配置对象 不允许修改
 * 
 * @author hoojo
 * @createDate 2017-3-6 下午8:42:22
 * @file RuntimeOptions.java
 * @package com.cnblogs.hoojo.config
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class RuntimeOptions {

	private Options options;

	public RuntimeOptions(Options options) {
		this.options = options;
	}

	public int getMaxAnalyzerTaskNum() {
		return this.options.getMaxAnalyzerTaskNum();
	}

	public int getBeginPage() {
		return this.options.getBeginPage();
	}

	public int getPageNum() {
		return this.options.getPageNum();
	}

	public String getSite() {
		return this.options.getSite();
	}

	public String getSpiderURL() {
		return this.options.getSpiderURL();
	}

	public String getMatch() {
		return this.options.getMatch();
	}

	public String getSaveLocation() {
		return this.options.getSaveLocation();
	}

	public Method getMethod() {
		return this.options.getMethod();
	}

	public long getTimeout() {
		return this.options.getTimeout();
	}

	public boolean isOverride() {
		return this.options.isOverride();
	}

	public boolean isAsync() {
		return this.options.isAsync();
	}

	public NamedMode getNamedMode() {
		return this.options.getNamedMode();
	}

	public PathMode getPathMode() {
		return this.options.getPathMode();
	}
	public NamedMode getFileNameMode() {
		return this.options.getFileNameMode();
	}
	public int getMaxDownloadTaskNum() {
		return this.options.getMaxDownloadTaskNum();
	}
	public int getMaxSpiderWorksNum() {
		return this.options.getMaxSpiderWorksNum();
	}
	public String toString() {
		return this.options.toString();
	}
}
