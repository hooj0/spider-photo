package com.cnblogs.hoojo.core.analyzer.pool;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.analyzer.Analyzer;

/**
 * <b>function:</b> 分析任务线程池
 * 
 * @author hoojo
 * @createDate 2017-3-7 下午10:19:52
 * @file AnalyzerTaskThreadPool.java
 * @package com.cnblogs.hoojo.core.analyzer.pool
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class AnalyzerTaskThreadPool extends AbstractExecutorService {

	public static volatile int THREAD_NUM = 0;
	public static volatile int POOL_NUM = 0;
	
	private int taskThreadCount;
	private ThreadPoolExecutor threadPoolExecutor;

	public AnalyzerTaskThreadPool(Integer size) {

		int createThreadCount = threadCount(size);
		while (AnalyzerTaskThreadPool.POOL_NUM > Analyzer.MAX_ANALYZER_POOL_NUM || (AnalyzerTaskThreadPool.THREAD_NUM + createThreadCount) > Analyzer.MAX_ANALYZER_POOL_THREAD_NUM) {
			System.out.println("当前线程池过多，线程池数：" + AnalyzerTaskThreadPool.POOL_NUM + "，线程数：" + AnalyzerTaskThreadPool.THREAD_NUM);

			try {
				Thread.sleep(GlobalConst.AnalyzerTaskThreadPoolConst.CREATE_POOL_IDLE_MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			createThreadCount = threadCount(size);
		}
		
		this.threadPoolExecutor = new ThreadPoolExecutor(createThreadCount, createThreadCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		this.taskThreadCount = createThreadCount;

		POOL_NUM++;
		THREAD_NUM += taskThreadCount;

		System.out.println("创建新线程：" + taskThreadCount);
		System.out.println("创建->线程池数：" + POOL_NUM + "，线程数：" + THREAD_NUM);
	}

	@Override
	public void shutdown() {
		threadPoolExecutor.shutdown();

		while (true) {
			if (this.isTerminated()) {

				POOL_NUM--;
				THREAD_NUM -= taskThreadCount;

				System.out.println("关闭新线程：" + taskThreadCount);
				System.out.println("关闭->线程池数：" + POOL_NUM + "，线程数：" + THREAD_NUM);
				break;
			}

			try {
				Thread.sleep(GlobalConst.AnalyzerTaskThreadPoolConst.SHUTDOWN_POOL_IDLE_MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int threadCount(int size) {
		
		Double count = size * 0.2;
		int createThreadCount = Math.max(count.intValue(), 1);
		
		createThreadCount = Math.min(createThreadCount, Math.max(Analyzer.MAX_ANALYZER_POOL_THREAD_NUM - AnalyzerTaskThreadPool.THREAD_NUM, 2));
		System.out.println("实际创建线程：" + createThreadCount);
		
		return createThreadCount;
	}

	@Override
	public List<Runnable> shutdownNow() {
		return threadPoolExecutor.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return threadPoolExecutor.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return threadPoolExecutor.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return threadPoolExecutor.awaitTermination(timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		threadPoolExecutor.execute(command);
	}
}
