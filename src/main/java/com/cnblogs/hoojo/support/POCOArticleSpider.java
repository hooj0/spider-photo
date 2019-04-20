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
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;
import com.cnblogs.hoojo.util.ConversionUtils;
import com.cnblogs.hoojo.util.FilePathNameUtils;
import com.google.common.collect.Lists;

/**
 * <b>function:</b>
 * @author hoojo
 * @createDate 2019年4月19日 下午6:36:15
 * @file POCOArticleSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class POCOArticleSpider extends POCOBasedSpider {
	private static final String URL = "https://web-api.poco.cn/v1_1/article/get_article_list";
	private static final String PARAM = "{\"type_id\":4,\"start\":%s,\"length\":%s}";
	
	public POCOArticleSpider(String spiderName, String spiderURL, Options options) {
		super(spiderName, spiderURL, options);
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
		
		String req = String.format(REQUEST_PARAM, System.currentTimeMillis(), param, genSignCode(param));
		executeURL += "&req=" + req;
		
		return executeURL;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public WorksQueue analyzer(String url) throws Exception {
		WorksQueue queue = new WorksQueue();

		try {
			Map<String, Object> result = this.analyzerJSONWeb(url, this.getOptions().getMethod(), charset);
			
			if (StringUtils.equalsIgnoreCase(MapUtils.getString(result, "code"), "10000")) {
				
				String blog = "https://www.poco.cn/works/works_list?classify_type=1&works_type=view";
				
				Map<String, Object> data = MapUtils.getMap(result, "data");
                if (data != null) {
                	
                    Object workListObject = MapUtils.getObject(data, "list");
                    if (workListObject != null) {
                    	
                    	List<Map<String, Object>> workList = (List<Map<String, Object>>) workListObject;
                        for (Map<String, Object> workItem : workList) {
                        	Works works = new Works();

                        	works.setType(getType(url));
                        	works.setId(MapUtils.getString(workItem, "article_id"));
                        	works.setBlog(blog);
                        	works.setSite(this.getOptions().getSite());
                        	works.setAuthor("视觉漫游");

                        	String cover = "http:" + MapUtils.getString(MapUtils.getMap(workItem, "cover_image_info"), "file_url");
                        	works.setCover(cover);
                        	works.setLink(MapUtils.getString(workItem, "url"));
            				works.setTitle(FilePathNameUtils.clean(StringUtils.trim(MapUtils.getString(workItem, "title"))));
            				works.setComment(MapUtils.getString(workItem, "description"));
            				works.setDate(MapUtils.getString(workItem, "create_time_str"));
            				String attract = "浏览：" + MapUtils.getString(workItem, "click_count");
            				attract += "喜欢：" + MapUtils.getString(workItem, "collect_count");
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
			System.out.println(link);
			System.out.println(doc.toString());
			
			String json = doc.select("div[class='json_hidden'] > textarea[jsonname='ret_json']").text();

			Map<String, Object> data = ConversionUtils.toMap(json);
			
			Object tmp = MapUtils.getObject(data, "content");
			if (tmp == null) {
				return list;
			}
			
			List<Map<String, Object>> images = (List<Map<String, Object>>) tmp;
			
		} catch (Exception e) {
			throw e;
		}
		
		return list;
	}

	public static void main(String[] args) {
		Options options = new Options();
		options.setMethod(Method.POST);
		options.setBeginPage(1);
		options.setPageNum(5);
		options.setAsync(true);
		options.setSite("www.poco.cn");
		options.setPathMode(PathMode.SITE_TYPE);
        options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);
		options.setMaxSpiderWorksNum(1);

		SpiderExecutor spider = null;
		
		spider = new POCOArticleSpider("POCO视觉漫游", URL + "?classify_type=1&works_type=view&p=", options);
		spider.execute();
	}
}
