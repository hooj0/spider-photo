package com.cnblogs.hoojo.support;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.spider.AbstractSpider;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;
import com.cnblogs.hoojo.util.ConversionUtils;
import com.cnblogs.hoojo.util.FilePathNameUtils;
import com.google.common.collect.Lists;

/**
 * <b>function:</b> cnu 首页抓取
 * 
 * @author hoojo
 * @createDate 2017-4-16 下午5:05:03
 * @file CNUHomeSpider.java
 * @package com.cnblogs.hoojo.support
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class CNUHomeSpider extends AbstractSpider {

    String worksURL = "http://www.cnu.cc/works/";
    String imageURL = "http://imgoss.cnu.cc/";

    public CNUHomeSpider(String spiderName, String spiderURL, Options options) {
        super(spiderName, spiderURL, options);
        
        System.out.println(options);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WorksQueue analyzer(String url) throws Exception {
        WorksQueue queue = new WorksQueue();
        
        try {
            Map<String, Object> result = this.analyzerJSONWeb(url);
            if (StringUtils.equalsIgnoreCase(MapUtils.getString(result, "status"), "success")) {

                Object object = MapUtils.getObject(result, "data");
                if (object != null) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) object;

                    String type = "首页";
                    for (Map<String, Object> item : data) {
                        //String date = MapUtils.getString(item, "date");

                        Object workListObject = MapUtils.getObject(item, "works");
                        if (workListObject != null) {

                            List<Map<String, Object>> workList = (List<Map<String, Object>>) workListObject;
                            for (Map<String, Object> workItem : workList) {
                                if (StringUtils.equals(MapUtils.getString(workItem, "type"), "-2")) {
                                    continue;
                                }

                                Works works = new Works();

                                works.setId(MapUtils.getString(workItem, "id"));
                                works.setAuthor(FilePathNameUtils.clean(MapUtils.getString(workItem, "author_display_name")));
                                works.setTitle(FilePathNameUtils.clean(MapUtils.getString(workItem, "title")));
                                works.setCover(imageURL + MapUtils.getString(workItem, "cover"));
                                works.setLink(worksURL + works.getId());
                                works.setSite(this.getOptions().getSite());
                                works.setType(type);

                                queue.add(works);
                            }
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
        options.setBeginPage(1);
        options.setPageNum(50);
        options.setAsync(true);
        options.setSite("cnu.cc");
        options.setMaxAnalyzerTaskNum(5);
        options.setPathMode(PathMode.SITE_TYPE);
        options.setNamedMode(NamedMode.DATE_TITLE_AUTHOR);

        CNUHomeSpider spider = new CNUHomeSpider("CNU主站首页", "http://www.cnu.cc/selectedsFlow/", options);
        spider.execute();
    }
}
