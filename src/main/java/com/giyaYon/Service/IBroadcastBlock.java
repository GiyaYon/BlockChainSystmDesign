package com.giyaYon.Service;

/**
 * 上传增加一个或一条链
 * @author GiyaYon
 */
public interface IBroadcastBlock {

    /**
     * 增加一个区块
     * @return added result
     */
    int broadcastBlock(String msg);


}
