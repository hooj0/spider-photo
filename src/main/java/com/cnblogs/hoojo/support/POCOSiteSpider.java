package com.cnblogs.hoojo.support;

import java.util.Iterator;
import java.util.List;

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
import com.cnblogs.hoojo.util.FilePathNameUtils;
import com.google.common.collect.Lists;

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
public class POCOSiteSpider extends AbstractSpider {

	private final String charset = "GBK";
	private final String mobileURL = "http://m.poco.cn/vision/detail.php?photo_id=";
	private final String worksURL = "http://my.poco.cn/lastphoto_v2-htx-id-%s-user_id-%s-p-0.xhtml";
	
	public POCOSiteSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		WorksQueue queue = new WorksQueue();

		try {
			Document doc = this.analyzerHTMLWeb(url, charset);

			Elements articleEls = doc.select("#content .photo-works-list");

			Elements navbar = articleEls.select(".item-title");
			String type = navbar.select("ul.ui-btn-nav li.cur span").text() + " - " + navbar.select("div.menu input").val();
			
			Elements worksEls = articleEls.select("div[class=mod-txtimg230-list] li");
			for (Element thumbnail : worksEls) {
				Works works = new Works();

				String userId = thumbnail.select(".txt-box a[role=usercard]").attr("data-usercard-uid");
				String blog = thumbnail.select(".txt-box a[role=usercard]").attr("href");
				String author = FilePathNameUtils.clean(thumbnail.select(".txt-box a[role=usercard]").text()); 
				
				works.setLink(thumbnail.select(".img-box a").attr("href"));
				works.setId(StringUtils.substringBetween(works.getLink(), "htx-id-", "-p-0"));
				works.setLink(String.format(worksURL, works.getId(), userId));
				works.setAuthor(author);
				works.setTitle(FilePathNameUtils.clean(StringUtils.trim(thumbnail.select(".img-box a").attr("title"))));
				works.setCover(thumbnail.select(".img-box a img").attr("src"));
				works.setAttract(thumbnail.select(".txt-box p").text());
				works.setBlog(blog);
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
		link = mobileURL + works.getId();
		
		List<String> list = Lists.newArrayList();
		Document doc;
		try {
			doc = this.analyzerHTMLWeb(link, charset);
			
			Elements detailEls = doc.select(".vw_content");
			
			String avatar = StringUtils.remove(detailEls.select(".base_personhead .base_personimg img").attr("src"), "_32");
			works.setAvatar(avatar);
			works.setComment(detailEls.select(".base_detail_content .base_detail_article").text());
			works.setDate(doc.select(".base_detail_content .base_detail_date").text());
			works.setAuthor(detailEls.select(".base_personhead .base_personname").text());
			
			Elements imgEls = detailEls.select(".base_detail_content .base_detail_imagebox img");
			Iterator<Element> iter = imgEls.iterator();
			while (iter.hasNext()) {
				Element imgEl = iter.next();
				
				list.add(StringUtils.remove(imgEl.attr("data-src"), "_640"));
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
		options.setSite("www.poco.cn");
		options.setPathMode(PathMode.SITE_TYPE);
        options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);
		//options.setMaxSpiderWorksNum(1);

		SpiderExecutor spider = null;
		
		spider = new POCOSiteSpider("POCO勋章作品-人像", "http://photo.poco.cn/vision.htx&index_type=award&tid=-1&gid=0&p=", options);
		spider.execute();
		/*
		spider = new POCOSiteSpider("POCO热门作品-人像", "http://photo.poco.cn/vision.htx&index_type=hot&tid=-1&gid=0&p=", options);
		spider.execute();
		
		spider = new POCOSiteSpider("POCO编推作品-人像", "http://photo.poco.cn/vision.htx&index_type=edit&tid=-1&gid=0&p=", options);
		spider.execute();
		*/
	}
}
