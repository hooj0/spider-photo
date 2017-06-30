package com.cnblogs.hoojo.controller;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.core.listening.ListeningDownloadManager;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.enums.SpiderTaskType;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.service.OneKeyExtractService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * <b>function:</b> 抓取图文控制器
 * @author hoojo
 * @createDate 2017-6-29 下午4:53:30
 * @file SpiderController.java
 * @package com.cnblogs.hoojo.controller
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
@Controller
public class SpiderController extends ApplicationLogging {

	private static final String SPIDER_KEY = "_spider_";
	
    @Resource
    private OneKeyExtractService extractService;
    
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    
    @RequestMapping("/console/single-task")
    public String singleTaskConsole(SpiderTaskType taskType, ModelMap model) {
        
    	model.put("namedModes", NamedMode.values());
    	model.put("pathModes", PathMode.values());

    	model.put("taskTypes", SpiderTaskType.values());
    	model.put("taskType", taskType);
    	
        return "single-task";
    }
    
    @RequestMapping("/console/onekey-task")
    public String oneKeyConsole(ModelMap model) {
        
    	model.put("executors", extractService.getExecutors());
    	
        return "onekey-task";
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/exec-task")
    @ResponseBody
    public String executeTask(String spiderClazz, String spiderName, Options options, HttpServletRequest request) throws Exception {
        try {
            
        	Class<SpiderExecutor> clazz = (Class<SpiderExecutor>) Class.forName(spiderClazz);
        	Constructor<SpiderExecutor> constructor = clazz.getConstructor(String.class, String.class, Options.class);
        	
			SpiderExecutor spider = constructor.newInstance(spiderName, options.getSpiderURL(), options);
			spider.execute();
            
			String spiderKey = SPIDER_KEY + System.currentTimeMillis();
			request.getSession().setAttribute(spiderKey, spider);
			
            return spiderKey;
        } catch (Exception e) {
            error("爬取数据异常", e);
            return "操作失败，" + e.getMessage();
        }
    }
    
    @RequestMapping("/onekey-task")
    @ResponseBody
    public String onekeyTask() throws Exception {
        try {
            
            taskExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return null;
                }
            });
            
            extractService.extractor();
            
            return "success";
        } catch (Exception e) {
            error("爬取数据异常", e);
            return "操作失败，" + e.getMessage();
        }
    }
    
    @RequestMapping("/onekey-task-progress")
    @ResponseBody
    public List<SpiderExecutor> onekeyTaskProgress() throws Exception {
    	
    	return extractService.getExecutors();
    }
    
    @RequestMapping("/single-task-progress")
    @ResponseBody
    public Map<String, Object> singleTaskProgress(String spiderKey, HttpServletRequest request) throws Exception {
        
    	SpiderExecutor spider = (SpiderExecutor) request.getSession().getAttribute(spiderKey);
    	
    	ListeningDownloadManager downloadManager = spider.getListeningDownloadManager();
    	
    	Map<String, Object> holder = Maps.newHashMap();
    	
    	holder.put("spiderState", spider.getSpiderState());
    	holder.put("downloadState", downloadManager.getDownloadState());

    	holder.put("spiderHolder", ImmutableMap.of(
  		                           "spider-wait", spider.getAnalyzer().getDataHolder().getSpiderWaiting(), 
  		                           "spidering", spider.getAnalyzer().getDataHolder().getSpidering(), 
  		                           "spider-finish", spider.getAnalyzer().getDataHolder().getSpiderCompleted()));
    	
		holder.put("downloadHolder", ImmutableMap.of(
		                           "downloading", downloadManager.getDataHolder().getDownloading(), 
		                           "download-finish", downloadManager.getDataHolder().getDownloadCompleted(), 
		                           "download-error", downloadManager.getDataHolder().getDownloadErrored()));
    	
		return holder;
    }
    
    @SuppressWarnings("all")
    @ExceptionHandler
	public void handlerException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		error(ex.getMessage(), ex);
	}
}
