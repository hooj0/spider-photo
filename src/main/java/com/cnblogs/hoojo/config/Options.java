package com.cnblogs.hoojo.config;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;

/**
 * <b>function:</b> 运行任务参数
 * 
 * @author hoojo
 * @createDate 2017-3-3 下午10:08:01
 * @file Options.java
 * @package com.cnblogs.hoojo.config
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class Options {

	private String site;
	private String spiderURL;
	private String match;
	private String saveLocation;
	private int beginPage;
	private int pageNum;
	private int maxAnalyzerTaskNum;
	private int maxDownloadTaskNum;
	private int maxSpiderWorksNum;

	private Method method;
	private long timeout;
	private boolean override = false;
	private boolean async = true;

	private NamedMode fileNameMode;
	private NamedMode namedMode;
	private PathMode pathMode;

	public Options() {
	}

	public Options(String site, String spiderURL) {
		this.site = site;
		this.spiderURL = spiderURL;
	}

	public Options(String site, String spiderURL, String saveLocation, int beginPage) {
		super();
		this.site = site;
		this.spiderURL = spiderURL;
		this.saveLocation = saveLocation;
		this.beginPage = beginPage;
	}

	public int getMaxAnalyzerTaskNum() {
		return maxAnalyzerTaskNum;
	}

	public void setMaxAnalyzerTaskNum(int maxAnalyzerTaskNum) {
		this.maxAnalyzerTaskNum = maxAnalyzerTaskNum;
	}

	public int getBeginPage() {
		return beginPage;
	}

	public void setBeginPage(int beginPage) {
		this.beginPage = beginPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getSpiderURL() {
		return spiderURL;
	}

	public void setSpiderURL(String spiderURL) {
		this.spiderURL = spiderURL;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getSaveLocation() {
		return saveLocation;
	}

	public void setSaveLocation(String saveLocation) {
		this.saveLocation = saveLocation;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public NamedMode getNamedMode() {
		return namedMode;
	}

	public void setNamedMode(NamedMode namedMode) {
		this.namedMode = namedMode;
	}

	public PathMode getPathMode() {
		return pathMode;
	}

	public void setPathMode(PathMode pathMode) {
		this.pathMode = pathMode;
	}
	public NamedMode getFileNameMode() {
		return fileNameMode;
	}

	public void setFileNameMode(NamedMode fileNameMode) {
		this.fileNameMode = fileNameMode;
	}

	public int getMaxDownloadTaskNum() {
		return maxDownloadTaskNum;
	}
	
	public void setMaxDownloadTaskNum(int maxDownloadTaskNum) {
		this.maxDownloadTaskNum = maxDownloadTaskNum;
	}

	public int getMaxSpiderWorksNum() {
		return maxSpiderWorksNum;
	}
	
	public void setMaxSpiderWorksNum(int maxSpiderWorksNum) {
		this.maxSpiderWorksNum = maxSpiderWorksNum;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
