package com.cnblogs.hoojo.core.listening;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.config.RuntimeOptions;
import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.holder.AnalyzerDataHolder;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.core.task.DescriptionFileTask;
import com.cnblogs.hoojo.core.task.DownloadFileTask;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.TaskState;
import com.cnblogs.hoojo.model.DownloadInfo;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.util.FilePathNameUtils;

/**
 * <b>function:</b> 下载监听服务
 * 
 * @author hoojo
 * @createDate 2017-3-4 上午11:09:23
 * @file ListeningDownloadTaskService.java
 * @package com.cnblogs.hoojo.core.listening
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class ListeningDownloadService extends ListeningDownloadWrapper {

	private TaskState state = TaskState.WAIT;
	private RuntimeOptions options;
	private ExecutorService downloadPool;
	private AnalyzerDataHolder analyzerDataHolder;
	private SpiderExecutor executor;

	private ListeningDownloadWrapper wrapper = this;
	
	public ListeningDownloadService(SpiderExecutor executor) {
		this.executor = executor;
		this.options = executor.getOptions();
		this.downloadPool = Executors.newFixedThreadPool(options.getMaxDownloadTaskNum());
		
		this.analyzerDataHolder = executor.getAnalyzer().getDataHolder();
	}

	@Override
	public void start() throws Exception {

		if (state != TaskState.WAIT) {
			log.trace("下载任务程序已启动运行，不能重复执行");
			return;
		}
		
		state = TaskState.START;
		new Thread(new Runnable() {

			@Override
			public void run() {
				state = TaskState.RUN;
				
				printDownloadWorksInfoTask();
				downloadWorksResourcesTask();
				
				unFinishDownloadTask();
				retryDownloadErrorTask();
				
				if (!downloadPool.isShutdown()) {
					downloadPool.shutdown();
				}
				if (isFinish()) {
					state = TaskState.FINISH;
				}
				
				try {
	                executor.getCacheManager().getCacheWriter().close();
                } catch (Exception e) {
	                log.error("关闭写入临时缓存异常", e);
                }
				log.info("全部完成，任务结束：{}", state);
			}
		}, executor.getThreadName() + "-下载任务").start();
	}

	private void downloadWorksResourcesTask() {
		
		int count = 0;
		while (true) {

			if (!analyzerDataHolder.getSpiderWaiting().isEmpty()) {
				
				state = TaskState.RUN;
				count = 0;

				Works works = analyzerDataHolder.getSpiderWaiting().remove();
				if (works != null) {
					analyzerDataHolder.getSpidering().add(works);

					try {
						String dir = FilePathNameUtils.conversionPathName(options, works);
						String savePath = FilePathNameUtils.cleanParam(options.getSaveLocation() + File.separatorChar + dir);
						String name = FilePathNameUtils.conversionNamed(options.getFileNameMode(), works);

						downloadAttachment(works, savePath);
						for (String imageURL : works.getResources()) {

							String fileName = StringUtils.substringAfterLast(imageURL, "/");
							if (!StringUtils.isBlank(name)) {
								fileName = name + "_" + fileName;
							}
							submitDownloadTask(works, imageURL, fileName, savePath);
							
							int skip = 0;
							skip = sleepDownloadTask(skip);
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
					
					while (analyzerDataHolder.getSpidering().size() > GlobalConst.ListeningDownloadServiceConst.MAX_DOWNLOAD_WORKS_NUM) {
						try {
							Thread.sleep(GlobalConst.ListeningDownloadServiceConst.EXCEED_MAX_DOWNLOAD_WORKS_IDLE_MINUTES);
						} catch (InterruptedException e) {
							log.error(e);
						}
					}
				}
				
			} else {
				state = TaskState.PAUSE;
				
				try {
					Thread.sleep(GlobalConst.ListeningDownloadServiceConst.WORKS_DOWNLOAD_FINISH_IDLE_MINUTES);
					count++;
				} catch (InterruptedException e) {
					log.error(e);
				}
				
				if (count > 10 && executor.getSpiderState() == TaskState.FINISH) {
					log.info("未有发现新任务，下载程序自动退出……");
					break;
				}
			}
		}
	}
	
	private void printDownloadWorksInfoTask() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (state != TaskState.FINISH) {
					log.info(String.format("等待下载图文：%s，正在下载图文：%s，下载完成图文：%s", analyzerDataHolder.getSpiderWaiting().size(), analyzerDataHolder.getSpidering().size(), analyzerDataHolder.getSpiderCompleted().size()));
					
					try {
	                    Thread.sleep(GlobalConst.ListeningDownloadServiceConst.WORKS_INFO_QUQUE_IDLE_MINUTES);
                    } catch (InterruptedException e) {
                    	log.error(e);
                    }
                }
			}
		}, executor.getSpiderName() + "-监控下载信息").start();
	}
	
	private int sleepDownloadTask(int skip) {
		
		while (DownloadFileTask.TASK_NUM > options.getMaxDownloadTaskNum() + spareNum(options.getMaxDownloadTaskNum())) {
			if (skip > 5) {
				log.info(String.format("启动下载数：%s（%s），最大下载数：%s，线程数过多，等待中！", DownloadFileTask.TASK_NUM, downloading.size(), options.getMaxDownloadTaskNum()));
				skip = 0;
			}
            
            try {
                Thread.sleep(GlobalConst.ListeningDownloadServiceConst.EXCEED_MAX_DOWNLOAD_TASK_NUM_IDLE_MINUTES);
            } catch (InterruptedException e) {
            	log.error(e);
            }
            skip++;
        }
		return skip;
	}
	
	private void unFinishDownloadTask() {
		
		int retryCount = 0;
		while (true) {
			if (wrapper.getDownloading().isEmpty()) {
				log.info("下载任务全部完成: Global-Num({})/Downloading-Size({})", DownloadFileTask.TASK_NUM, wrapper.getDownloading().size());
				break;
			}
			
			System.out.println("\r\r------------还未完成下载文件：" + wrapper.getDownloading().size() + "个------------");
			System.out.println("------------未完成下载文件列表------------");
			for (DownloadInfo downloadInfo : wrapper.getDownloading()) {
				System.out.println("未完成下载：" + downloadInfo);
			}
			System.out.println("\r");
			
			try {
				Thread.sleep(GlobalConst.ListeningDownloadServiceConst.WORKS_DOWNLOAD_UNFINISH_IDLE_MINUTES);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
			
			if (retryCount >= 15) {
				wrapper.getDownloadErrored().addAll(wrapper.getDownloading());
				wrapper.getDownloading().clear();
			}
			
			retryCount++;
		}
	}
	
	private void retryDownloadErrorTask() {
		if (!this.getDownloadErrored().isEmpty()) {
			log.info("发现下载失败文件 {} 个进行重试下载", this.getDownloadErrored().size());
		}
		
		try {
			while (!this.getDownloadErrored().isEmpty()) {
				
				DownloadInfo failureDownload = this.getDownloadErrored().remove();
				System.out.println("下载失败文件：" + failureDownload);
				
				downloading.add(failureDownload);
				downloadPool.submit(new DownloadFileTask(failureDownload, executor));
				
				int skip = 0;
				skip = sleepDownloadTask(skip);
			}
			
        } catch (Exception e) {
	        log.error("重试下载失败文件任务异常", e);
        }
	}
	
	private void submitDownloadTask(Works works, String downloadURL, String fileName, String savePath) {
		
		if (!StringUtils.isBlank(downloadURL)) {
			
			try {
				fileName = FilePathNameUtils.cleanParam(fileName);
				
				DownloadInfo downloadInfo = new DownloadInfo(downloadURL, fileName, savePath);
				downloadInfo.setName(fileName);
				downloadInfo.setBeginTime(new Date());
				downloadInfo.setRefId(works.getId());
				downloadInfo.setWorksTitle(works.getTitle());
				downloading.add(downloadInfo);
				
				downloadPool.submit(new DownloadFileTask(downloadInfo, executor));
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	private void downloadSmallPic(Works works, String picURL, String fileName, String savePath) {
		
		try {
			String name = FilePathNameUtils.conversionNamed(NamedMode.SITE_TITLE_AUTHOR_DATE_ATTRACT, works);
			if (!StringUtils.isBlank(name)) {
				fileName = fileName + "_" + name;
			}
			
			String suffix = StringUtils.substringAfterLast(picURL, ".");
			fileName = fileName + "." + suffix;
			fileName = FilePathNameUtils.cleanParam(fileName);
			
			submitDownloadTask(works, picURL, fileName, savePath);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void saveDescriptionFile(Works works, String fileName, String savePath) {
		
		try {
			new Thread(new DescriptionFileTask(works, savePath, fileName, executor)).start();
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void cleanSmallPic(String savePath) {
		File dir = new File(savePath);
		
		if (dir.exists()) {
			for (File smallPic : dir.listFiles()) {
				if (StringUtils.startsWith(smallPic.getName(), "_封面") || StringUtils.startsWith(smallPic.getName(), "__头像")) {
					smallPic.delete();
				}
			}
		}
	}
	
	private void downloadAttachment(Works works, String savePath) {
		
		this.cleanSmallPic(savePath);
		this.downloadSmallPic(works, works.getCover(), "_封面", savePath);
		this.downloadSmallPic(works, works.getAvatar(), "__头像", savePath);
		this.saveDescriptionFile(works, "__Description.txt", savePath);
	}
	
	private int spareNum(int size) {
		
		if (GlobalConst.ListeningDownloadServiceConst.STRICT_LIMIT_DOWNLOAD_TASK_NUM_MODE){
			return 0;
		}
		
		Double count = size * 0.2;
		int createThreadCount = Math.max(count.intValue(), 1);
		
		//createThreadCount = Math.min(createThreadCount, Math.max(options.getMaxDownloadTaskNum() - DownloadFileTask.TASK_NUM, 2));
		return createThreadCount;
	}

	@Override
	public boolean isFinish() {

		while (true) {
			if (downloadPool.isTerminated()) {
				return true;
			}

			try {
				Thread.sleep(GlobalConst.ListeningDownloadServiceConst.SHUTDOWN_POOL_IDLE_MINUTES);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	@Override
	public TaskState getDownloadState() {
		return state;
	}
}
