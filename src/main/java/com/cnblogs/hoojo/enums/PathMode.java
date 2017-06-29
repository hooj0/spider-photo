package com.cnblogs.hoojo.enums;

/**
 * <b>function:</b> 路径命名方式
 * @author hoojo
 * @createDate 2017-3-19 下午10:22:54
 * @file PathMode.java
 * @package com.cnblogs.hoojo.enums
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public enum PathMode {

	SITE("站点", "${site}"),
	SITE_TYPE("站点/类型", "${site}/${type}"),
	SITE_TYPE_AUTHOR("站点/类型/作者", "${site}/${type}/${author}"),
	SITE_ID("站点/id", "${site}/${id}"),
	SITE_DATE("站点/日期", "${site}/${date}"),
	SITE_TYPE_DATE("站点/类型/日期", "${site}/${type}/${date}"),
	SITE_CREATE_DATE("站点/日期", "${site}/{createDate}"),
	SITE_TYPE_CREATE_DATE("站点/类型/日期", "${site}/${type}/{createDate}"),
	SITE_AUTHOR("站点/作者", "${site}/${author}");
	
	private String desc;
	private String pathExpression;
	
	PathMode(String desc, String pathExpression) {
		this.desc = desc;
		this.pathExpression = pathExpression;
	}
	
	public String getDesc() {
		return desc;
	}
	public String getPathExpression() {
		return pathExpression;
	}
}
