package com.cnblogs.hoojo.util;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import com.cnblogs.hoojo.config.RuntimeOptions;
import com.cnblogs.hoojo.enums.NamedMode;
import com.cnblogs.hoojo.enums.PathMode;
import com.cnblogs.hoojo.model.Works;
import com.google.common.collect.Sets;

/**
 * <b>function:</b> 路径命名、文件夹命名转换工具类
 * @author hoojo
 * @createDate 2017-3-26 下午4:29:53
 * @file FilePathNameUtils.java
 * @package com.cnblogs.hoojo.util
 * @project PhotoSpider
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class FilePathNameUtils {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// 空白字符集ascii
    private static final Set<Integer> EXCLUDE_ASCII = Sets.newHashSet( 127, 129, 141, 143, 144, 151, 157 );
    
    @SuppressWarnings("unlikely-arg-type")
	public static boolean includeAsciiBlank(String src) {
        
        for (int i = 0, len = src.length(); i < len; i++) {
            long ascii = src.charAt(i);
            //System.out.println(src.charAt(i) + "->" + ascii);
            if (ascii >=0 && ascii <= 31) {
                //System.out.println("包含非法字符集：" + ascii);
                return true;
            } else if (EXCLUDE_ASCII.contains(ascii)) {
                //System.out.println("包含非法字符集：" + ascii);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * <b>function:</b> 删除空白字符集、非打印字符集
     * @author hoojo
     * @createDate 2017-1-13 下午3:19:04
     */
    public static String removeAsciiBlank(String src) {
        char[] srcs = src.toCharArray();
    
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = srcs.length; i < len; i++) {
            int ascii = srcs[i];
            //System.out.println(srcs[i] + "->" + ascii + "--" + Long.toHexString(ascii));
            if (ascii >=0 && ascii <= 31) {
                //System.out.println("包含非法字符集：" + ascii);
            } else if (EXCLUDE_ASCII.contains(ascii)) {
                //System.out.println("包含非法字符集：" + ascii);
            } else {
                sb.append(srcs[i]);
            }
        }
        
        return sb.toString();
    }
	
	public static String conversionNamed(NamedMode mode, Works works) throws Exception {
		
		String name = mode.getPathExpression();
		Map<String, Object> data = desc(works);
		data.put("createDate", DATE_FORMAT.format(new Date()));
		
		name = ConversionUtils.resolverExpression(name, data);
		return cleanPath(name);
	}
	
	public static String conversionFilePath(PathMode mode, Works works) throws Exception {
		
		String name = mode.getPathExpression();
		Map<String, Object> data = desc(works);
		data.put("createDate", DATE_FORMAT.format(new Date()));
		
		name = ConversionUtils.resolverExpression(name, data);
		return cleanPath(name);
	}
	
	public static String conversionPathName(RuntimeOptions options, Works works) throws Exception {
		
		Map<String, Object> data = desc(works);
		data.put("createDate", DATE_FORMAT.format(new Date()));

		String path = options.getPathMode().getPathExpression();
		path = ConversionUtils.resolverExpression(path, data);
		
		String workName = options.getNamedMode().getPathExpression();
		workName = ConversionUtils.resolverExpression(workName, data);
		
		return cleanPath(path + File.separatorChar + workName);
	}
	
	/**
	 * <b>function:</b> 将一个JavaObject转换成一个Map对象
	 * @author hoojo
	 * @createDate 2012-2-9 下午05:47:54
	 * @param obj 将要转换的对象
	 * @return Map
	 */
	public static Map<String, Object> desc(Object obj) {

		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);

		PropertyDescriptor[] pds = wrapper.getPropertyDescriptors();
		HashMap<String, Object> ps = new HashMap<String, Object>();

		for (PropertyDescriptor pd : pds) {

			Method readMethod = pd.getReadMethod();
			if (readMethod == null || pd.getName().equals("class")) {
				continue;
			}
			try {
				ps.put(pd.getName(), readMethod.invoke(obj));
			} catch (IllegalArgumentException e) {
				// noop
			} catch (IllegalAccessException e) {
				// noop
			} catch (InvocationTargetException e) {
				// noop
			}
		}
		return ps;
	}
	
	public static String cleanPath(String path) {
	    String[] pattens = { "|", "\"", ":", "*", "?", "<", ">" };
	    
	    for (String patten : pattens) {
	        path = StringUtils.remove(path, patten);
	    }
	    return StringUtils.trim(path);
	}
	
	public static String cleanParam(String fileName) {
	    if (StringUtils.contains(fileName, "?")) {
	    	return StringUtils.substring(fileName, 0, StringUtils.indexOf(fileName, "?"));
	    }
	    return removeAsciiBlank(fileName);
	}
	
	public static String clean(String name) {
	    String[] pattens = { "|", "\"", ":", "*", "?", "<", ">", "." };
	    
	    for (String patten : pattens) {
	    	name = StringUtils.remove(name, patten);
	    }
	    return StringUtils.trim(name);
	}
}
