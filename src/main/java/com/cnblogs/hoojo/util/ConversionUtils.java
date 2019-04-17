package com.cnblogs.hoojo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	public static String readWebContent(String url, int timeout) throws Exception {
        String webContent = null;
        
        System.out.println("即将读入网址：" + url);
        // 临时标准网页的内存字节数组输出流，长度自动增长
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 打开网络连接
        URL requestURL;
        URLConnection urlConnection;
        // 获取网络输入流
        InputStream is = null;

        // 重置保存网页内容的内存字节数组输出流
        baos.reset();

        // 创建网页连接
        requestURL = new URL(url);
        urlConnection = requestURL.openConnection();
        urlConnection.setConnectTimeout(timeout);

        // 获取网页的输入流
        is = urlConnection.getInputStream();
        /***************************************
         * 获取编码
         ***************************************/
        String encoding = "";
        Map<String, List<String>> map = urlConnection.getHeaderFields();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        // 遍历,查找字符编码
        String key = null;
        String tmp = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            tmp = map.get(key).toString().toLowerCase();
            // 获取content-type charset
            if (key != null && key.equals("Content-Type")) {
                int m = tmp.indexOf("charset=");
                if (m != -1) {
                    encoding = tmp.substring(m + 8).replace("]", "");
                }
            }
        }
        /***************************************** strencoding有可能为空 ******************/
        // if(strencoding.length()==0) strencoding="gbk";
        // 读取网页内容，保存在网页内容的内存字节数组输出流
        int firstByte = is.read();
        int flag = 0;
        while (firstByte > 0) {
            // 忽略网页内容开头处的空格字符、回车、换行符
            // if (flag == 0 && (firstByte == 32||firstByte==13||firstByte==10)) {
            if (flag == 0 && firstByte <= 32) {
                firstByte = is.read();
                System.out.println("内层oneByte:" + firstByte);
                continue;
            }
            // 如果网页不是文本网页（通过判断网页的第一个字符是不是“<”（编码为60）），则退出
            if (flag == 0 && firstByte != 60) {
            	break;
            }
            // 如果网页是文本网页，设置标识变量
            else {
            	flag = 1;
            }

            // 将读取的网页字符内容写入内存字节数组输出流
            baos.write(firstByte);
            // 继续读下一个网页字符
            firstByte = is.read();
        }
        
        // 如果是文本网页
        if (flag == 1) {
            // 获取网页文本内容
            if (encoding.length() != 0) {
            	webContent = new String(baos.toByteArray(), encoding);
            } else {
            	webContent = new String(baos.toByteArray(), "UTF-8");
            }
        }
        
        return webContent;
    }
	
	public static String readFile(String path, String encoding) throws Exception {
		
		System.out.println("即将读入文件：" + path);
		File file = new File(path);
		
		String webContent = null;

		// 临时标准网页的内存字节数组输出流，长度自动增长
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 获取网页的输入流
        InputStream is = new FileInputStream(file);
        /***************************************** strencoding有可能为空 ******************/
        // if(strencoding.length()==0) strencoding="gbk";
        // 读取网页内容，保存在网页内容的内存字节数组输出流
        int firstByte = is.read();
        int flag = 0;
        while (firstByte > 0) {
            // 忽略网页内容开头处的空格字符、回车、换行符
            // if (flag == 0 && (firstByte == 32||firstByte==13||firstByte==10)) {
            if (flag == 0 && firstByte <= 32) {
                firstByte = is.read();
                System.out.println("内层oneByte:" + firstByte);
                continue;
            }
            // 如果网页不是文本网页（通过判断网页的第一个字符是不是“<”（编码为60）），则退出
            if (flag == 0 && firstByte != 60) {
            	break;
            }
            // 如果网页是文本网页，设置标识变量
            else {
            	flag = 1;
            }

            // 将读取的网页字符内容写入内存字节数组输出流
            baos.write(firstByte);
            // 继续读下一个网页字符
            firstByte = is.read();
        }
        
        // 如果是文本网页
        if (flag == 1) {
            // 获取网页文本内容
            if (encoding.length() != 0) {
            	webContent = new String(baos.toByteArray(), encoding);
            } else {
            	webContent = new String(baos.toByteArray(), "UTF-8");
            }
        }
        
        baos.flush();
        baos.close();
        is.close();
        
        return webContent;
	}
	
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
