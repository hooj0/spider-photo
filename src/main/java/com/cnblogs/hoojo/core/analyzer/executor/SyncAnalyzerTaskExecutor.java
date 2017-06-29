package com.cnblogs.hoojo.core.analyzer.executor;

import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.analyzer.Analyzer;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.queue.WorksQueue;
import com.google.common.collect.Sets;

/**
 * <b>function:</b> 同步执行器
 * 
 * @author hoojo
 * @createDate 2017-3-4 下午5:25:57
 * @file SyncAnalyzerTaskExecutor.java
 * @package com.cnblogs.hoojo.core.analyzer.executor
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SyncAnalyzerTaskExecutor extends ApplicationLogging implements AnalyzerTaskExecutor {

	private Analyzer analyzerTask;
	private Set<String> worksLinks;

	public SyncAnalyzerTaskExecutor(Analyzer analyzerTask) {
		this.analyzerTask = analyzerTask;

		if (!SpiderExecutor.SPIDER_WORKS_LINKS.containsKey(analyzerTask.getOptions().getSite())) {
			this.worksLinks = Sets.newHashSet();
			SpiderExecutor.SPIDER_WORKS_LINKS.put(analyzerTask.getOptions().getSite(), this.worksLinks);
		} else {
			this.worksLinks = SpiderExecutor.SPIDER_WORKS_LINKS.get(analyzerTask.getOptions().getSite());
		}
		
		Thread.currentThread().setName(analyzerTask.getThreadName());
	}

	@Override
	public void doTask(String url) {

		try {
			WorksQueue queue = analyzerTask.analyzer(url);
			
			if (queue != null) {
				Iterator<Works> iter = queue.iterator();
				while (iter.hasNext()) {
					Works works = iter.next();

					if (!StringUtils.isBlank(analyzerTask.getOptions().getMatch()) && !StringUtils.contains(works.getLink(), analyzerTask.getOptions().getMatch())) {
						continue;
					}
					
					System.out.println(works);
					doTask(works.getLink(), works);
					System.out.println(works);
				}
			}
		} catch (SocketTimeoutException e) {
			log.info("请求URL超时，系统自动重试：{}", url);
			
			doTask(url);
		} catch (Exception e) {
			log.error("分析页面URL：{}，异常", url, e);
		}
	}

	private void doTask(String link, Works works) {
		try {
			if (StringUtils.isBlank(link)) {
				return;
			}
			
			if (!worksLinks.contains(works.getLink())) {
				worksLinks.add(works.getLink());

				works.setResources(analyzerTask.analyzer(link, works));
				analyzerTask.getDataHolder().getSpiderWaiting().add(works);
			} else {
				System.out.println("该图文已被抓取，无需重复抓取：" + works.getLink());
			}
		} catch (SocketTimeoutException e) {
			log.info("请求URL超时，系统自动重试：{}", link);
			
			doTask(link, works);
		} catch (Exception e) {
			log.error("分析作品集URL：{}，异常", works.getLink(), e);
		}

		log.debug("最终作品集：{}", works);
	}

	@Override
	public void shutdownTask() {
	}

	@Override
	public boolean isFinish() {
		return true;
	}
}
