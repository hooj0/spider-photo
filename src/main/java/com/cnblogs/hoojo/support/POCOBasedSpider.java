package com.cnblogs.hoojo.support;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.DigestUtils;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.spider.AbstractSpider;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;
import com.cnblogs.hoojo.util.FilePathNameUtils;
import com.google.common.collect.Lists;

/**
 * <b>function:</b> POCO 站点抓取图片基类
 * @author hoojo
 * @createDate 2019年4月29日 下午6:07:11
 * @file POCOBasedSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class POCOBasedSpider extends AbstractSpider {

	protected final String charset = "GBK";
	protected static final int LENGTH = 20;
	protected static final String REQUEST_PARAM = "{\"version\":\"1.1.0\",\"app_name\":\"poco_photography_wap\",\"os_type\":\"weixin\",\"is_enc\":0,\"env\":\"prod\",\"ctime\":%s,\"param\":%s,\"sign_code\":\"%s\"}";
	
	public POCOBasedSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WorksQueue analyzer(String url) throws Exception {
		WorksQueue queue = new WorksQueue();

		try {
			Map<String, Object> result = this.analyzerJSONWeb(url, this.getOptions().getMethod(), charset);
			
			if (StringUtils.equalsIgnoreCase(MapUtils.getString(result, "code"), "10000")) {
				
				String blog = "http://www.poco.cn/user/user_center?user_id=";
				
				Map<String, Object> data = MapUtils.getMap(result, "data");
                if (data != null) {
                	
                    Object workListObject = MapUtils.getObject(data, "list");
                    if (workListObject != null) {
                    	
                    	List<Map<String, Object>> workList = (List<Map<String, Object>>) workListObject;
                        for (Map<String, Object> workItem : workList) {
                        	Works works = new Works();

                        	works.setType(getType(url));
                        	works.setAvatar("http:" + MapUtils.getString(workItem, "user_avatar"));
                        	works.setId(MapUtils.getString(workItem, "works_id"));
                        	works.setBlog(blog + MapUtils.getString(workItem, "user_id"));
                        	works.setSite(this.getOptions().getSite());
                        	works.setAuthor(MapUtils.getString(workItem, "user_nickname"));

                        	String cover = "http:" + MapUtils.getString(MapUtils.getMap(workItem, "cover_image_info"), "file_url");
                        	works.setCover(cover);
                        	works.setLink(MapUtils.getString(workItem, "works_url"));
            				works.setTitle(FilePathNameUtils.clean(StringUtils.trim(MapUtils.getString(workItem, "title"))));
            				works.setComment(MapUtils.getString(workItem, "description"));
            				works.setDate(fillDate(MapUtils.getString(workItem, "create_time_str")));
            				String attract = "浏览：" + MapUtils.getString(workItem, "click_count");
            				attract += "喜欢：" + MapUtils.getString(workItem, "like_count");
            				attract += "评论：" + MapUtils.getString(workItem, "comment_count");
            				works.setAttract(attract);

            				queue.add(works);
                        }
                    }
                }
			} else {
                throw new RuntimeException("抓取数据异常:" + result);
            }
		} catch (Exception e) {
			throw e;
		}

		return queue;
	}

	@Override
	public List<String> analyzer(String link, Works works) throws Exception {
		
		List<String> list = Lists.newArrayList();
		Document doc;
		try {
			doc = this.analyzerHTMLWeb(link, "UTF-8");
			Elements imgEls = doc.select(".vw_h div img[data-src*='poco/works']");
			
			Iterator<Element> iter = imgEls.iterator();
			while (iter.hasNext()) {
				Element imgEl = iter.next();
				
				list.add("http:" + imgEl.attr("data-src"));
			}
		} catch (Exception e) {
			throw e;
		}
		
		return list;
	}
	
	protected String genSignCode(String param) {
		String signCode = DigestUtils.md5DigestAsHex(("poco_" + param + "_app").getBytes());
		signCode = StringUtils.substring(signCode, 5, 19 + 5);
		
		return signCode;
	}
	
	protected String fillDate(String date) {
		if (date.length() >= 8) {
			return date;
		} else {
			return DateFormatUtils.format(System.currentTimeMillis(), "yyyy年") + date;
		}
	}
	
	protected String getType(String url) {
		if (StringUtils.contains(url, "get_user_works_list")) {
			return "博客 - 作品";
		} else if (StringUtils.contains(url, "get_works_list")) {
			
			if (StringUtils.contains(url, "type=medal")) {
				return "勋章 - 作品";
			}
			if (StringUtils.contains(url, "type=editor")) {
				return "编辑 - 作品";
			}
			
		} else if (StringUtils.contains(url, "get_article_list")) {
			return "视觉漫游";
		}
		
		return "未知";
	}
}
