package controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import annotation.RequestMapping;

public class LoginController {
	
	@RequestMapping("/tologin.do")
	public String toLogin() {
		return "login";
	}
	
	@RequestMapping("/login.do")
	public String login(HttpServletRequest request,HttpServletResponse response) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if("han".equals(username) && "123".equals(password)) {
			return "welcome";
		}
		
		return "redirect:tologin.do";
	}
	
	
	
	
}
