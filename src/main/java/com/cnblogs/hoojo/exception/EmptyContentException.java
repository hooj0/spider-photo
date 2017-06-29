package com.cnblogs.hoojo.exception;

/**
 * <b>function:</b> 空内容异常
 * @author hoojo
 * @createDate 2017-3-4 下午3:54:14
 * @file EmptyContentException.java
 * @package com.cnblogs.hoojo.exception
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class EmptyContentException extends SpiderException {

	private static final long serialVersionUID = -2714816286138707596L;

	public EmptyContentException() {
		super();
	}

	public EmptyContentException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyContentException(String message) {
		super(message);
	}
}
