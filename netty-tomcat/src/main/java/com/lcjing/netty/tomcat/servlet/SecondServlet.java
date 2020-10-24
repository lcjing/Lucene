package com.lcjing.netty.tomcat.servlet;


import com.lcjing.netty.tomcat.http.MyRequest;
import com.lcjing.netty.tomcat.http.MyResponse;
import com.lcjing.netty.tomcat.http.MyServlet;
/**
 * @author lcjing
 * @date 2020/10/16
 */
public class SecondServlet extends MyServlet {

	public void doGet(MyRequest request, MyResponse response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(MyRequest request, MyResponse response) throws Exception {
		response.write("This is Second Servlet");
	}

}
