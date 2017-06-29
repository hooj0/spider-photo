package com.cnblogs.hoojo.enums;

/**
 * <b>function:</b> 命名方式
 * @author hoojo
 * @createDate 2017-3-19 下午10:15:44
 * @file NamedMode.java
 * @package com.cnblogs.hoojo.enums
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public enum NamedMode {

	DEFAULT("默认", ""),
	
	TITLE("标题", "${title}"),
	TITLE_AUTHOR("标题-作者", "${title}@${author}"),
	TITLE_AUTHOR_DATE("标题-作者-发表日期", "${title}@${author}-${date}"),
	
	TITLE_AUTHRO_CREATE_DATE("标题-作者-爬取日期", "${title}@${author}-${createDate}"),
	
	DATE("发表日期", "${date}"),
	DATE_TITLE("发表日期-标题", "${date}-${title}"),
	DATE_TITLE_AUTHOR("发表日期-标题-作者", "${date}-${title}@${author}"),
	DATE_TITLE_AUTHOR_ATTRACT("发表日期-标题-作者-热度信息", "${date}-${title}@${author}_${attract}"),
	
	SITE_TITLE("站点-标题", "${site}_${title}"),
	SITE_TITLE_AUTHOR("站点-标题-作者", "${site}_${title}@${author}"),
	SITE_TITLE_AUTHOR_DATE("站点-标题-作者-发表日期", "${site}_${title}@${author}_${date}"),
	SITE_TITLE_AUTHOR_DATE_ATTRACT("站点-标题-作者-发表日期-热度信息", "${site}_${title}@${author}_${date}_${attract}"),
	
	SITE_TITLE_AUTHRO_CREATE_DATE("站点-标题-作者-爬取日期", "${site}_${title}@${author}_${createDate}");
	
	private String desc;
	private String pathExpression;
	
	public String getDesc() {
		return desc;
	}

	public String getPathExpression() {
		
		return pathExpression;
	}

	NamedMode(String desc, String pathExpression) {
		this.desc = desc;
		this.pathExpression = pathExpression;
	}
}
