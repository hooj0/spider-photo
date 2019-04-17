package com.cnblogs.hoojo.core;

/**
 * <b>function:</b> 全局配置
 * @author hoojo
 * @createDate 2017-6-20 下午3:46:13
 * @file GlobalConst.java
 * @package com.cnblogs.hoojo.core
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract interface GlobalConst {

	class AnalyzerTaskThreadPoolConst {
		
		/** 创建分析线程池 轮询时间 */
		public static int CREATE_POOL_IDLE_MINUTES = 1000;
		
		/** 关闭分析线程池 轮询时间 */
		public static int SHUTDOWN_POOL_IDLE_MINUTES = 1000;
	}
	
	class AsyncAnalyzerTaskExecutorConst {
		
		/** 关闭分析图文线程池 轮询时间 */
		public static int SHUTDOWN_POOL_IDLE_MINUTES = 1000;
	}
	
	class TemporaryCacheServiceConst {
		
		/** 线程池固定数量 */
		public static int FIXED_THREAD_POOL_NUM = 3;
		
		/** 临时缓存读取线程池 轮询时间 */
		public static int SHUTDOWN_POOL_IDLE_MINUTES = 1000;
	}
	
	class OptimizerCacheServiceConst {
		
		/** 线程池固定数量 */
		public static int FIXED_THREAD_POOL_NUM = 3;
		
		/** 关闭临时缓存读取线程池 轮询时间 */
		public static int SHUTDOWN_POOL_IDLE_MINUTES = 1000;
		
		/** 缓存队列轮询时间 */
		public static int CACHE_QUQUE_IDLE_MINUTES = 1000;
	}
	
	class ListeningDownloadServiceConst {
		
		/** 同时下载图文 */
		public static int MAX_DOWNLOAD_WORKS_NUM = 5;

		/** 失败下载重试 轮询时间 */
		public static int RETRY_DOWNLOAD_QUQUE_IDLE_MINUTES = 1000 * 5;
		
		/** 超过最大同时下载图文 轮询时间 */
		public static int EXCEED_MAX_DOWNLOAD_WORKS_IDLE_MINUTES = 1000 * 15;

		/** 下载图文情况 轮询时间 */
		public static int WORKS_INFO_QUQUE_IDLE_MINUTES = 1000 * 45;

		/** 判断图文下载是否完成 */
		public static int WORKS_DOWNLOAD_FINISH_IDLE_MINUTES = 1000 * 15;
		
		/** 超过最大同时下载任务数量 轮询时间 */
		public static int EXCEED_MAX_DOWNLOAD_TASK_NUM_IDLE_MINUTES = 1000 * 3;
		
		/** 输出未完成下载图文 轮询时间 */
		public static int WORKS_DOWNLOAD_UNFINISH_IDLE_MINUTES = 1000 * 15;
		
		/** 下载线程池关闭 轮询时间 */
		public static int SHUTDOWN_POOL_IDLE_MINUTES = 1000 * 2;
		
		/** 严格限定下载任务数量模式 */
		public static boolean STRICT_LIMIT_DOWNLOAD_TASK_NUM_MODE = true;
	}
	
	class DownloadFileTaskConst {
		
		/** 最大下载失败数量 */
		public static int MAX_FAILURE_DOWNLOAD_NUM = 5;

		/** 失败下载重试 轮询时间 */
		public static int RETRY_DOWNLOAD_QUQUE_IDLE_MINUTES = 1000 * 3;

		/** 下载完成后休眠间隔 */
		public static int DOWNLOAD_COMPLETED_SLEEP_MINUTES = 500;

		/** 写入文件到硬盘 休眠间隔 */
		public static int WRITE_DISK_FILE_SLEEP_MINUTES = 1;

		/** 每次读取多少字节 */
		public static int READ_FILE_BUFFER_LENGTH = 1024;
	}
}
