package com.cnblogs.hoojo.queue;

import java.util.Iterator;
import java.util.List;

import com.cnblogs.hoojo.model.Works;
import com.google.common.collect.Lists;

/**
 * <b>function:</b> 作品集
 * @author hoojo
 * @createDate 2017-3-7 下午9:24:06
 * @file WorksQueue.java
 * @package com.cnblogs.hoojo.queue
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class WorksQueue {
	private List<Works> list = Lists.newArrayList();
	
	public void add(Works works) throws Exception {
		list.add(works);
	}

	public List<Works> list() {
		return list;
	}
	
	public Iterator<Works> iterator() {
		return list.iterator();
	}
	
	public int size() {
		return list.size();
	}
}
