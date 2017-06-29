package com.cnblogs.hoojo.enums;

/**
 * <b>function:</b> 任务状态
 * @author hoojo
 * @createDate 2017-3-5 上午11:16:39
 * @file TaskState.java
 * @package com.cnblogs.hoojo.enums
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public enum TaskState {

	START("启动"), WAIT("等待"), RUN("运行"), FINISH("完成"), PAUSE("暂停"), EXCEPTION("异常");
	
	private String desc;
	TaskState(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}
}
