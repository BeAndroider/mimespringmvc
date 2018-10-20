package base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	HandlerMapping map;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String uri = request.getRequestURI();
		System.out.println("URI:"+uri);
		//获取得到Handler对象的key
		String path = uri.substring(uri.lastIndexOf("/"));
		System.out.println("key:"+path);
		Handler handler = map.getHandler(path);
		Method m = handler.getMethod();
		Object o = handler.getObj();
		
		String viewName;
		try {
			//调用方法前需要检查是否有参数,大于0就是有参数
			Class[] param = m.getParameterTypes();
			if(param.length > 0) {
				//准备一个存放参数的数组
				Object[] newparam = new Object[param.length];
				for(int i=0;i<param.length;i++) {
					if(param[i] == HttpServletRequest.class) {
						newparam[i] = request;
					}
					if(param[i] == HttpServletResponse.class) {
						newparam[i] = response;
					}
				}
				//这里调用含有注解的方法，获取方法的返回值作为视图名
				viewName = (String) m.invoke(o, newparam);
			}else {
				viewName = (String) m.invoke(o);
			}
			
			if(viewName.startsWith("redirect:")) {
				//这里进行重定向，文件名是应用名+/WEB-INF/+视图名+.jsp
				String url = request.getContextPath()+"/"+viewName.substring("redirect:".length());
				response.sendRedirect(url);
			}else {
				//这里进行转发，jsp文件名是/WEB-INF/+视图名+.jsp
				String url = "/WEB-INF/"+viewName+".jsp";
				request.getRequestDispatcher(url).forward(request,response);
			}
			
			
			
			
			
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void init() throws ServletException {
		try {
			SAXReader reader = new SAXReader();
			String mvcxml = getServletConfig().getInitParameter("mvc");
			InputStream in = getClass().getClassLoader().getResourceAsStream(mvcxml);
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			List<Element> beans = root.elements();
			//存储从mvc.xml文件中读取出来的bean元素
			List<Object> beanList = new ArrayList<Object>();
			for(Element e : beans) {
				String beanName = e.attributeValue("class");
				try {
					Class cls = Class.forName(beanName);
					Object bean = cls.newInstance();
					beanList.add(bean);
				} catch (Exception e1) {
					System.out.println("读取bean并且加载这里出错");
					e1.printStackTrace();
				}
			}
			
			//把beanList交给HandlerMapping建立资源路径和controller映射关系
			map = new HandlerMapping();
			map.process(beanList);
			
			
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
