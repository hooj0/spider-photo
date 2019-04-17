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
public class POCOBlogSpider extends AbstractSpider {

	private final String charset = "GBK";
	private final String mobileURL = "http://www.poco.cn/user/user_center?user_id=";
	
	public POCOBlogSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		WorksQueue queue = new WorksQueue();

		try {
			Document doc = this.analyzerHTMLWeb(url, charset);

			Elements articleEls = doc.select("#content .module_body ul li:not([class])");

			String type = "博客 - 作品";
			String blog = doc.select("#navbar ul li:eq(0) a").attr("href");
			String author = FilePathNameUtils.clean(doc.select(".user_info_bar").text()); 
			
			for (Element thumbnail : articleEls) {
				Works works = new Works();

				works.setLink(thumbnail.select(".title a").attr("href"));
				works.setAuthor(author);
				works.setTitle(FilePathNameUtils.clean(StringUtils.trim(thumbnail.select(".title").text())));
				works.setCover(thumbnail.select(".summary img").attr("src"));
				works.setId(StringUtils.substringBetween(works.getLink(), "htx-id-", "-user_id"));
				works.setAttract(thumbnail.select(".stat").text());
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
	
	public List<String> _analyzer(String link, Works works) throws Exception {

		List<String> list = Lists.newArrayList();
		Document doc;
		try {
			doc = this.analyzerHTMLWeb(link, this.getOptions().getMethod());

			Elements detailEls = doc.select("#content .module_body .act_detail_info");

			String avatar = detailEls.select(".author_info .author_icon img").attr("src");
			works.setAvatar(avatar);
			works.setComment(detailEls.select(".content p").text());
			works.setDate(StringUtils.substringAfterLast(doc.select(".lastphoto_table tr:eq(0) td:eq(0)").text(), "："));

			Elements scriptEls = detailEls.select("script");
			Iterator<Element> iter = scriptEls.iterator();
			while (iter.hasNext()) {
				Element script = iter.next();

				if (StringUtils.contains(script.data(), "输出相片数组")) {
					String text = StringUtils.substringBetween(script.data(), "photoData:", "'}]");
					analyzerImage(text, list);
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return list;
	}

	private void analyzerImage(String text, List<String> list) throws Exception {
		String[] imageURLs = StringUtils.substringsBetween(text, "originPhoto:'", "',bigPhoto:");
		for (String img : imageURLs) {
			list.add(img);
		}
	}

	public static void main(String[] args) {
		Options options = new Options();
		options.setBeginPage(1);
		options.setPageNum(7);
		options.setAsync(true);
		options.setSite("www.poco.cn");
		options.setPathMode(PathMode.SITE_TYPE_AUTHOR);
		options.setNamedMode(NamedMode.DATE_TITLE);
		//options.setMaxSpiderWorksNum(1);

		SpiderExecutor spider = null;
		
		spider = new POCOBlogSpider("POCO博客-小刘", "http://my.poco.cn/act/act_list.htx&user_id=52749018&p=", options);
		spider.execute();
		/*

		spider = new POCOBlogSpider("POCO博客-李姣", "http://my.poco.cn/act/act_list.htx&user_id=66065652&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-猫大大", "http://my.poco.cn/act/act_list.htx&user_id=174936577&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-无限", "http://my.poco.cn/act/act_list.htx&user_id=54397&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-疯子", "http://my.poco.cn/act/act_list.htx&user_id=174572931&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-不二哥", "http://my.poco.cn/act/act_list.htx&user_id=174054887&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Youth疯子", "http://my.poco.cn/act/act_list.htx&user_id=174352321&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Junhee_晴天", "http://my.poco.cn/act/act_list.htx&user_id=174748912&p=", options);
		spider.execute();
		 
		
		spider = new POCOBlogSpider("POCO博客-一叶城", "http://my.poco.cn/act/act_list.htx&user_id=174419976&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-楚狂", "http://my.poco.cn/act/act_list.htx&user_id=53393816&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-金浩森", "http://my.poco.cn/act/act_list.htx&user_id=39472155&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-刘宥灵Jovie", "http://my.poco.cn/act/act_list.htx&user_id=55929893&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-李钱钱", "http://my.poco.cn/act/act_list.htx&user_id=175205246&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-牛牛很忙Binger", "http://my.poco.cn/act/act_list.htx&user_id=55354497&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-一念尘", "http://my.poco.cn/act/act_list.htx&user_id=174985063&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-研衔", "http://my.poco.cn/act/act_list.htx&user_id=66096257&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-微醺十月", "http://my.poco.cn/act/act_list.htx&user_id=174413193&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-黑卡露", "http://my.poco.cn/act/act_list.htx&user_id=23760238&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-汤圆", "http://my.poco.cn/act/act_list.htx&user_id=175123237&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Archer", "http://my.poco.cn/act/act_list.htx&user_id=64211114&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-六指卫星", "http://my.poco.cn/act/act_list.htx&user_id=174649232&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-追风的瑞恩", "http://my.poco.cn/act/act_list.htx&user_id=66431972&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-刘顺儿妞", "http://my.poco.cn/act/act_list.htx&user_id=53319835&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-阿牧AMS", "http://my.poco.cn/act/act_list.htx&user_id=174487079&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-龄漫 ", "http://my.poco.cn/act/act_list.htx&user_id=174815470&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-肉肉ROEM", "http://my.poco.cn/act/act_list.htx&user_id=174202503&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-路寒", "http://my.poco.cn/act/act_list.htx&user_id=44988233&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-J神", "http://my.poco.cn/act/act_list.htx&user_id=64708926&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-花想衣裳", "http://my.poco.cn/act/act_list.htx&user_id=64206635&p=", options);
		spider.execute();
		
		options.setPageNum(1);
		spider = new POCOBlogSpider("POCO博客-JessieYu.13", "http://my.poco.cn/act/act_list.htx&user_id=64404724&p=", options);
		spider.execute();
		
		options.setPageNum(2);
		spider = new POCOBlogSpider("POCO博客-拾壹-MLZZ", "http://my.poco.cn/act/act_list.htx&user_id=52812329&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客- Ryjoe", "http://my.poco.cn/act/act_list.htx&user_id=174766030&p=", options);
		spider.execute();
		
		/////////////////
		spider = new POCOBlogSpider("POCO博客-Aaronsky", "http://my.poco.cn/act/act_list.htx&user_id=19430718&p=", options);
		spider.execute();
		spider = new POCOBlogSpider("POCO博客-Luna_Atlantis", "http://my.poco.cn/act/act_list.htx&user_id=173726994&p=", options);
		spider.execute();
		spider = new POCOBlogSpider("POCO博客-新叶", "http://my.poco.cn/act/act_list.htx&user_id=53889050&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-李姣", "http://my.poco.cn/act/act_list.htx&user_id=66065652&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-我不是耶稣", "http://my.poco.cn/act/act_list.htx&user_id=38503959&p=", options);
		spider.execute();
	
		options.setPageNum(4);
		options.setMaxSpiderWorksNum(26);
		spider = new POCOBlogSpider("POCO博客-三火Yvan", "http://my.poco.cn/act/act_list.htx&user_id=178974769&p=", options);
		spider.execute();
		
		spider = new POCOBlogSpider("POCO博客-Wynn温馨", "http://my.poco.cn/act/act_list.htx&user_id=58840627&p=", options);
		spider.execute();

		spider = new POCOBlogSpider("POCO博客-燕子", "http://my.poco.cn/act/act_list.htx&user_id=43847200&p=", options);
		spider.execute();
		*/
	}
}
