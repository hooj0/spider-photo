package com.cnblogs.hoojo.support;

import java.util.List;
import java.util.Map;

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
	
	private static final int LENGTH = 30;
	private static final String PARAM = "";
	private static final String REQUEST_PARAM = "";
	
	private static final String URL = "https://gallery.1x.com/backend/loadmore.php";
	
	
	public IXPhotoSpider(String spiderName, String spiderURL, Options spiderOptions) {
	    super(spiderName, spiderURL, spiderOptions);
    }
	
	@Override
	protected String executedPageNext() {
		
		String executeURL = super.executedPageNext();
		if (executeURL == null) {
			return null;
		}
        
		Map<String, Object> params = ConversionUtils.convertQueryString(executeURL);
		String param = String.format(PARAM, this.getOptions().getCurrentPage() * LENGTH, LENGTH, System.currentTimeMillis());
		
		try {
			param = ConversionUtils.resolverExpression(param, params);
		} catch (Exception e) {
			log.error("转换参数表达式异常：", e);
		}
		
		String req = String.format(REQUEST_PARAM, System.currentTimeMillis(), param);
		executeURL += "&req=" + req;
		
		return executeURL;
    }

	@Override
	public WorksQueue analyzer(String url) throws Exception {
		
		WorksQueue queue = new WorksQueue();
		try {
			Document doc = this.analyzerHTMLWeb(url, this.getOptions().getMethod());
			String title = doc.select(".header .title a").text();

			Elements articleEls = doc.select(".content .post");
			for (Element el : articleEls) {
				Works works = new Works();
				
				Elements photos = el.getElementsByClass("photo-posts");
				Elements foot = el.getElementsByClass("post-foot");
				Elements author = foot.select("div[id^='like_button_']");
				
				works.setCover("");
				works.setId(author.attr("data-post-id"));
				works.setLink(foot.select(".datenotes a:eq(0)").attr("href"));
				works.setAuthor(author.attr("data-blog-name"));
				works.setTitle(photos.select(".photoCaption").text());
				works.setSite(this.getOptions().getSite());
				works.setDate(StringUtils.substringAfter(foot.select(".datenotes a:eq(0)").text(), " "));
				works.setType(title);
				
				Elements images = photos.select("img");
				for (Element img : images) {
					works.getResources().add(img.attr("src"));
				}
				
				if (images.size() > 0) {
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
		options.setPageNum(30);
		options.setSite("1x.com");
		options.setPathMode(PathMode.SITE);
		options.setNamedMode(NamedMode.DATE);
		options.setFileNameMode(NamedMode.TITLE_AUTHOR);
		options.setMaxSpiderWorksNum(1);
		
		
		SpiderExecutor spider = null;
		
		spider = new IXPhotoSpider("1x 获奖作品", URL + "?app=photos&userid=0&from=30&cat=all&sort=curators-choice&p=", options);
		spider.execute();
		
		/*
		spider = new IXPhotoSpider("1x 流行作品", URL + "?app=photos&userid=0&from=30&cat=all&sort=popular&p=", options);
		spider.execute();
		
		spider = new IXPhotoSpider("1x 最新作品", URL + "?app=photos&userid=0&from=30&cat=all&sort=latest&p=", options);
		spider.execute();
		*/
    }
}
