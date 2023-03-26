package com.giyaYon.Service;


import com.giyaYon.Blocks.Block;
import com.giyaYon.Blocks.TransactionData;
import java.util.*;

/**
 * @author GiyaYon
 * @code 服务层 网络同步区块服务模块
 *
 * 开启服务
 *
 * //接口
 * 发送一个区块
 * 发送一个链
 *
 * 验证一个区块
 * 验证一个链
 *
 * 查看当前最新链
 * 查看整个链
 */
public class ServiceClient{

    /**
     *  receive blockchain from others 区块链数据
     */
    public static LinkedList<Block> blockchain = new LinkedList<Block>();
    /**
     *  receive difficulty form others 定义工作量难度由区块链协议定义
     */
    public static int difficulty = 6;
    /**
     *  交易池
     */
    public LinkedList<TransactionData> transactions;
    /**
     * 需要先开启联网服务
     */
    public ConnectService connection;

    /**
     * 后续的挖矿等服务
     */
    public UserService userService;


    public ServiceClient(String url){
        try{
            connection = new ConnectService(url,this);
            userService = new UserService(this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void runClientServer(){
        connection.startService();
    }

    public void exitClientServer()
    {
        connection.stopService();
    }

}
