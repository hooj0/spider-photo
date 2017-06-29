package com.cnblogs.hoojo.core.analyzer.executor;

import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.analyzer.Analyzer;
import com.cnblogs.hoojo.core.analyzer.pool.AnalyzerTaskThreadPool;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;
import com.google.common.collect.Sets;

/**
 * <b>function:</b> 异步执行器
 * 
 * @author hoojo
 * @createDate 2017-3-4 下午5:27:39
 * @file AsyncAnalyzerTaskExecutor.java
 * @package com.cnblogs.hoojo.core.analyzer.executor
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class AsyncAnalyzerTaskExecutor extends ApplicationLogging implements AnalyzerTaskExecutor {

	private Set<String> worksLinks;

	private int spiderWorksNum = 0;
	private String threadName;
	private Analyzer analyzerTask;
	private ExecutorService analyzerTaskPool;

	public AsyncAnalyzerTaskExecutor(Analyzer analyzerTask) {
		this.threadName = analyzerTask.getThreadName();
		this.analyzerTask = analyzerTask;

		this.analyzerTaskPool = Executors.newFixedThreadPool(analyzerTask.getOptions().getMaxAnalyzerTaskNum());
		log.info("创建线程池：{} 个线程", analyzerTask.getOptions().getMaxAnalyzerTaskNum());

		if (!SpiderExecutor.SPIDER_WORKS_LINKS.containsKey(analyzerTask.getOptions().getSite())) {
			this.worksLinks = Sets.newHashSet();
			SpiderExecutor.SPIDER_WORKS_LINKS.put(analyzerTask.getOptions().getSite(), this.worksLinks);
		} else {
			this.worksLinks = SpiderExecutor.SPIDER_WORKS_LINKS.get(analyzerTask.getOptions().getSite());
		}
	}

	@Override
	public void doTask(final String url) {

		analyzerTaskPool.submit(new Runnable() {
			@Override
			public void run() {

				String name = Thread.currentThread().getName();
				if (StringUtils.contains(name, threadName + "$")) {
					name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "$");
				}
				Thread.currentThread().setName(threadName + "$" + name);

				log.debug("----------开始爬取作品数据：{}---------", url);
				try {
					WorksQueue queue = analyzerTask.analyzer(url);
					if (queue != null) {

						Iterator<Works> iter = queue.iterator();
						if (iter.hasNext()) {
							newTask(iter, queue.size());
						}
					}
				} catch (SocketTimeoutException e) {
					log.info("请求URL超时，系统自动重试：{}", url);
					doTask(url);
				} catch (Exception e) {
					log.error("分析页面URL：{}，异常", url, e);
				}
				log.debug("----------结束爬取作品数据：{}---------", url);
			}
		});
	}

	private void doTask(final String link, final Works works, final ExecutorService analyzerTaskPool) {

		analyzerTaskPool.submit(new Runnable() {
			@Override
			public void run() {

				String name = Thread.currentThread().getName();
				if (StringUtils.contains(name, threadName + "$")) {
					name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "$");
				}
				Thread.currentThread().setName(threadName + "$" + name);

				log.debug("----------开始爬取图片数据：{}---------", link);
				try {
					List<String> photos = analyzerTask.analyzer(works.getLink(), works);
					if (photos != null && !photos.isEmpty()) {
						works.setResources(photos);
					}

					analyzerTask.getDataHolder().getSpiderWaiting().add(works);
				} catch (SocketTimeoutException e) {
					log.info("请求URL超时，系统自动重试：{}", link);
					
					doTask(link, works, analyzerTaskPool);
				} catch (Exception e) {
					log.error("分析作品集URL：{}，异常", works.getLink(), e);
				}
				log.debug("----------结束爬取图片数据：{}---------", link);

				log.debug("添加图片一篇：{}", works);
			}
		});
	}

	private void newTask(Iterator<Works> iter, int size) {

		AnalyzerTaskThreadPool taskPool = new AnalyzerTaskThreadPool(size);

		while (iter.hasNext()) {
			Works works = iter.next();

			if (StringUtils.isBlank(works.getLink())) {
				continue;
			}
			if (!StringUtils.isBlank(analyzerTask.getOptions().getMatch()) && !StringUtils.contains(works.getLink(), analyzerTask.getOptions().getMatch())) {
				continue;
			}
			
			if (spiderWorksNum >= analyzerTask.getOptions().getMaxSpiderWorksNum()) {
				log.info("已超过最大图文数量：{}/{}，可以跳过后面任务", spiderWorksNum, analyzerTask.getOptions().getMaxSpiderWorksNum());
				break;
			}
			
			if (!worksLinks.contains(works.getLink())) {
				worksLinks.add(works.getLink());
				spiderWorksNum++;
				
				doTask(works.getLink(), works, taskPool);
			} else {
				log.info("该图文已被抓取，无需重复抓取：{}", works.getLink());
			}
		}
		taskPool.shutdown();
	}

	public boolean isFinish() {
		while (true) {
			if (analyzerTaskPool.isTerminated()) {
				return true;
			}

			try {
				Thread.sleep(GlobalConst.AsyncAnalyzerTaskExecutorConst.SHUTDOWN_POOL_IDLE_MINUTES);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void shutdownTask() {
		if (analyzerTaskPool != null) {
			analyzerTaskPool.shutdown();
		}
	}
}
