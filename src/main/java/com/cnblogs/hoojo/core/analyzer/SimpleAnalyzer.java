package com.cnblogs.hoojo.core.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.cnblogs.hoojo.core.holder.AnalyzerDataHolder;
import com.cnblogs.hoojo.enums.Method;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.util.ConversionUtils;

/**
 * <b>function:</b> 抽象简单分析器
 * 
 * @author hoojo
 * @createDate 2017-3-4 下午3:01:50
 * @file SimpleAnalyzer.java
 * @package com.cnblogs.hoojo.core.analyzer
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class SimpleAnalyzer extends ApplicationLogging implements Analyzer, AnalyzerDataHolder {
	
	private Queue<Works> spiderWaiting = new ConcurrentLinkedQueue<Works>();
	private Queue<Works> spiderCompleted = new ConcurrentLinkedQueue<Works>();
	private Queue<Works> spidering = new ConcurrentLinkedQueue<Works>();

	protected Document analyzerHTMLWeb(String url) throws Exception {
		return this.analyzerHTMLWeb(url, this.getOptions().getMethod());
	}

	protected Document analyzerHTMLWeb(String url, String charset) throws Exception {
        return this.analyzerHTMLWeb(url, this.getOptions().getMethod(), charset);
    }

    protected Document analyzerHTMLWeb(String url, Method method) throws Exception {
    	
    	try {
    		
    		Document document = null;
    		Connection connection = Jsoup.connect(url);
    		connection.timeout(new Long(this.getOptions().getTimeout()).intValue());
    		if (method == Method.GET) {
    			document = connection.get();
    		} else {
    			document = connection.post();
    		}
    		
    		return document;
    	} catch (IOException e) {
    		throw e;
    	}
    }
    
    protected Document analyzerHTMLWeb(String url, Method method, String charset) throws Exception {

    	// 打开网络连接
    	URL _url;
    	URLConnection connect;
    	// 获取网络输入流
    	InputStream is = null;
        try {
            
            Document document = null;
            if (StringUtils.isBlank(charset)) {
            	document = this.analyzerHTMLWeb(url, method);
            } else {
            	
                _url = new URL(url);
                connect = _url.openConnection();
                connect.setConnectTimeout(new Long(this.getOptions().getTimeout()).intValue());

                // 获取网页的输入流
                is = connect.getInputStream();
            	document = Jsoup.parse(is, charset, url);
            }
            
            return document;
        } catch (IOException e) {
            throw e;
        } finally {
        	if (is != null) {
        		is.close();
        	}
        }
    }

    protected Document analyzerHTMLWeb(String url, Map<String, String> cookies) throws Exception {
        return this.analyzerHTMLWeb(url, this.getOptions().getMethod(), cookies);
    }

    protected Document analyzerHTMLWeb(String url, Method method, Map<String, String> cookies) throws Exception {

        try {
            Document document = null;
            Connection connection = Jsoup.connect(url);
            connection.timeout(new Long(this.getOptions().getTimeout()).intValue());
            connection.cookies(cookies);

            if (method == Method.GET) {
                document = connection.get();
            } else {
                document = connection.post();
            }

            return document;
        } catch (IOException e) {
            throw e;
        }
    }

    protected List<Map<String, Object>> analyzerJSONContent(String json) throws Exception {
        return ConversionUtils.toList(json);
    }

    protected Map<String, Object> analyzerJSONWeb(String url) throws Exception {
        return this.analyzerJSONWeb(url, this.getOptions().getMethod());
    }

    protected Map<String, Object> analyzerJSONWeb(String url, Method method) throws Exception {

        try {
            return ConversionUtils.toMap(new URL(url));
            
            /*
            Map<String, Object> result = Maps.newHashMap();
            String json = ConversionUtils.readWebContent(url, new Long(options.getTimeout()).intValue());
            if (StringUtils.isBlank(json)) {
                try {
                    result = ConversionUtils.toMap(json);
                } catch (Exception e) {
                    // log.error("解析 json 异常：", e);
                    result = ConversionUtils.toMap(new URL(url));
                }

                return result;
            }
            return null;*/
        } catch (IOException e) {
            throw e;
        }
    }

	public final Queue<Works> getSpiderWaiting() {
		return spiderWaiting;
	}

	public final Queue<Works> getSpiderCompleted() {
		return spiderCompleted;
	}

	public final Queue<Works> getSpidering() {
		return spidering;
	}

	@Override
    public final AnalyzerDataHolder getDataHolder() {
	    return this;
    }
}
