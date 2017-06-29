package com.cnblogs.hoojo.exception;

/**
 * <b>function:</b> 自定义异常
 * @author hoojo
 * @createDate 2017-3-4 下午3:52:08
 * @file SpiderException.java
 * @package com.cnblogs.hoojo.exception
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SpiderException extends RuntimeException {

	private static final long serialVersionUID = 8480245097238147096L;

	public SpiderException() {
		super();
	}

	public SpiderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SpiderException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpiderException(String message) {
		super(message);
	}

	public SpiderException(Throwable cause) {
		super(cause);
	}
}
