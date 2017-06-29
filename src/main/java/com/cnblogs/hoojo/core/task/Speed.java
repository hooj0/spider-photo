package com.cnblogs.hoojo.core.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * <b>function:</b>
 * 
 * @author hoojo
 * @createDate 2017-6-24 上午9:59:47
 * @file Speed.java
 * @package com.cnblogs.hoojo.core.task
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class Speed {

	public void name() throws Exception {

		long speed = 1024 * 100L;// 限制下载速度为100k/s,
		long current = 0;
		OutputStream out = new FileOutputStream(new File(""));
		File file = new File("xxxxxx");
		FileInputStream in = new FileInputStream(file);

		byte[] temp = new byte[1024];
		int i;
		long startTime = System.currentTimeMillis();
		while ((i = in.read(temp)) != -1) {
			current = current + i;
			out.write(temp);
			if (current > speed) {
				startPause(startTime + 1000);
				current = 0;
				startTime = System.currentTimeMillis();
			}
		}

		out.close();
		in.close();
	}

	private void startPause(long time) {
		while (true) {
			if (System.currentTimeMillis() > time) {
				break;
			}
		}
	}
}
