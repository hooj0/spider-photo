package com.cnblogs.hoojo.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <b>function:</b>
 * @author hoojo
 * @createDate 2017-2-27 上午9:59:28
 * @file DownloadInfo.java
 * @package com.hoo.spider
 * @project Spider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class DownloadInfo implements Serializable {

    private static final long serialVersionUID = -7587127225988360402L;
    
    private String refId;
    private String worksTitle;
    
	private String url;
    private String fileName;
    private String name;
    
    private long size;
    private String type;
    
    private Date beginTime;
    private Date endTime;
    
    private String usedTime;
    private String savePath;
    
    private int retryCount = 0;
    
    public DownloadInfo() {
    }
    
    public DownloadInfo(String url) {
        this.url = url;
    }
    
    public DownloadInfo(String url, String savePath) {
        this.url = url;
        this.savePath = savePath;
    }
    
    public DownloadInfo(String url, String fileName, String savePath) {
    	
    	this.url = url;
        this.fileName = fileName;
        this.savePath = savePath;
    }
    public DownloadInfo(String url, String fileName, String savePath, long size, String type, String usedTime) {
        super();
        this.url = url;
        this.fileName = fileName;
        this.savePath = savePath;
        this.size = size;
        this.type = type;
        this.usedTime = usedTime;
    }
    
    public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getUsedTime() {
		return usedTime;
	}

	public void setUsedTime(String usedTime) {
		this.usedTime = usedTime;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getWorksTitle() {
		return worksTitle;
	}

	public void setWorksTitle(String worksTitle) {
		this.worksTitle = worksTitle;
	}
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public int getRetryCount() {
		return retryCount;
	}

	public void retryDownload() {
		this.retryCount++;
	}
}
