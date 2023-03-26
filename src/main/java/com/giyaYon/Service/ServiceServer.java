package com.giyaYon.Service;

import com.giyaYon.Blocks.Block;
import com.giyaYon.Network.Message;
import com.giyaYon.Network.SocketServer;
import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.giyaYon.Network.BlockConstant.*;
import static com.giyaYon.Service.ServiceClient.difficulty;


/**
 * @author GiyaYon
 */
public class ServiceServer implements IQueryBlock{

    //通信服务器
    SocketServer server;

    //开启端口
    public int port;

    //账本
    LinkedList<Block> blockchain = new LinkedList<Block>();

    public ServiceServer(int port) {
        this.port = port;
    }

    public void runServer()
    {

        try {
                server = new SocketServer(port)
            {
                public void onMessage(WebSocket conn, String message)
                {

                    handleMessage(conn,message);

//                    broadcast(conn + ":" + message);
//                    System.out.println(conn + ": " + message + "，修改后。");
                }
            };

            server.start();
            System.out.println("ChatServer started on port: " + server.getPort());

            BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String in = sysin.readLine();
                //s.broadcast(in);
                if (in.equals("exit")) {
                    server.stop(1000);
                    break;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void handleMessage(WebSocket conn, String message)
    {
        Message msg = simpleJsonToObj(message,Message.class);

        switch (msg.getType())
        {
            case QUERY_LATEST_BLOCK:
                Message queryBlockMsg = new Message(RETURN_LATEST_BLOCK, simpleObjToJson(queryBlockFromLocal()));
                String sendBlockMsg  = simpleObjToJson(queryBlockMsg);
                conn.send(sendBlockMsg);

                System.out.println(conn.getRemoteSocketAddress() + ",queryBLOCK:"+ queryBlockMsg.getData());
                break;
            case QUERY_BLOCKCHAIN:
                Message queryChain = new Message(RETURN_BLOCKCHAIN, queryChainFromLocal());
                String sendChain  = simpleObjToJson(queryChain);
                conn.send(sendChain);

                System.out.println(conn.getRemoteSocketAddress() +": QUERY_BLOCK_CHAIN:"+ queryChain.getData());
                break;
            case QUERY_LATEST_TRANSACTION:
                server.broadcast("TRANSACTION:" + msg.getData());
                System.out.println("TRANSACTION:" + msg.getData());
                break;
            case UPLOAD_MINED_BLOCK:

                Block mineBlock = simpleJsonToObj(msg.getData(),Block.class);
                blockchain.add(mineBlock);
                if(!isChainValid())
                {
                    blockchain.removeLast();
                }
                else
                {
                    server.broadcast(message);
                    System.out.println(message);
                }
                break;

            case RETURN_LATEST_BLOCK:
                Block b = simpleJsonToObj(msg.getData(),Block.class);
                blockchain.add(b);

                System.out.println("got the latest block! " + queryBlockFromLocal().hash);

                break;
            case RETURN_BLOCKCHAIN:
                //server.broadcast("DOWNLOAD_BLOCKCHAIN:"+ msg.getData());

                //System.out.println("DOWNLOAD_BLOCKCHAIN:"+ msg.getData());
                break;

            default:
        }

        //System.out.println(msg);
    }


    @Override
    public void queryBlockFromOthers() {
//        Message queryBlock = new Message(QUERY_LATEST_BLOCK,"");
//        String msg = simpleObjToJson(queryBlock);
//        server.sendMessage(msg);
    }

    @Override
    public Block queryBlockFromLocal() {
        return blockchain.getLast();
    }

    @Override
    public String queryChainFromLocal() {

        StringBuilder list = new StringBuilder();

        Iterator<Block> iterator = blockchain.iterator();
        Block block;
        while(iterator.hasNext()){
            block = iterator.next();
            list.append(simpleObjToJson(block));
            list.append("|");
        }
        if (list.length() > 0) {
            list.deleteCharAt(list.length() - 1);
        }

        return list.toString();
    }

    @Override
    public void queryChainFromOthers() {

    }


    public  Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[ difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i <  blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

}
