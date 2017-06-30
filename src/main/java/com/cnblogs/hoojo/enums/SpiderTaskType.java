package com.cnblogs.hoojo.enums;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.analyzer.Analyzer;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;

/**
 * <b>function:</b> 抓取任务类型
 * @author hoojo
 * @createDate 2017-6-29 下午6:12:25
 * @file SpiderTaskType.java
 * @package com.cnblogs.hoojo.enums
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public enum SpiderTaskType {

	TumblrBlog("com.cnblogs.hoojo.support.TumblrBlogSpider", "汤博乐博客"),
	
	POCOBlog("com.cnblogs.hoojo.support.POCOBlogSpider", "破壳博客"),
	POCOSite("com.cnblogs.hoojo.support.POCOSiteSpider", "破壳站点"),

	CNUBlog("com.cnblogs.hoojo.support.CNUBlogSpider", "CNU博客"),
	CNUSite("com.cnblogs.hoojo.support.CNUSiteSpider", "CNU站点"),
	CNUHome("com.cnblogs.hoojo.support.CNUHomeSpider", "CNU首页");
	
	private String clazz;
	private String desc;
	private Options options;
	
	SpiderTaskType(String clazz, String desc) {
		this.clazz = clazz;
		this.desc = desc;
		
		this.options = new Options();
		
		this.options.setBeginPage(1);
		this.options.setPageNum(1);
		this.options.setMaxAnalyzerTaskNum(Analyzer.MAX_ANALYZER_TASK_NUM);
		this.options.setMaxDownloadTaskNum(SpiderExecutor.MAX_DOWNLOAD_TASK_NUM);
	}

	public String getClazz() {
		return clazz;
	}

	public String getDesc() {
		return desc;
	}

	public Options getOptions() {
		
		if (TumblrBlog == this) {
			
			options.setPathMode(PathMode.SITE);
			options.setNamedMode(NamedMode.DATE);
			options.setFileNameMode(NamedMode.TITLE_AUTHOR);
			
			options.setSpiderURL("http://wanimal1983.org/page/");
			
		} else if (StringUtils.contains(this.name(), "CNU")) {
			
			options.setPathMode(PathMode.SITE_TYPE);
	        options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);
	        
	        if (CNUSite == this) {
	        	
	        	options.setSpiderURL("http://www.cnu.cc/discoveryPage/hot-%E4%BA%BA%E5%83%8F?page=");
	        } else if (CNUHome == this) {
	        	
	        	options.setSpiderURL("http://www.cnu.cc/selectedsFlow/");
	        } else if (CNUBlog == this) {
	        	
	        	options.setPathMode(PathMode.SITE_TYPE_AUTHOR);
	    		options.setNamedMode(NamedMode.DATE_TITLE);
	        	options.setSpiderURL("http://www.cnu.cc/users/111111?page=");
	        }
		} else if (StringUtils.contains(this.name(), "POCO")) {
			
			options.setSite("www.poco.cn");
	        
	        if (POCOSite == this) {
	        	
	        	options.setPathMode(PathMode.SITE_TYPE);
	        	options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);
	        	options.setSpiderURL("http://photo.poco.cn/vision.htx&index_type=award&tid=-1&gid=0&p=");
	        	
	        } else if (POCOBlog == this) {
	        	
	        	options.setPathMode(PathMode.SITE_TYPE_AUTHOR);
	    		options.setNamedMode(NamedMode.DATE_TITLE);
	        	options.setSpiderURL("http://my.poco.cn/act/act_list.htx&user_id=111111&p=");
	        }
		}
		
		return options;
	}
}
