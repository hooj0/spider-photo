package com.cnblogs.hoojo.support;

import java.util.Map;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.util.ConversionUtils;

/**
 * <b>function:</b> poco 站点作品爬取图文
 * 
 * @author hoojo
 * @createDate 2017-6-19 下午6:40:31
 * @file POCOSiteSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class POCOSiteSpider extends POCOBasedSpider {

	private static final String URL = "https://web-api.poco.cn/v1_1/works/get_works_list";
	private static final String HOME_PARAM = "{\"type\":\"${type}\",\"start\":%s,\"length\":%s,\"category\":\"${category}\",\"time_point\":1556532889,\"user_id\":null}";
	
	public POCOSiteSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}

	@Override
	protected String executedPageNext() {
		
		String executeURL = super.executedPageNext();
		if (executeURL == null) {
			return null;
		}
        
		Map<String, Object> params = ConversionUtils.convertQueryString(executeURL);
		String param = String.format(HOME_PARAM, this.getOptions().getCurrentPage() * LENGTH, LENGTH, System.currentTimeMillis());
		
		try {
			param = ConversionUtils.resolverExpression(param, params);
		} catch (Exception e) {
			log.error("转换参数表达式异常：", e);
		}
		
		String req = String.format(REQUEST_PARAM, System.currentTimeMillis(), param, genSignCode(param));
		executeURL += "&req=" + req;
		
		return executeURL;
    }
	
	public static void main(String[] args) {
		Options options = new Options();
		options.setMethod(Method.POST);
		options.setBeginPage(1);
		options.setPageNum(5);
		options.setSite("www.poco.cn");
		options.setPathMode(PathMode.SITE_TYPE);
        options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);
		//options.setMaxSpiderWorksNum(1);

		SpiderExecutor spider = null;
		
		spider = new POCOSiteSpider("POCO勋章作品-人像", URL + "?type=medal&category=1&p=", options);
		spider.execute();
		
		/*
		spider = new POCOSiteSpider("POCO编辑推荐-人像", URL + "?type=editor&category=1&p=", options);
		spider.execute();
		*/
	}
}
