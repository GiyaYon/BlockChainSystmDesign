package com.giyaYon.Service;

import com.giyaYon.Blocks.Block;
import com.giyaYon.Network.Message;

import java.util.ArrayList;

import static com.giyaYon.Network.BlockConstant.UPLOAD_MINED_BLOCK;
import static com.giyaYon.Network.BlockConstant.simpleObjToJson;
import static com.giyaYon.Service.StateConstant.*;

/**
 * 用户主动型服务
 * @author GiyaYon
 */
public class UserService {

    ServiceClient serviceClient;
    /**
     * 该客户端正在干什么
     */
    public Message state;

    public Block currentMineBlock;

    public UserService(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
        state = new Message(IDLE,"");
    }

    /**
     * 挖矿
     */
    public void tryToMineBlock() {

        //保持联网状态挖矿
        if(serviceClient.connection.client.isClosed()) {return;}
        if(serviceClient.connection.client.isClosing()) {return;}
        if(state.getType() != IDLE){return;}

        //开始挖矿
        int index = ServiceClient.blockchain.size();
        if (ServiceClient.blockchain.size() < 1) {
            //定义正在挖矿的状态
            state = new Message(MINEING, "0");
            //开挖
            currentMineBlock = new Block(0, new ArrayList<>(), "0");
            String broadcastNewBlockHash = null;
            //模拟矿工挖矿过程
            broadcastNewBlockHash = currentMineBlock.mineBlock(ServiceClient.difficulty);
            //如果为空则会自动抛弃这个矿不会广播
            if(broadcastNewBlockHash != null)
            {
                System.out.println("第0个！" + broadcastNewBlockHash);
                Message msg = new Message(UPLOAD_MINED_BLOCK, simpleObjToJson(currentMineBlock));
                String broadcastBlock = simpleObjToJson(msg);
                //将挖到的区块广播出去
                serviceClient.connection.broadcastBlock(broadcastBlock);
            }
        } else {
            //非创世矿挖矿
            state = new Message(MINEING, String.valueOf(index));
            //开挖
            currentMineBlock = new Block(index, new ArrayList<>(), ServiceClient.blockchain.get(ServiceClient.blockchain.size() - 1).hash);
            String broadcastNewBlockHash = null;
            broadcastNewBlockHash = currentMineBlock.mineBlock(ServiceClient.difficulty);
            if(broadcastNewBlockHash != null) {
                System.out.println("第" + index + "个！" + broadcastNewBlockHash);
                Message msg = new Message(UPLOAD_MINED_BLOCK, simpleObjToJson(currentMineBlock));
                String broadcastBlock = simpleObjToJson(msg);
                //将挖到的区块广播出去
                serviceClient.connection.broadcastBlock(broadcastBlock);
            }

        }
        state = new Message(IDLE, "");
    }

    public void stopToMineBlock()
    {
        currentMineBlock.stopMining();
    }

}

