package com.lcjing.netty.tomcat.http;
/**
 * @author lcjing
 * @date 2020/10/16
 */
public abstract class MyServlet {
	
	public void service(MyRequest request, MyResponse response) throws Exception{
		
		//由service方法来决定，是调用doGet或者调用doPost
		if("GET".equalsIgnoreCase(request.getMethod())){
			doGet(request, response);
		}else{
			doPost(request, response);
		}

	}
	
	public abstract void doGet(MyRequest request, MyResponse response) throws Exception;
	
	public abstract void doPost(MyRequest request, MyResponse response) throws Exception;
}
