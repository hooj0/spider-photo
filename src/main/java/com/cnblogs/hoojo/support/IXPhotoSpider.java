package com.cnblogs.hoojo.support;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.Jsoup;
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

/**
 * <b>function:</b> 1x.com 
 * @author hoojo
 * @createDate 2019年4月19日 下午3:07:11
 * @file IXPhotoSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class IXPhotoSpider extends AbstractSpider {
	
	private static final int SIZE = 30;
	private static final String DOMAIN = "https://gallery.1x.com";
	private static final String URL = DOMAIN + "/backend/loadmore.php?app=photos&userid=0";
	private static final String PARAM = "&from=%s&size=%s";
	
	
	public IXPhotoSpider(String spiderName, String spiderURL, Options spiderOptions) {
	    super(spiderName, spiderURL, spiderOptions);
    }
	
	@Override
	protected String executedPageNext() {
		
		String executeURL = super.executedPageNext();
		if (executeURL == null) {
			return null;
		}
        
		String param = String.format(PARAM, this.getOptions().getCurrentPage() * SIZE, SIZE);
		
		return executeURL + param;
    }

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		
		WorksQueue queue = new WorksQueue();
		try {
			Map<String, Object> params = ConversionUtils.convertQueryString(url);
			
			Document doc = this.analyzerHTMLWeb(url, this.getOptions().getMethod());
			doc = Jsoup.parseBodyFragment(doc.select("data").text());
			
			Elements links = doc.select("table.photos_rendertable td a[href^='/photo']");
			
			Iterator<Element> iter = links.iterator();
			while (iter.hasNext()) {
				Element link = iter.next();
				Elements item = link.select("table td");
				
				Works works = new Works();
				
				works.setId(link.attr("href"));
				works.setLink(DOMAIN + works.getId());
				works.setAuthor(item.select("a.dynamiclink").text());
				works.setBlog(DOMAIN + item.select("a.dynamiclink").attr("href"));
				works.setTitle(works.getAuthor());
				
				works.setSite(this.getOptions().getSite());
				works.setDate(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd"));
				works.setType(MapUtils.getString(params, "sort") + "~" + MapUtils.getString(params, "cat"));
				
				String img = item.select("img").attr("src");
				if (!StringUtils.endsWith(img, "nude-ld.jpg")) {
					img = DOMAIN + StringUtils.replaceOnce(img, "-ld.jpg", "-sd.jpg");
					works.getResources().add(img);

					queue.add(works);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		return queue;
	}

	@Override
	public List<String> analyzer(String link, Works works) throws Exception {
		return null;
	}
	
	public static void main(String[] args) {
		Options options = new Options();
		options.setBeginPage(0);
		options.setPageNum(70);
		options.setSite("1x.com");
		options.setPathMode(PathMode.SITE_TYPE);
		options.setNamedMode(NamedMode.DATE);
		options.setFileNameMode(NamedMode.TITLE_AUTHOR);
		//options.setMaxSpiderWorksNum(60);
		
		
		SpiderExecutor spider = null;
		
		spider = new IXPhotoSpider("1x 获奖作品", URL + "&cat=all&sort=curators-choice&p=", options);
		spider.execute();
		
		/*
		spider = new IXPhotoSpider("1x 流行作品", URL + "&cat=all&sort=popular&p=", options);
		spider.execute();
		
		spider = new IXPhotoSpider("1x 最新作品", URL + "&cat=all&sort=latest&p=", options);
		spider.execute();
		*/
    }
}
