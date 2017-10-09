package com.cnblogs.hoojo.support;

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

/**
 * <b>function:</b> Tumblr博客
 * @author hoojo
 * @createDate 2017-4-19 下午10:51:40
 * @file TumblrBlogSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TumblrBlogSpider extends AbstractSpider {

	public TumblrBlogSpider(String spiderName, String spiderURL, Options spiderOptions) {
	    super(spiderName, spiderURL, spiderOptions);
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
		options.setPageNum(27);
		//options.setAsync(true);
		options.setPathMode(PathMode.SITE);
		options.setNamedMode(NamedMode.DATE);
		options.setFileNameMode(NamedMode.TITLE_AUTHOR);
		
		SpiderExecutor spider = new TumblrBlogSpider("Tumblr博客", "http://wanimal1983.org/page/", options);
		
		spider.execute();
    }
}
