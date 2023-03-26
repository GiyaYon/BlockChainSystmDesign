import com.giyaYon.Blocks.Block;


import com.giyaYon.Network.SocketClient;
import com.giyaYon.Network.SocketServer;
import com.giyaYon.Service.ServiceServer;
import com.google.gson.GsonBuilder;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.giyaYon.Service.ServiceClient.difficulty;

public class Test {

    //https://www.cnblogs.com/helloworld2018/p/9011369.html

//    @org.junit.Test
//    public void test01() {
//
//            Block genesisBlock = new Block(0,new ArrayList<>(), "0");
//            System.out.println("Hash for block 1 : " + genesisBlock.hash);
//
//            Block secondBlock = new Block(1,new ArrayList<>(),genesisBlock.hash);
//            System.out.println("Hash for block 2 : " + secondBlock.hash);
//
//            Block thirdBlock = new Block(2,new ArrayList<>(),secondBlock.hash);
//            System.out.println("Hash for block 3 : " + thirdBlock.hash);
//
//    }

//    @org.junit.Test
//    public void test02() {
//        ArrayList<Block> blockchain = new ArrayList<Block>();
//
//        //add our blocks to the blockchain ArrayList:
//        blockchain.add(new Block(0,new ArrayList<>(), "0"));
//        blockchain.add(new Block(1,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
//        blockchain.add(new Block(2,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
//
//        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println(blockchainJson);
//
//
//      }


    ArrayList<Block> blockchain = new ArrayList<Block>();
    int difficulty = 3;
//    @org.junit.Test
//    public void test03()
//    {
//
//
//            blockchain.add(new Block(0,new ArrayList<>(), "0"));
//          System.out.println("Trying to Mine block 1... ");
//          blockchain.get(0).mineBlock(difficulty);
//
//        blockchain.add(new Block(1,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
//          System.out.println("Trying to Mine block 2... ");
//          blockchain.get(1).mineBlock(difficulty);
//
//        blockchain.add(new Block(2,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
//          System.out.println("Trying to Mine block 3... ");
//          blockchain.get(2).mineBlock(difficulty);
//
//          System.out.println("\nBlockchain is Valid: " + isChainValid());
//
//          String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//          System.out.println("\nThe block chain: ");
//          System.out.println(blockchainJson);
//    }

    /**
     * 验证这个区块的合法性
     * @return
     */
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

//    @org.junit.Test
//    public void server() {
//
//        int port = 8888;
//        try {
//            SocketServer server = new SocketServer(port);
//            server.start();
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
//        Scanner scanner = new Scanner(System.in);
//        String wait = scanner.nextLine();
//
//    }
////  ws://localhost:8888
//    @org.junit.Test
//    public void client()
//    {
//        SocketClient client = null;
//        try {
//            client = new SocketClient(new URI("ws://localhost:8888"));
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        client.connect();
//        Scanner scanner = new Scanner(System.in);
//        String wait = scanner.nextLine();
//    }
//        int port = 8888; // 843 flash policy port
//
//        SocketServer s = new SocketServer(port);
//        s.start();
//        System.out.println("ChatServer started on port: " + s.getPort());
//
//        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
//        while (true) {
//            String in = sysin.readLine();
//            //s.broadcast(in);
//            if (in.equals("exit")) {
//                s.stop(1000);
//                break;
//            }
//        }



    @org.junit.Test
    public void test05()
    {
        ServiceServer server = new ServiceServer(8888);
        server.runServer();
    }

}


