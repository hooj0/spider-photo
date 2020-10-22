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
 * <b>function:</b> CNU博客
 * @author hoojo
 * @createDate 2017-4-21 上午10:41:26
 * @file CNUBlogSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class CNUBlogSpider extends AbstractSpider {

	String imageURL = "http://imgoss.cnu.cc/";
	String oldImageURL = "http://img.cnu.cc/forum/";
	int oldDate = 201501;
	
	public CNUBlogSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		WorksQueue queue = new WorksQueue();
		
		try {
			Document doc = this.analyzerHTMLWeb(url, this.getOptions().getMethod());
			Elements articleEls = doc.select(".work-thumbnail");

			String blog = StringUtils.substringBefore(url, "?");
			String type = "博客 - " + StringUtils.substringBefore(doc.select(".page-header li a[href='" + blog + "']").text(), "(");
			String author = FilePathNameUtils.clean(doc.select(".author_info .author_name").text());
			String avatar = doc.select(".author_info img.avatar").attr("src");
			for (Element el : articleEls) {
				Works works = new Works();
				
				Elements thumbnail = el.getElementsByClass("thumbnail");
				works.setLink(thumbnail.attr("href"));
				works.setAuthor(author);
				works.setTitle(FilePathNameUtils.clean(StringUtils.trim(thumbnail.select(".title").text())));
				works.setCover(thumbnail.select("img").attr("src"));
				works.setId(StringUtils.substringAfterLast(works.getLink(), "/"));
				works.setBlog(blog);
				works.setAvatar(avatar);
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
			
			works.setAttract(doc.select(".category").text());
			works.setComment(doc.select("#work_body p").text());
			works.setDate(StringUtils.substringBefore(doc.select(".author-info .timeago").attr("title"), " "));

			String imageJSON = doc.select("#imgs_json").text();
			List<Map<String, Object>> imageList = ConversionUtils.toList(imageJSON);
			Iterator<Map<String, Object>> iterList = imageList.iterator();
			while (iterList.hasNext()) {
				Map<String, Object> imageMap = iterList.next();
				String imgPath = MapUtils.getString(imageMap, "img");
				if (MapUtils.getIntValue(imageMap, "type") == 1) {
					list.add(oldImageURL + imgPath);
				} else {
					list.add(imageURL + imgPath);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		return list;
	}

	public static void main(String[] args) {
	    
		Options options = new Options();
		options.setBeginPage(1);
		options.setPageNum(5);
		options.setAsync(true);
		options.setSite("cnu.cc");
		options.setPathMode(PathMode.SITE_TYPE_AUTHOR);
		options.setNamedMode(NamedMode.DATE_TITLE);
		options.setMaxSpiderWorksNum(1);
		//options.setMatch("98471");
		
		SpiderExecutor spider = new CNUBlogSpider("CNU博客-双喜儿", "http://www.cnu.cc/users/106718?page=", options);
		spider.execute();
		/*
		SpiderExecutor spider = new CNUBlogSpider("CNU博客-侃烃", "http://www.cnu.cc/users/295337?page=", options);
		spider.execute();

		
		SpiderExecutor spider = new CNUBlogSpider("CNU博客-月下汲水", "http://www.cnu.cc/users/259198?page=", options);
		spider.execute();
		
		SpiderExecutor spider = new CNUBlogSpider("CNU博客-秋明", "http://www.cnu.cc/users/129162?page=", options);
		spider.execute();
		
		
		SpiderExecutor spider = new CNUBlogSpider("CNU博客-小莹", "http://www.cnu.cc/users/121692?page=", options);
		spider.execute();
		
		spider = new CNUBlogSpider("CNU博客-姬卡", "http://www.cnu.cc/users/203572?page=", options);
		spider.execute();
		*/
    }
}
