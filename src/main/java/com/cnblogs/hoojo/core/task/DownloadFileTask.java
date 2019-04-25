package com.cnblogs.hoojo.core.task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.GlobalConst;
import com.cnblogs.hoojo.core.holder.AnalyzerDataHolder;
import com.cnblogs.hoojo.core.holder.DownloadDataHolder;
import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.model.DownloadInfo;
import com.cnblogs.hoojo.model.Works;
import com.cnblogs.hoojo.util.MathUtils;

/**
 * <b>function:</b> 下载文件任务
 * @author hoojo
 * @createDate 2017-4-21 下午3:47:47
 * @file DownloadFileTask.java
 * @package com.cnblogs.hoojo.core.task
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class DownloadFileTask extends ApplicationLogging implements Runnable {

    public static volatile int TASK_NUM = 0;

    private SpiderExecutor spiderExecutor;
    private AnalyzerDataHolder analyzerDataHolder;
    private DownloadDataHolder downloadDataHolder;
    
    private boolean overwrite = false;
    private DownloadInfo downloadInfo;
    private String targetURL;
    private String savePath;

    public DownloadFileTask(DownloadInfo downloadInfo, SpiderExecutor executor) {
    	this.spiderExecutor = executor;
    	
        this.targetURL = downloadInfo.getUrl();
        this.savePath = downloadInfo.getSavePath();
        this.overwrite = executor.getOptions().isOverride();
        this.downloadInfo = downloadInfo;
        
        this.downloadDataHolder = executor.getListeningDownloadManager().getDataHolder();
        this.analyzerDataHolder = executor.getAnalyzer().getDataHolder();
        
        TASK_NUM++;
    }

	public void run() {
    	
    	try {
    		
    		String threadName = spiderExecutor.getSpiderName() + ".下载任务-" + downloadInfo.getWorksTitle();
    		String name = Thread.currentThread().getName();
			if (StringUtils.contains(name, "#")) {
				name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "#");;
			}
			Thread.currentThread().setName(threadName + "#" + name);
    		
            boolean successed = download(), isCompleted = false;
            if (successed) {
            	downloadDataHolder.getDownloading().remove(downloadInfo);
                downloadInfo.setEndTime(new Date());
                downloadDataHolder.getDownloadCompleted().add(downloadInfo);
                
                isCompleted = true;
            } else {
            	
            	if (downloadInfo.getRetryCount() >= GlobalConst.DownloadFileTaskConst.MAX_FAILURE_DOWNLOAD_NUM) {
            		log.error("重试下载{}次无法下载文件：{}", downloadInfo.getRetryCount(), downloadInfo);
            		
            		downloadDataHolder.getDownloading().remove(downloadInfo);
            		downloadInfo.setEndTime(new Date());
            		downloadDataHolder.getDownloadErrored().add(downloadInfo);
            		
            		isCompleted = true;
            		
            		TASK_NUM--;
            	} else {
            		downloadInfo.retryDownload();
            		
            		// 重新下载
            		System.out.println("文件下载失败，重新下载：" + downloadInfo);
            		Thread.sleep(GlobalConst.DownloadFileTaskConst.RETRY_DOWNLOAD_QUQUE_IDLE_MINUTES);
            		
            		this.overwrite = true;
            		run();
            	}
            }
            
            if (isCompleted) {
            	for (Works works : analyzerDataHolder.getSpidering()) {
                	
                	if (StringUtils.equals(works.getId(), downloadInfo.getRefId())) {
                		if (works.isFinish()) {
                			analyzerDataHolder.getSpidering().remove(works);
                			analyzerDataHolder.getSpiderCompleted().add(works);
                			
                			spiderExecutor.getCacheManager().getCacheWriter().push(works.getLink());
                		} else {
                			works.nextDownload();
                		}
                		log.info("图文：" + works.getTitle() + ", 已下载图片：" + works.getDownloadCompletedNum() + ", 总图片：" + works.getResources().size());
                	}
                }
            	
            	if (GlobalConst.DownloadFileTaskConst.DOWNLOAD_COMPLETED_SLEEP_MINUTES > 0) {
            		Thread.sleep(GlobalConst.DownloadFileTaskConst.DOWNLOAD_COMPLETED_SLEEP_MINUTES);
            	}
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } 
    }

    private synchronized boolean download() {

        long timed = System.currentTimeMillis();
        
        File file = null;
        InputStream is = null;
        OutputStream fos =  null;
        try {
        	
        	if (StringUtils.isBlank(targetURL)) {
        		downloadInfo.setUsedTime(MathUtils.formatTime(new Date().getTime() - timed));
            	log.info("文件路径为空，不进行下载：{}", downloadInfo);
            	
            	TASK_NUM--;
            	return true;
        	}
        	
            File dir = new File(this.savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 网络文件的URL
            URL url = new URL(targetURL);
            // 打开该网络文件的URL连接
            URLConnection connec = url.openConnection();
            connec.setConnectTimeout(new Long(spiderExecutor.getOptions().getTimeout()).intValue());
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
 			connec.setDoOutput(true);
 			// 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
 			connec.setDoInput(true);
 			// 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
 			connec.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
 			connec.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            
            downloadInfo.setSize(connec.getContentLength());
            downloadInfo.setType(connec.getContentType());
            
            file = new File(this.savePath + File.separator + downloadInfo.getFileName());
            
            File folder = file.getParentFile();
        	if (folder.exists() && folder.listFiles().length >= 50) {
        		file = new File(createFolder(folder), downloadInfo.getFileName());
        	}
            
            if (file.exists() && !overwrite) {
            	
            	if (connec.getContentLength() == file.length()) {
            		downloadInfo.setUsedTime(MathUtils.formatTime(new Date().getTime() - timed));
	            	log.info("文件已经存在，不进行下载：{}", targetURL);
	            	
	            	TASK_NUM--;
	            	return true;
            	} else {
            		log.info("文件已经存在，但大小不等：{}({})，重新下载：{}", file.length(), connec.getContentLength(), targetURL);
            	}
            } else {
            	
            	StringBuilder taskInfo = new StringBuilder();
            	taskInfo.append("\n");
            	taskInfo.append("即将下载资源：").append(targetURL).append("\n");
            	taskInfo.append("创建新文件：").append(file.getAbsolutePath()).append("\n");
            	taskInfo.append("当前下载任务数量：").append(TASK_NUM).append("\n");
            	log.info(taskInfo);
            }
            
			// 网络文件的相关信息
        	StringBuffer info = new StringBuffer();
        	
            info.append("主机：" + url.getHost() + "\n");
            info.append("端口：" + url.getDefaultPort() + "\n");
            info.append("网络文件的类型：" + connec.getContentType() + "\n");
            info.append("长度：" + connec.getContentLength() + "\n");
            info.append("正在下载:" + file.getAbsolutePath());
            
            // 创建网络文件的输入流
            is = new BufferedInputStream(connec.getInputStream());
            // 创建本地文件输出流
            fos = new BufferedOutputStream(new FileOutputStream(file));

            // 读取网络文件到本地文件
            byte[] buff = new byte[GlobalConst.DownloadFileTaskConst.READ_FILE_BUFFER_LENGTH];
            int length;
            while ((length = is.read(buff)) != -1) {
                fos.write(buff, 0, length);
                
                if (GlobalConst.DownloadFileTaskConst.WRITE_DISK_FILE_SLEEP_MINUTES > 0) {
            		Thread.sleep(GlobalConst.DownloadFileTaskConst.WRITE_DISK_FILE_SLEEP_MINUTES);
            	}
            }
        } catch (Exception e) {
            log.error("下载异常：{}", downloadInfo, e);
            downloadInfo.setUsedTime(MathUtils.formatTime(new Date().getTime() - timed));
            
            //TASK_NUM--;
            return false;
        } finally {
        	// 关闭流
        	try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
        }
        
        file = new File(file.getAbsolutePath());
        if (downloadInfo.getSize() != file.length()) {
        	log.error(String.format("下载大小(%s)和实际文件大小(%s)不符", file.length(), downloadInfo.getSize()));
        	return false;
        }
        
        downloadInfo.setUsedTime(MathUtils.formatTime(new Date().getTime() - timed));
        TASK_NUM--;
        
        log.info("图片下载完毕：{}", targetURL);
        return true;
    }
    
    private File createFolder(File folder) {
    	
    	if (folder.isDirectory()) {
    		if (folder.exists()) {
    			
    			File[] folders = folder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							return true;
						}
						
						return false;
					}
				});
    			
    			for (File dir : folders) {
    				if (dir.listFiles().length < 50) {
    					return dir;
    				}
    			}
    			
    			folder = new File(folder, folder.getName() + "_" + (folders.length + 1));
    			folder.mkdirs();
    			
    			return folder;
    		} else {
    			folder.mkdirs();
    			
    			return folder;
    		}
    	}
    	
    	throw new RuntimeException(folder.getAbsolutePath() + " 不是一个文件夹");
    }
}
