package com.giyaYon.Blocks;

import com.giyaYon.arithmetic.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author GiyaYon
 */
public class Block {

    public int index;
    /**
     * this hash of block ,It will save our digital signature
     */
    public String hash;
    /**
     * the previous hash of block
     */
    public String previousHash;
    /**
     * our data will be a simple message.
     */
    private ArrayList<TransactionData> data;
    /**
     * as number of milliseconds since 1/1/1970.
     */
    private long timeStamp;

    private int nonce;

    public volatile boolean running = true;
    /**
     * Block Constructor.
     * @param data
     * @param previousHash
     */
    public Block(int index, ArrayList<TransactionData> data, String previousHash ) {
        this.index = index;
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        /**
         * Making sure we do this after we set the other values.
         */
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
    }

    public String mineBlock (int difficulty){
        // Create a string with difficulty * "0"
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
            if(!running)
            {
                System.out.println("Block Mined despected!!! " );
                return null;
            }
        }
        System.out.println("Block Mined!!! : " + hash);
        return hash;
    }

    public void stopMining()
    {
        running = false;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", data=" + data +
                ", timeStamp=" + timeStamp +
                ", nonce=" + nonce +
                '}';
    }
}
