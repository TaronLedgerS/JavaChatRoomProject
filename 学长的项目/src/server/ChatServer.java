package server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
class Chat extends Thread {
    private Socket socket;
    private Map<Integer, Socket> socketMap;
    private Map<Integer, Integer> userMap;//<用户ID，端口>
    private int count;
    private int port;
    private int id;
    private JTextArea textArea1;

    public Chat(int count, Socket socket, Map<Integer, Socket> socketMap, Map<Integer, Integer> userMap, int port,JTextArea textArea1) {
        this.count = count;
        this.socket = socket;
        this.socketMap = socketMap;
        this.port = port;
        this.userMap = userMap;
        this.textArea1=textArea1;
    }



    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            InputStream inputStream = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            Boolean isRoom = true;//是否为群聊，否则为私聊
            String roomID = "0";
            String userName = "null";
            String toUserID = "0";
            String message = null;
            int nullNum=0;
            while (true) {
                message = reader.readLine();
                System.out.println("接收：" + message);
                textArea1.append("接收：" + message+"\n");
                if (message != null) {
                    nullNum=0;
                    // 接收到客户端的bye信息，客户端即将退出，并将bye写入到该客户端
                    if (message.equals("TUOGOL")) {
                        writer = new PrintWriter(socket.getOutputStream());
                        writer.println("bye");
                        writer.flush();
                        for (int ks : userMap.keySet()) {
                            if (port == userMap.get(ks)) {
                                userMap.remove(ks);
                            }
                        }
                        socketMap.remove(port);
                        System.out.println("删除更新userMap：" + userMap);
                        System.out.println("删除更新socketMap：" + socketMap);
                        break;
                    }
                    String[] info = message.split("#@");
                    if (info[0].equals("port")) {
                        //消息:“port标签+端口+userID”
                        id=Integer.valueOf(info[2]);
                        if (userMap.containsKey(id)) {
                            //用户在线,挤掉原来的用户
                            writer = new PrintWriter(socketMap.get(userMap.get(id)).getOutputStream());
                            writer.println("TUOGOL");
                            writer.flush();
                            userMap.put(Integer.valueOf(info[2]), Integer.valueOf(info[1]));
                            System.out.println("更新userMap：" + userMap);
                        } else {
                            //用户不在线
                            userMap.put(Integer.valueOf(info[2]), Integer.valueOf(info[1]));
                            System.out.println("建立userMap：" + userMap);
                        }
                        continue;
                    } else if (info[0].equals("join")) {
                        //加入群聊,提示其他群成员
                        //消息封装成“join标签+roomID+userID+userName”
                        isRoom = true;//群聊
                        message = "room#@" + info[1] + "#@" + "系统消息：" + "#@" + info[2] +"#@"+info[3]+ "进来了";
                    } else if (info[0].equals("room")) {
                        isRoom = true;//群聊
                        roomID = info[1];
                        userName = info[2];
                    } else if (info[0].equals("priv")) {
                        isRoom = false;//私聊
                        toUserID = info[1];
                        userName = info[2];
                        message = "priv#@" + id + "#@" + userName + "#@"+info[3];
                    } else {
                        String header = null;//信息头
                        if (isRoom) {
                            header = "room#@" + roomID + "#@" + userName + "#@";
                        } else {
                            header = "priv#@" + id + "#@" + userName + "#@";
                        }
                        if (message.equals("")) message = "\n";
                        message = header + message;
                    }

                    System.out.println("发送：" + message);
                    textArea1.append("发送：" + message+"\n");
                    if (isRoom) {
                        // 向所有的客户端发送接收到信息，实现群聊
                        for (Map.Entry<Integer, Socket> socket : socketMap.entrySet()) {
                            writer = new PrintWriter(socket.getValue().getOutputStream());
                            writer.println(message);
                            writer.flush();
                        }
                    } else {
                        //私聊
                        if (userMap.containsKey(Integer.valueOf(toUserID))){
                            int toport = userMap.get(Integer.valueOf(toUserID));
                            System.out.println("toport" + toport);
                            writer = new PrintWriter(socketMap.get(toport).getOutputStream());
                            writer.println(message);
                            writer.flush();
                        }else{
                            message = "priv#@" + toUserID + "#@系统消息：#@暂时无法连接到对方，请稍后再联系。";
                            writer = new PrintWriter(socket.getOutputStream());
                            writer.println(message);
                            writer.flush();
                        }
                    }
                }else{
                    nullNum++;
                    if(nullNum==10) break;
                }
            }
        } catch (IOException e) {
            socketMap.remove(port);
            e.printStackTrace();
        }
    }
}

public class ChatServer {
    private JTextArea textArea1;
    public ChatServer() {
    }
    public ChatServer(JTextArea textArea1) {
        this.textArea1=textArea1;
    }
    /**
     * Description
     *
     * @param
     */

    public void startWork() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4000);
            Map<Integer, Socket> socketMap = new HashMap<>();
            Map<Integer, Integer> userMap = new HashMap<>();
            Socket socket = null;
            int count = 0;
            while (true) {
                socket = serverSocket.accept();
                count++;
                System.out.println(count + " clinet connected to the server!");
                textArea1.append(count + " clinet connected to the server!\n");

                // 将每一个连接到该服务器的客户端，加到List中
                socketMap.put(socket.getPort(), socket);
                for(Map.Entry<Integer,Socket> ss: socketMap.entrySet()){
                    if(ss.getValue().isClosed()){
                        socketMap.remove(ss.getKey());
                    }
                }
                System.out.println("socketMap:" + socketMap);
                // 每一个连接到服务器的客户端，服务器开启一个新的线程来处理
                new Chat(count, socket, socketMap, userMap, socket.getPort(),textArea1).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description
     *
     * @param args
     * @throws IOException
     */
//    public static void main(String[] args){
//        ChatServer chatServer = new ChatServer();
//        chatServer.startWork();
//    }

}