package com.cnblogs.hoojo.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.spider.AbstractSpider;
import com.cnblogs.hoojo.util.ConversionUtils;
import com.cnblogs.hoojo.util.FilePathNameUtils;

/**
 * <b>function:</b> POCO站点抽象爬取器
 * @author hoojo
 * @createDate 2019年4月28日 下午5:58:36
 * @file POCOBaseSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class POCOBaseSpider extends AbstractSpider {

	protected static final int LENGTH = 20;
	private static final String REQUEST_PARAM = "{\"version\":\"1.1.0\",\"app_name\":\"poco_photography_wap\",\"os_type\":\"weixin\",\"is_enc\":0,\"env\":\"prod\",\"ctime\":%s,\"param\":%s,\"sign_code\":\"%s\"}";
	
	protected static final String BLOG_PARAM = "{\"visited_user_id\":${userId},\"year\":\"\",\"length\":%s,\"start\":%s}"; 
	protected static final String HOME_PARAM = "{\"type\":\"medal\",\"start\":%s,\"length\":%s,\"category\":\"${category}\",\"time_point\":%s,\"user_id\":null}"; 
	
	public POCOBaseSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}
	
	@Override
	protected String executedPageNext() {
		String executeURL = super.executedPageNext();
        
		Map<String, Object> params = ConversionUtils.convertQueryString(executeURL);
		
		String param = String.format(BLOG_PARAM, LENGTH, this.getOptions().getCurrentPage() * LENGTH);
		
		try {
			param = ConversionUtils.resolverExpression(param, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String req = String.format(REQUEST_PARAM, System.currentTimeMillis(), param, genSignCode(param));
		executeURL += "&req=" + req;
		
		return executeURL;
    }

	protected String genSignCode(String param) {
		
		String signCode = DigestUtils.md5DigestAsHex(("poco_" + param + "_app").getBytes());
		System.out.println(signCode);
		
		signCode = StringUtils.substring(signCode, 5, 19 + 5);
		System.out.println(signCode);
		
		return signCode;
	}
	
	protected String genSignCode2(String param) throws Exception {
		
		Map<String, Object> e = new LinkedHashMap<>();
		e.put("visited_user_id", 174798930);
		e.put("year", "");
		e.put("length", 3);
		e.put("start", 10);
		
		String o = ConversionUtils.toJSON(e);
		System.out.println(o);
		
		String signCode = DigestUtils.md5DigestAsHex(("poco_" + param + "_app").getBytes());
		System.out.println(signCode);
		
		signCode = StringUtils.substring(signCode, 5, 19 + 5);
		System.out.println(signCode);
		
		return signCode;
	}
}
