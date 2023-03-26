package com.giyaYon.Service;


import com.giyaYon.Blocks.Block;
import com.giyaYon.Network.Message;
import com.giyaYon.Network.SocketClient;
import org.java_websocket.WebSocket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import static com.giyaYon.Network.BlockConstant.*;
import static com.giyaYon.Service.ServiceClient.difficulty;
import static com.giyaYon.Service.StateConstant.*;
/**
 * 系统被动型服务
 * @author GiyaYon
 */

public class ConnectService implements IBroadcastBlock,IVerifyBlock,IQueryBlock {


    ServiceClient serviceClient;
    /**
     * for communication
     */
    public SocketClient client;

    public ConnectService(String url,ServiceClient serviceClient) throws URISyntaxException {

        this.serviceClient = serviceClient;
        client = new SocketClient(new URI(url))
        {
            public void onMessage(String s)
            {
                handleMessage(client,s);
            }
        };
    }

    public SocketClient getClient() {
        return client;
    }

    /**
     * 开启联网
     */
    public void startService()
    {
        client.connect();
    }

    public void stopService(){client.close();}
    /**
     * 消息处理
     * @param conn
     * @param message
     */
    public void handleMessage(WebSocket conn, String message)
    {
        Message msg = simpleJsonToObj(message,Message.class);

        switch (msg.getType())
        {
            //请求查询当前最新区块
            case QUERY_LATEST_BLOCK:
                Message queryBlockMsg = new Message(RETURN_LATEST_BLOCK, simpleObjToJson(queryBlockFromLocal()));
                String sendBlockMsg  = simpleObjToJson(queryBlockMsg);
                conn.send(sendBlockMsg);
                break;
            //请求查询最新区块链
            case QUERY_BLOCKCHAIN:
                Message queryChain = new Message(RETURN_BLOCKCHAIN, queryChainFromLocal());
                String sendChain  = simpleObjToJson(queryChain);
                conn.send(sendChain);
                break;
            //TODO 请求查询最新交易池
            case QUERY_LATEST_TRANSACTION:

                break;
            // 上传挖矿块
            case UPLOAD_MINED_BLOCK:

                //解析消息
                Block mineBlock = simpleJsonToObj(msg.getData(),Block.class);
                //加入本账本
                ServiceClient.blockchain.add(mineBlock);
                //验证合法性，若不正确就删除这个最新的区块
                if(!isChainValid())
                {
                    ServiceClient.blockchain.removeLast();
                    System.out.println("had removed the block");
                }
                else
                {

                    Block latestBlock = queryBlockFromLocal();
                    System.out.println("i received the new block:" + latestBlock.toString());
                    //收到新的区块，如果用户仍在挖矿，则看看它挖的是不是重复的区块，是的话就提示他停下来了
                    if(serviceClient.userService.state.getType() == MINEING)
                    {
                        if(Integer.parseInt(serviceClient.userService.state.getData()) == latestBlock.index)
                        {
                            serviceClient.userService.stopToMineBlock();
                        }
                    }
                }
                break;
            // 返回最新区块
            case RETURN_LATEST_BLOCK:
                Block b = simpleJsonToObj(msg.getData(), Block.class);
                serviceClient.blockchain.add(b);
                if(!isChainValid())
                {
                    ServiceClient.blockchain.removeLast();
                    System.out.println("had removed the block");
                }else
                {
                    Block latestBlock = queryBlockFromLocal();
                    System.out.println("your local latest block is:" + latestBlock.hash);
                }

                break;
            //TODO 返回最新的区块链
            case RETURN_BLOCKCHAIN:
                serviceClient.blockchain.clear();
                String[] chain = msg.getData().split("\\|");
                for (String s : chain) {
                    Block block = simpleJsonToObj(s,Block.class);
                    serviceClient.blockchain.add(block);
                }
                for (Block block :ServiceClient.blockchain)
                {
                    System.out.println(block.toString());
                }
                break;

            default:

        }

        //System.out.println(msg);
    }

    @Override
    public int verifyBlock(String block) {
        return 0;
    }

    @Override
    public int verifyChain(String chain) {
        return 0;
    }

    @Override
    public int broadcastBlock(String msg) {

        client.sendMessage(msg);

        return 0;
    }

    @Override
    public void queryBlockFromOthers() {
        Message queryBlock = new Message(QUERY_LATEST_BLOCK,"");
        String msg = simpleObjToJson(queryBlock);
        client.sendMessage(msg);
    }

    @Override
    public Block queryBlockFromLocal() {

        return serviceClient.blockchain.getLast();
    }

    @Override
    public String queryChainFromLocal() {

        StringBuilder list = new StringBuilder();

        Iterator<Block> iterator = serviceClient. blockchain.iterator();
        Block block;
        while(iterator.hasNext()){
            block = iterator.next();
            list.append(simpleObjToJson(block));
            list.append(",");
        }
        if (list.length() > 0) {
            list.deleteCharAt(list.length() - 1);
        }

        return list.toString();
    }

    @Override
    public void queryChainFromOthers() {
        Message queryBlock = new Message(QUERY_BLOCKCHAIN,"");
        String msg = simpleObjToJson(queryBlock);
        client.sendMessage(msg);
    }

    /**
     * 验证这个区块的合法性
     * @return
     */
    public  Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[ difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i <  serviceClient.blockchain.size(); i++) {
            currentBlock = serviceClient.blockchain.get(i);
            previousBlock = serviceClient.blockchain.get(i-1);
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
