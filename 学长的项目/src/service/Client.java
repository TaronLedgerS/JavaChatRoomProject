package service;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端
 * Created by wuhul on 2016/3/17.
 */
public class Client {
//    Socket socket;

    public Socket connect() throws IOException {
        Socket socket = null;
        socket = new Socket("127.0.0.1", 4000);
        return socket;
    }

}
