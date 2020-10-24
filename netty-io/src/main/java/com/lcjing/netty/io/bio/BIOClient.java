package com.lcjing.netty.io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * BIO 客户端
 *
 * @author lcjing
 * @date 2020/10/15
 */
public class BIOClient {

    public static void main(String[] args) throws IOException {
        //要和谁进行通信，服务器IP、服务器端口
        Socket client = new Socket("localhost", 8080);

        //向服务端发送数据, 写：Output
        OutputStream os = client.getOutputStream();
        //生成一个随机的ID
        String name = UUID.randomUUID().toString();
        System.out.println("客户端发送数据：" + name);
        os.write(name.getBytes());
        os.close();
        client.close();
    }

}
