package com.cnblogs.hoojo.support;

import java.util.Map;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.util.ConversionUtils;

/**
 * <b>function:</b> poco 博客爬取图文
 * 
 * @author hoojo
 * @createDate 2017-6-19 下午6:40:31
 * @file POCOBlogSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class POCOBlogSpider extends POCOBasedSpider {

	private static final String URL = "https://web-api.poco.cn/v1_1/space/get_user_works_list";
	private static final String BLOG_PARAM = "{\"visited_user_id\":${user_id},\"year\":\"\",\"length\":%s,\"start\":%s}"; 
	

	public POCOBlogSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}
	
	@Override
	protected String executedPageNext() {
		
		String executeURL = super.executedPageNext();
		if (executeURL == null) {
			return null;
		}
        
		Map<String, Object> params = ConversionUtils.convertQueryString(executeURL);
		String param = String.format(BLOG_PARAM, LENGTH, this.getOptions().getCurrentPage() * LENGTH);
		
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
		options.setBeginPage(0);
		options.setPageNum(1);
		options.setSite("www.poco.cn");
		options.setPathMode(PathMode.SITE_TYPE_AUTHOR);
		options.setNamedMode(NamedMode.DATE_TITLE);
		//options.setMaxSpiderWorksNum(1);

		SpiderExecutor spider = null;
		
		spider = new POCOBlogSpider("POCO博客-不厌", URL + "?user_id=174798930&p=", options);
		spider.execute();
		
		/*
		spider = new POCOBlogSpider("POCO博客-小刘", URL + "?user_id=52749018&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-李姣", URL + "?user_id=66065652&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-猫大大", URL + "?user_id=174936577&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-无限", URL + "?user_id=54397&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-疯子", URL + "?user_id=174572931&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-不二哥", URL + "?user_id=174054887&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Youth疯子", URL + "?user_id=174352321&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Junhee_晴天", URL + "?user_id=174748912&p=", options);
		spider.execute();
		 
		
		spider = new POCOBlogSpider("POCO博客-一叶城", URL + "?user_id=174419976&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-楚狂", URL + "?user_id=53393816&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-金浩森", URL + "?user_id=39472155&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-刘宥灵Jovie", URL + "?user_id=55929893&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-李钱钱", URL + "?user_id=175205246&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-牛牛很忙Binger", URL + "?user_id=55354497&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-一念尘", URL + "?user_id=174985063&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-研衔", URL + "?user_id=66096257&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-微醺十月", URL + "?user_id=174413193&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-黑卡露", URL + "?user_id=23760238&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-汤圆", URL + "?user_id=175123237&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Archer", URL + "?user_id=64211114&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-六指卫星", URL + "?user_id=174649232&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-追风的瑞恩", URL + "?user_id=66431972&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-刘顺儿妞", URL + "?user_id=53319835&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-阿牧AMS", URL + "?user_id=174487079&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-龄漫 ", URL + "?user_id=174815470&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-肉肉ROEM", URL + "?user_id=174202503&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-路寒", URL + "?user_id=44988233&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-J神", URL + "?user_id=64708926&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-花想衣裳", URL + "?user_id=64206635&p=", options);
		spider.execute();
		
		options.setPageNum(1);
		spider = new POCOBlogSpider("POCO博客-JessieYu.13", URL + "?user_id=64404724&p=", options);
		spider.execute();
		
		options.setPageNum(2);
		spider = new POCOBlogSpider("POCO博客-拾壹-MLZZ", URL + "?user_id=52812329&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客- Ryjoe", URL + "?user_id=174766030&p=", options);
		spider.execute();
		
		/////////////////
		spider = new POCOBlogSpider("POCO博客-Aaronsky", URL + "?user_id=19430718&p=", options);
		spider.execute();
		spider = new POCOBlogSpider("POCO博客-Luna_Atlantis", URL + "?user_id=173726994&p=", options);
		spider.execute();
		spider = new POCOBlogSpider("POCO博客-新叶", URL + "?user_id=53889050&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-李姣", URL + "?user_id=66065652&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-我不是耶稣", URL + "?user_id=38503959&p=", options);
		spider.execute();
	
		options.setPageNum(4);
		options.setMaxSpiderWorksNum(26);
		spider = new POCOBlogSpider("POCO博客-三火Yvan", URL + "?user_id=178974769&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Wynn温馨", URL + "?user_id=58840627&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-燕子", URL + "?user_id=43847200&p=", options);
		spider.execute();
		*/
	}
}
