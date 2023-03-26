package com.giyaYon.Network;


import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author GiyaYon
 * @code 网络模块 服务端
 */
public class SocketServer extends WebSocketServer {

    private List<WebSocket> list;
    public SocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // This method sends a message to the new client
        conn.send("Welcome to the server!");
        // This method sends a message to all clients connected
        broadcast(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        list.add(conn);
        //broatCast("hi");


    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
        list.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        broadcast(conn + ":" + message);
        System.out.println(conn + ": " + message + "，修改前。");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific
            // websocket
            broadcast(conn + "has a error! it will be quit!");
            list.remove(conn);
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
        list = new ArrayList<>();
    }

    public void MsgBroadcast(String msg)
    {
        if(msg != null && list.size() > 1)
        {
            broadcast(msg);
        }
    }

}