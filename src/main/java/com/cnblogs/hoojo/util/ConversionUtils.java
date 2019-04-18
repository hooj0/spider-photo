package com.cnblogs.hoojo.util;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

/**
 * <b>function:</b> 转换工具类
 * @author hoojo
 * @createDate 2017-3-3 下午11:10:40
 * @file HttpUtils.java
 * @package com.cnblogs.hoojo.util
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class ConversionUtils {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> toList(String json) throws Exception {
	    
	    return MAPPER.readValue(json, List.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String json) throws Exception {
		
		return MAPPER.readValue(json, Map.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(URL url) throws Exception {
		
		return MAPPER.readValue(url, Map.class);
	}
	
	public static String toJSON(Object target) throws Exception {
		
		return MAPPER.writeValueAsString(target);
	}
	
	/**
	 * <b>function:</b> 解析语句中的表达式
	 * @author hoojo
	 * @createDate 2013-3-18 下午01:09:39
	 * @param target 查询语句/匹配类似velocity规则的字符串
	 * @param params 被替换关键字的的数据源
	 * @return 返回解析后的语句
	 * @throws Exception
	 */
	public static String resolverExpression(String target, Map<String, Object> params) throws Exception {
		// 生成匹配模式的正则表达式
		String patternString = "\\$\\{(" + StringUtils.join(params.keySet(), "|") + ")\\}";
		//String patternString = "\\$\\{([a-z0-9_\\.\\-\\+]+)\\}";
		Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
		//System.out.println("pattern: " + pattern.pattern());
		Matcher matcher = pattern.matcher(target);

		// 两个方法：appendReplacement, appendTail
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String text = getString(params, matcher.group(1), "");
			text = StringUtils.remove(text, "/");
			text = StringUtils.remove(text, "\\");
			// System.out.println("find group: " + matcher.group(1) + ", val: " + text);
			matcher.appendReplacement(sb, text);
		}
		matcher.appendTail(sb);

		//System.out.println("text: " + sb.toString());
		return sb.toString();
	}
	
	private static String getString(Map<String, ?> map, String key, String defaults) {
		
		if (map == null) {
			return defaults;
		}
		if (map.containsKey(key)) {
			return map.get(key) == null ? defaults : map.get(key).toString();
		}
		
		return defaults;
	}
	
	public static Map<String, Object> convertQueryString(String url) {
		Map<String, Object> items = Maps.newHashMap();
		
		if (StringUtils.contains(url, "?")) {
			url = StringUtils.substringAfter(url, "?");
		}
		
		String[] params = StringUtils.split(url, "&");
		for (String param : params) {
			String[] kv = StringUtils.split(param, "=");
			items.put(kv[0], kv[1]);
		}
		
		return items;
	}
}
