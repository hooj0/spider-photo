package com.cnblogs.hoojo.core.spider;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.cnblogs.hoojo.config.Options;
import com.cnblogs.hoojo.config.RuntimeOptions;
import com.cnblogs.hoojo.core.analyzer.Analyzer;
import com.cnblogs.hoojo.core.analyzer.SimpleAnalyzer;
import com.cnblogs.hoojo.core.analyzer.executor.AnalyzerTaskExecutor;
import com.cnblogs.hoojo.core.analyzer.executor.AsyncAnalyzerTaskExecutor;
import com.cnblogs.hoojo.core.analyzer.executor.SyncAnalyzerTaskExecutor;
import com.cnblogs.hoojo.core.cache.FileSystemCacheManager;
import com.cnblogs.hoojo.core.cache.FileSystemCacheExecutor;
import com.cnblogs.hoojo.core.listening.ListeningDownloadManager;
import com.cnblogs.hoojo.core.listening.ListeningDownloadService;
import com.cnblogs.hoojo.enums.TaskState;
import com.cnblogs.hoojo.exception.EmptyContentException;

/**
 * <b>function:</b> 抽象爬取器
 * @author hoojo
 * @createDate 2017-3-2 下午11:12:44
 * @file AbstractSpider.java
 * @package com.cnblogs.hoojo.core.spider
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class AbstractSpider extends SimpleAnalyzer implements SpiderExecutor {
	
	private ListeningDownloadManager listeningDownloadManager;
	private AnalyzerTaskExecutor analyzerTaskExecutor;
	private FileSystemCacheManager cacheManager;
	private TaskState state = TaskState.WAIT;
	private Options options;
	
    private String site;
    private String spiderName;
    private String saveLocation;
    
    private String spiderURL;
    private int pageNo;

    public AbstractSpider(String spiderName, String spiderURL, Options spiderOptions) {
    	
    	this.site = spiderOptions.getSite();
    	this.spiderURL = spiderURL;
    	this.spiderName = spiderName;
    	this.saveLocation = spiderOptions.getSaveLocation();
    	this.pageNo = spiderOptions.getBeginPage();
    	afterParamterSet();
    	
    	this.options = new Options();
    	BeanUtils.copyProperties(spiderOptions, options);
    	afterOptionsSet();
    	
    	Thread.currentThread().setName(spiderNameAvailable(spiderName, 0));
    	createCacheManager();
    	createAnalyzerTaskExecutor();
    	createListeningDownloadService();
    }
    
	public AbstractSpider(String spiderName, String spiderURL) {
    	this(spiderName, null, spiderURL);
    }
    
    public AbstractSpider(String spiderName, String site, String spiderURL) {
    	this(spiderName, site, spiderURL, null);
    }
    
    public AbstractSpider(String spiderName, String site, String spiderURL, String saveLocation) {
    	this(site, spiderURL, spiderName, saveLocation, MIN_PAGE);
    }
    
    public AbstractSpider(String spiderName, String site, String spiderURL, String saveLocation, int beginPage) {
    	
    	this.site = site;
    	this.spiderURL = spiderURL;
    	this.spiderName = spiderName;
    	this.saveLocation = saveLocation;
    	this.pageNo = beginPage;
    	afterParamterSet();
    	
    	if (this.options == null) {
    		this.options = new Options(this.site, this.spiderURL, this.saveLocation, this.pageNo);
    	}
    	afterOptionsSet();
    	
    	Thread.currentThread().setName(spiderNameAvailable(spiderName, 0));
    	createAnalyzerTaskExecutor();
    	createListeningDownloadService();
    	createCacheManager();
    }
    
    private void createAnalyzerTaskExecutor() {
    	if (this.options.isAsync()) {
    		analyzerTaskExecutor = new AsyncAnalyzerTaskExecutor(this);
    	} else {
    		analyzerTaskExecutor = new SyncAnalyzerTaskExecutor(this);
    	}
    }
    
    private void createListeningDownloadService() {
    	listeningDownloadManager = new ListeningDownloadService(this);
	}
    
    private void createCacheManager() {
    	cacheManager = new FileSystemCacheExecutor(this.options.getSite(), spiderName);
    }
    
    private void afterParamterSet() throws RuntimeException {
    	try {
    		
    		if (StringUtils.isBlank(spiderURL)) {
    			throw new RuntimeException("spiderURL is empty.");
    		}
    		
    		if (!StringUtils.startsWith(spiderURL, "http")) {
    			this.spiderURL = "http://" + spiderURL;
    		} 
    		
    		if (StringUtils.isBlank(site)) {
    			this.site = StringUtils.substringBetween(spiderURL, "//", "/");
    		}
    		
    		if (StringUtils.isBlank(spiderName)) {
    			spiderName = this.site + "#" + this.getClass().getName();
    		}
    		
    		if (pageNo <= 0) {
    			this.pageNo = MIN_PAGE;
    		}
    		
    		if (StringUtils.isBlank(saveLocation)) {
    			this.saveLocation = DEFAULT_SAVE_LOCATION + File.separator;
    		}
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    		throw e;
    	}
    }
    
	private void afterOptionsSet() throws RuntimeException {
    	try {
    		
    		if (this.options.getPageNum() <= 0 || this.options.getPageNum() > PAGE_NUM) {
    			this.options.setPageNum(PAGE_NUM);
    		}
    		if (this.options.getMaxDownloadTaskNum() <= 0 || this.options.getMaxDownloadTaskNum() > MAX_DOWNLOAD_TASK_NUM) {
    			this.options.setMaxDownloadTaskNum(MAX_DOWNLOAD_TASK_NUM);
    		}
    		if (this.options.getMaxAnalyzerTaskNum() <= 0) {
    			this.options.setMaxAnalyzerTaskNum(MIN_ANALYZER_TASK_NUM);
    		}
    		if (this.options.getMaxAnalyzerTaskNum() > MAX_ANALYZER_TASK_NUM) {
    			this.options.setMaxAnalyzerTaskNum(MAX_ANALYZER_TASK_NUM);
    		}
    		if (this.options.getMaxSpiderWorksNum() < 1) {
    			this.options.setMaxSpiderWorksNum(Integer.MAX_VALUE);
    		}
    		if (this.options.getTimeout() <= 0) {
    			this.options.setTimeout(TIMEOUT);
    		}
    		if (this.options.getBeginPage() <= 0) {
    			this.options.setBeginPage(MIN_PAGE);
    			this.options.setCurrentPage(MIN_PAGE);
    		}
    		if (this.options.getMethod() == null) {
    			this.options.setMethod(METHOD);
    		}
    		if (this.options.getPathMode() == null) {
    			this.options.setPathMode(PATH_MODE);
    		}
    		if (this.options.getNamedMode() == null) {
    			this.options.setNamedMode(NAMED_MODE);
    		}
    		if (this.options.getFileNameMode() == null) {
    			this.options.setFileNameMode(FILE_NAME_MODE);
    		}
    		
    		this.options.setSite(this.site);
    		this.options.setSpiderURL(spiderURL);
    		this.options.setSaveLocation(saveLocation);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
    }
    
    private String spiderNameAvailable(String spiderName, int i) {
    	
    	if (spiderNames.containsKey(spiderName)) {
    		spiderName = spiderName + "-" + i;
    		i++;
    		
    		return spiderNameAvailable(spiderName, i);
    	} else {
    		spiderNames.put(spiderName, this);
    		return spiderName;
    	}
    }
    
    protected String executedPageNext() {
        
    	if (this.options.getPageNum() <= 0) {
    		log.info("当前页已是任务尾页，停止爬取 begenPage:{}, pageNum：{}, pageNo: {}", options.getBeginPage(), options.getPageNum(), pageNo);
    		return null;
    	}
    	this.options.setPageNum(this.options.getPageNum() - 1);
    	this.options.setCurrentPage(pageNo);

    	log.info("读取第 {} 页数据 ，开始页:{}, 还剩：{} 页", pageNo, options.getBeginPage(), options.getPageNum());

    	String executeURL = spiderURL + pageNo;
        pageNo++;
        
        return executeURL;
    }
    
    @Override
    public final void execute() {
    	
    	if (this.options.isAsync()) {
    		
    		new Thread(new Runnable() {
				public void run() {
					doInvoke();
				}
			}, this.spiderName).start();
    		
    		log.debug("已创建任务：{}", spiderNames);
		} else {
			doInvoke();
		}
    };
    
    private void doInvoke() {
    	
    	if (state != TaskState.WAIT) {
    		throw new RuntimeException("程序已启动运行，不能重复执行。");
    	}
    	state = TaskState.START;
    	
    	boolean cached = cacheManager.execute();
    	if (cached) {
    		log.info("缓存处理完成，开始提取图文数据：{}", cached);
    	}
    	
    	String url = null;
    	while ((url = executedPageNext()) != null) {
    		state = TaskState.RUN;
    		
    		log.info("开始提取网页数据：{}", url);
    		
    		try {
    			analyzerTaskExecutor.doTask(url);
			} catch (EmptyContentException e) { 
				break;
			} catch (Exception e) {
				log.error("执行爬取数据方法异常：", e);
			}
    		
    		log.info("开始启动网页图片下载任务");
    		try {
				listeningDownloadManager.start();
			} catch (Exception e) {
				log.error("执行下载数据方法异常：", e);
			}
    	}
    	analyzerTaskExecutor.shutdownTask();
    	
    	if (analyzerTaskExecutor.isFinish()) {
    		state = TaskState.FINISH;
    	}
    }
    
    @Override
    public final TaskState getSpiderState() {
    	return state;
    }
    
    @Override
    public final RuntimeOptions getOptions() {
    	return new RuntimeOptions(this.options);
    }
	
	@Override
	public final String getSpiderName() {
    	return this.spiderName;
    }
	
	@Override
	public final String getThreadName() {
	    return Thread.currentThread().getName();
	}
	
	@Override
	public Analyzer getAnalyzer() {
	    return this;
	}
	
	@Override
	public ListeningDownloadManager getListeningDownloadManager() {
	    return this.listeningDownloadManager;
	}
	
	@Override
	public FileSystemCacheManager getCacheManager() {
	    return this.cacheManager;
	}
}
