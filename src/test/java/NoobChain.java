import java.util.ArrayList;

import com.giyaYon.Blocks.Block;
import com.google.gson.GsonBuilder;

/**
 * @author GiyaYon
 */
public class NoobChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();

    public static void main(String[] args) {
        //add our blocks to the blockchain ArrayList:
//        blockchain.add(new Block(0,new ArrayList<>(), "0"));
//        blockchain.add(new Block(1,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
//        blockchain.add(new Block(2,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
//
//        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println(blockchainJson);
    }
}
