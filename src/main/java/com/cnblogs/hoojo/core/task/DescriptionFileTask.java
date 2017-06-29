package com.cnblogs.hoojo.core.task;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

import com.cnblogs.hoojo.core.spider.SpiderExecutor;
import com.cnblogs.hoojo.log.ApplicationLogging;
import com.cnblogs.hoojo.model.Works;

/**
 * <b>function:</b> 文件描述内容存档
 * @author hoojo
 * @createDate 2017-4-30 下午5:10:48
 * @file DescriptionFileTask.java
 * @package com.cnblogs.hoojo.core.task
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class DescriptionFileTask extends ApplicationLogging implements Runnable {

	public static volatile int TASK_NUM = 0;

    private SpiderExecutor spiderExecutor;
    private Works works;
    private boolean overwrite = false;
    private String savePath;
    private String fileName;

    public DescriptionFileTask(Works works, String savePath, String fileName, SpiderExecutor executor) {
    	this.spiderExecutor = executor;
    	this.overwrite = executor.getOptions().isOverride();
    	
    	this.savePath = savePath;
        this.fileName = fileName;
        this.works = works;
        
        TASK_NUM++;
    }
    
	@Override
    public void run() {
		
		try {
    		String threadName = spiderExecutor.getSpiderName() + ".存档任务-" + works.getTitle();
    		String name = Thread.currentThread().getName();
			if (StringUtils.contains(name, "#")) {
				name = StringUtils.substringAfterLast(Thread.currentThread().getName(), "#");;
			}
			Thread.currentThread().setName(threadName + "#" + name);
    		
			boolean successed = saveDescriptionFile();
			if (successed) {
			} else {
				System.out.println("图文存档失败：" + works.getTitle());
			}
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } 
    }
	
	private void printDescription(PrintStream print) {
		// 读取网络文件到本地文件
        print.printf("◆ 作者基本信息 \r");
        print.println();
        print.printf("作者：%s \r", works.getAuthor());
        print.println();
        print.printf("作者头像：%s \r", works.getAvatar());
        print.println();
        print.printf("作者博客：%s \r", works.getBlog());
        print.println();
        print.println();
        
        print.printf("◆ 图文基本信息 \r");
        print.println();
        print.printf("图文标题：%s \r", works.getTitle());
        print.println();
        print.printf("图文站点：%s \r", works.getSite());
        print.println();
        print.printf("图文类型：%s \r", works.getType());
        print.println();
        print.printf("图文发表日期：%s \r", works.getDate());
        print.println();
        print.printf("图文链接：%s \r", works.getLink());
        print.println();
        print.printf("图文封面：%s \r", works.getCover());
        print.println();
        print.printf("图文ID：%s \r", works.getId());
        print.println();
        print.printf("图文热度：%s \r", works.getAttract());
        print.println();
        print.printf("图文描述：%s \r", works.getComment());
        print.println();
        print.println();
        
        print.printf("◆ 资源基本信息 \r");
        print.println();
        print.printf("资源下载数量：%s \r", works.getDownloadCompletedNum());
        print.println();
        print.printf("资源数量：%s \r", works.getResources().size());
        print.println();
        print.println();
        
        print.printf("◆ 资源链接 \r");
        print.println();
        for (String res : works.getResources()) {
        	print.println(res);
        }
        print.println();
	}
	
	private boolean saveDescriptionFile() throws Exception {
		
        PrintStream print = null;
        try {
        	
            File dir = new File(this.savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File file = new File(this.savePath + File.separator + fileName);
            if (file.exists() && !overwrite) {
            	log.info("\n存档文件已经存在，不进行存档：{}", file.getAbsolutePath());
            	
            	TASK_NUM--;
            	return true;
            } else {
            	
            	StringBuilder taskInfo = new StringBuilder();
            	taskInfo.append("\n");
            	taskInfo.append("创建存档文件：").append(file.getAbsolutePath()).append("\n");
            	taskInfo.append("当前存档任务数量：").append(TASK_NUM).append("\n");
            	log.info(taskInfo);
            }
            
            // 创建本地文件输出流
            print = new PrintStream(file);
            
            printDescription(print);
            
        } catch (Exception e) {
            log.error("下载异常：{}", works.getTitle(), e);
            TASK_NUM--;
            return false;
        } finally {
        	// 关闭流
        	try {
				if (print != null) {
					print.flush();
					print.close();
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
        }
        TASK_NUM--;
        
        log.info("\n图文存档完毕：{}", works.getTitle());
		return true;
	}
}
