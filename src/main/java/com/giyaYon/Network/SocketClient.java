package com.giyaYon.Network;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GiyaYon
 * @code 网络模块，客户端
 */
public class SocketClient extends WebSocketClient {

    private List<WebSocket> list = new ArrayList<>();

    public List<WebSocket> getList() {
        return list;
    }

    public void setList(List<WebSocket> list) {
        this.list = list;
    }

    public SocketClient(URI serverUri) {
        super(serverUri);
    }

    public SocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        list.add(this);
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        list.remove(this);
    }

    @Override
    public void onError(Exception e) {
        list.remove(this);
    }

    public void sendMessage(String msg)
    {
        if(msg != null) {
            this.send(msg);
        }
    }
}
