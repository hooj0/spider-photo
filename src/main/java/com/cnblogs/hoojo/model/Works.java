package com.cnblogs.hoojo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * <b>function:</b> 博文作品对象
 * @author hoojo
 * @createDate 2017-2-28 下午12:05:17
 * @file Works.java
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class Works implements Serializable {

	private static final long serialVersionUID = -4738363631103752313L;
	
	private String site;
	// 热门、最新、人像、杂志、原创
	private String type;
	
	private String id;
    private String title;
    private String author;
    private String cover;
    private String link;
    private String date;
    private String comment;
    private String attract;
    
    private String avatar;
    private String blog;

    private List<String> resources = new ArrayList<String>();

	private int downloadCompletedNum = 1;

    public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAttract() {
		return attract;
	}

	public void setAttract(String attract) {
		this.attract = attract;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public String getBlog() {
		return blog;
	}
	public void setBlog(String blog) {
		this.blog = blog;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public synchronized int getDownloadCompletedNum() {
		return this.downloadCompletedNum;
	}
	
	public synchronized void nextDownload() {
		if (!isFinish()) {
			this.downloadCompletedNum++;
		}
	}
	
	public boolean isFinish() {
		if (this.downloadCompletedNum >= this.getResources().size()) {
			return true;
		}
		return false;
	}
	
	@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
