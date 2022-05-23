//package com.rubygym.servlet;
//
//import java.io.IOException;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletResponse;
//
//@WebFilter(asyncSupported = true, urlPatterns = { "/*" })
//public class SimpleCORSFilter implements Filter {
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//
//		((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
//        ((HttpServletResponse) response).addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");
//        ((HttpServletResponse) response).addHeader("Access-Control-Allow-Headers", "Content-Type");
//        ((HttpServletResponse) response).addHeader("Access-Control-Max-Age", "86400");
//		
//        chain.doFilter(request, response);
//	}
//	
//	@Override
//	public void init(FilterConfig filterConfig) throws ServletException {
//		
//	}
//	
//	@Override
//	public void destroy() {
//		
//	}
//
//}
