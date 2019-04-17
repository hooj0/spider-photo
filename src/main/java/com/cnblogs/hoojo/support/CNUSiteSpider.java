package com.cnblogs.hoojo.support;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.spider.AbstractSpider;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;
import com.cnblogs.hoojo.util.ConversionUtils;
import com.cnblogs.hoojo.util.FilePathNameUtils;
import com.google.common.collect.Lists;

/**
 * <b>function:</b> CNU 人像-热门
 * @author hoojo
 * @createDate 2017-3-4 下午3:20:19
 * @file CNUSiteSpider.java
 * @package com.cnblogs.hoojo.spider
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class CNUSiteSpider extends AbstractSpider {

	String imageURL = "http://img.cnu.cc/uploads/images/920/";
	
	public CNUSiteSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		
		WorksQueue queue = new WorksQueue();
		try {
			Document doc = this.analyzerHTMLWeb(url, this.getOptions().getMethod());
			Elements articleEls = doc.select(".work-thumbnail");

			String maxType = StringUtils.substringBeforeLast(url, "/");
			String minType = StringUtils.substringBeforeLast(url, "?");
			String type = doc.select("#navbar li a[href^='" + maxType + "']").text() 
					+ " - " + doc.select("div.menu a.selected").text() 
					+ " - " + StringUtils.defaultIfEmpty(doc.select("div.group a[href^='" + minType + "']").text(), "全部");
			
			for (Element el : articleEls) {
				Works works = new Works();
				
				Elements thumbnail = el.getElementsByClass("thumbnail");
				works.setLink(thumbnail.attr("href"));
				works.setAuthor(FilePathNameUtils.clean(thumbnail.select(".author").text()));
				works.setTitle(FilePathNameUtils.clean(thumbnail.select(".title").text()));
				works.setCover(thumbnail.select("img").attr("src"));
				works.setId(StringUtils.substringAfterLast(works.getLink(), "/"));
				works.setSite(this.getOptions().getSite());
				works.setType(type);
				
				queue.add(works);
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
	        doc = this.analyzerHTMLWeb(link, this.getOptions().getMethod());
			
	        works.setAvatar(doc.select(".work-head .avatar img").attr("src"));
			works.setBlog(doc.select(".work-head .avatar a").attr("href"));
			works.setAttract(doc.select(".category").text());
			works.setComment(doc.select("#work_body p").text());
			works.setDate(StringUtils.substringBefore(doc.select(".author-info .timeago").attr("title"), " "));

			String imageJSON = doc.select("#imgs_json").text();
			List<Map<String, Object>> imageList = ConversionUtils.toList(imageJSON);
			Iterator<Map<String, Object>> iterList = imageList.iterator();
			while (iterList.hasNext()) {
				Map<String, Object> imageMap = iterList.next();
				list.add(imageURL + MapUtils.getString(imageMap, "img"));
			}
		} catch (Exception e) {
			throw e;
		}
		
		return list;
	}
	
	public static void main(String[] args) {
		
		Options options = new Options();
		options.setBeginPage(0);
		options.setPageNum(3);
		options.setAsync(true);
		options.setPathMode(PathMode.SITE_TYPE);
        options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);
		
		SpiderExecutor spider = null; 
		/*
		spider = new CNUSiteSpider("CNU主站原创-热门", "http://www.cnu.cc/discoveryPage/hot-0?page=", options);
		spider.execute();
		*/
		spider = new CNUSiteSpider("CNU主站原创-推荐", "http://www.cnu.cc/discoveryPage/recommend-0?page=", options);
		spider.execute();
		
		/*
		spider = new CNUSiteSpider("CNU主站原创-热门-人像", "http://www.cnu.cc/discoveryPage/hot-%E4%BA%BA%E5%83%8F?page=", options);
		spider.execute();

		spider = new CNUSiteSpider("CNU主站原创-推荐-人像", "http://www.cnu.cc/discoveryPage/recommend-%E4%BA%BA%E5%83%8F?page=", options);
		spider.execute();
		
		//-------------------------------------
		spider = new CNUSiteSpider("CNU主站灵感-热门", "http://www.cnu.cc/inspirationPage/hot-0?page=", options);
		spider.execute();
		
		spider = new CNUSiteSpider("CNU主站灵感-最新", "http://www.cnu.cc/inspirationPage/recent-0?page=", options);
		spider.execute();
		
		spider = new CNUSiteSpider("CNU主站灵感-热门-人像", "http://www.cnu.cc/inspirationPage/hot-111?page=", options);
		spider.execute();
		
		spider = new CNUSiteSpider("CNU主站灵感-最新-人像", "http://www.cnu.cc/inspirationPage/recent-111?page=", options);
		spider.execute();
		*/
	}
}
