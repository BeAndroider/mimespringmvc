package base;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.RequestMapping;

public class HandlerMapping {
	
	//存储路径和Handler的映射
	private Map<String,Handler> map = new HashMap<>();
	
	public void process(List<Object> beanList) {
		for(Object o : beanList) {
			Class cls = o.getClass();
			Method[] methods = cls.getDeclaredMethods();
			//遍历所有方法，将带有注解的方法，获取出注解的value作为key，对象作为value
			for(Method m : methods) {
				RequestMapping rm = m.getAnnotation(RequestMapping.class);
				if(rm != null) {
					String path = rm.value();//key值
					Handler hander = new Handler(m, o);
					map.put(path, hander);
				}
			}
		}
	}

	public Handler getHandler(String path) {
		return map.get(path);
	}
}
