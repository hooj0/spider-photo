package com.cnblogs.hoojo.support;

import java.util.Arrays;
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
import com.google.common.collect.Lists;

/**
 * hideakihamada.com 滨田英明 站点图片抓取
 * @author hoojo
 * @createDate 2019年4月27日 上午10:28:39
 * @file BTYMSiteSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class BTYMSiteSpider extends AbstractSpider {
	
	private static final String URL = "http://hideakihamada.com";
	
	public BTYMSiteSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
	}

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		WorksQueue queue = new WorksQueue();
		
		try {
			Document doc = this.analyzerHTMLWeb(url, this.getOptions().getMethod());
			Elements articleEls = doc.select("#menu_bar nav ul.menu_list li.category:lt(6)");
			
			for (Element typeEl : articleEls) {
				
				String type = typeEl.select("a.category_name").text();
				Elements workEls = typeEl.select("li.item > a");

				for (Element workEl : workEls) {
					
					if (workEl.attr("href").contains("www.")) {
						continue;
					}
					
					Works works = new Works();
					
					works.setId(workEl.attr("href"));
					works.setLink(URL + workEl.attr("href"));
					works.setSite(this.getOptions().getSite());
					works.setBlog(works.getLink());
					works.setAuthor("滨田英明");
					
					works.setTitle(workEl.text());
					works.setType(type);
					
					queue.add(works);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		return queue;
	}

	@SuppressWarnings("unused")
	@Override
	public List<String> analyzer(String link, Works works) throws Exception {
		List<String> list = Lists.newArrayList();
		Document doc;
		
		try {
	        doc = this.analyzerHTMLWeb(link, this.getOptions().getMethod());
			
	        String[] imgURLs = StringUtils.substringsBetween(doc.toString(), "\"image_url_900x0\":\"", "\",\"image_dimensions_900x0\"");
	        list = Arrays.asList(imgURLs);

	        if (true) {
	        	return list;
	        }
	        
	        
			String imageJSON = StringUtils.substringBetween(doc.toString(), "\"assets\":", "},\"title\":null");
			if (!StringUtils.isEmpty(imageJSON)) {
				List<Map<String, Object>> images = this.analyzerJSONContent(imageJSON);
				
				for (Map<String, Object> image : images) {
					list.add(MapUtils.getString(image, "image_url_900x0"));
				}
			} else {
				imageJSON = StringUtils.substringBetween(doc.toString(), "var _4ORMAT_DATA = ", "//]]>");
				System.out.println(imageJSON);
			}
		} catch (Exception e) {
			throw e;
		}
		
		return list;
	}

	public static void main(String[] args) {

		Options options = new Options();
		options.setBeginPage(1);
		options.setPageNum(1);
		options.setSite("hideakihamada.com");
		options.setPathMode(PathMode.SITE_TYPE);
		options.setNamedMode(NamedMode.TITLE);
		//options.setMaxSpiderWorksNum(1);
		
		
		SpiderExecutor spider = null;
		
		spider = new BTYMSiteSpider("滨田英明作品", URL + "?p=", options);
		spider.execute();
	}
}
