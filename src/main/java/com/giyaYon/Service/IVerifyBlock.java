package com.giyaYon.Service;

/**
 * 验证一个或一条链
 * @author GiyaYon
 */
public interface IVerifyBlock {

    /**
     * 验证该区块是否有问题
     * @param block 需要验证的区块
     * @return 0:该块通过，
     *          1:该链有结构问题
     *          2:该链数字摘要修改过
     */
    int verifyBlock(String block);

    /**
     * 验证该区块链是否有问题
     * @param chain 需要验证的链
     * @return 0，通过 ，1不通过
     */
    int verifyChain(String chain);
}
