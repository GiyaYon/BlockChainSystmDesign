package com.giyaYon.Service;


import com.giyaYon.Blocks.Block;

/**
 * 查询接口
 * @author GiyaYon
 */
public interface IQueryBlock {

    /**
     * 查询区块
     */
    void queryBlockFromOthers();

    Block queryBlockFromLocal();

    /**
     * 查询链
     */
    String queryChainFromLocal();

    void queryChainFromOthers();
}
