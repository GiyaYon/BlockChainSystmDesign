---
title : 区块链技术实践-使用Java搭建简单的区块链系统
tags: 
      - 应用程序开发
      -	区块链
      - JavaSE
      - JavaWeb
category : 
- [软件工程,软件项目]
---

# 前言

本文欲实现的需求：

1. **设计区块和链结构**：你可以创建一个`Block`类来表示区块，其中包含属性：如时间戳、交易数据、前一个区块的哈希值、当前区块的哈希值等。然后创建一个`Blockchain`类来表示整个区块链，其中包含一个`Block`对象的列表来存储所有的区块。

2. **挖矿**：你可以在`Blockchain`类中添加一个挖矿方法，该方法接收一组交易数据作为参数，并创建一个新的区块。在这个过程中，你需要执行工作量证明算法来寻找满足特定条件的哈希值。

   挖矿是区块链中的一个重要过程，它指的是通过解决复杂的计算问题来创建新区块并获得奖励的过程。在一个基于工作量证明算法（Proof of Work，PoW）的区块链系统中，挖矿通常包括以下几个步骤：

   1. ~~**收集交易数据**：首先，挖矿节点需要收集一组未经确认的交易数据，并将其打包到一个新区块中。~~
   2. **计算区块哈希值**：其次，挖矿节点需要对新区块进行哈希运算，以计算出它的哈希值。这一步通常需要使用特定的哈希算法（如SHA-256）。
   3. **执行工作量证明算法**：然后，挖矿节点需要执行工作量证明算法来寻找满足特定条件的哈希值。具体来说，它需要不断改变新区块中的某个字段（如随机数），并重新计算哈希值，直到找到一个以特定数量的零开头的哈希值。
   4. **广播新区块**：最后，当挖矿节点成功找到满足条件的哈希值时，它就可以将新区块广播到整个网络中，并获得相应的奖励。

   以上就是一个简单的挖矿过程。不同的区块链系统可能会有所不同，请根据实际情况进行调整。

3. **设计P2P网络**：你可以使用Java中的网络编程技术（如Socket）来实现P2P网络。每个节点都可以监听特定端口，并与其他节点建立连接。

4. **新产生的区块同步到其它结点**：当一个节点成功挖出一个新区块时，它应该将这个新区块广播到整个网络中，让其他节点都能接收到这个新区块。

5. **验证其它结点广播的区块**：当一个节点接收到其他节点广播的新区块时，它应该对这个新区块进行验证（例如检查哈希值是否满足特定条件）。如果验证通过，则将这个新区块加入本地的区块链中。

   当一个节点接收到其他节点广播的新区块时，它应该对这个新区块进行验证。验证过程通常包括以下几个步骤：

   1. **检查区块结构**：首先，你需要检查新区块的结构是否符合预期，例如它是否包含所有必要的字段（如时间戳、交易数据、前一个区块的哈希值等）。
   2. **检查前一个区块的哈希值**：其次，你需要检查新区块中存储的前一个区块的哈希值是否与本地区块链中最后一个区块的哈希值相同。
   3. **检查工作量证明**：然后，你需要检查新区块是否满足工作量证明算法的要求。具体来说，你需要计算新区块的哈希值，并检查它是否满足特定条件（例如以特定数量的零开头）。
   4. ~~**验证交易数据**：最后，你需要验证新区块中包含的所有交易数据。这一步通常涉及到对每笔交易进行签名验证、双花检测等操作。~~

   如果新区快通过了以上所有验证步骤，则说明它是有效的，并可以被加入到本地区块链中。否则，应该将其忽略并继续监听网络中其他节点广播的新区快。

6. **设计提供API服务**：你可以使用Java Web技术（如Servlet）来实现API服务。例如，你可以创建一个Servlet来处理获取所有区块数据的请求，并返回JSON格式的数据。

   

   <img src="https://raw.githubusercontent.com/GiyaYon/mypicGo/master/%E5%8C%BA%E5%9D%97%E9%93%BE%E7%B3%BB%E7%BB%9F%E8%AE%BE%E8%AE%A1%E2%80%94%E6%95%B0%E6%8D%AE%E4%BA%A4%E4%BA%92%E8%BF%87%E7%A8%8B.png" style="zoom: 80%;" />

   <center>本篇使用JAVA语言开发区块链系统</center>

   在基础层方面，构建基本区块结构，进行哈希处理，矿工挖矿的功能，后续将交易模块嵌入区块结构里面，而区块以区块组成的链作为账本记录。

# 1.定义区块结构

## 基础区块Block.java

```java
import com.giyaYon.arithmetic.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author GiyaYon
 */
public class Block {

    // 区块的序号
    public int index;
    /**
     * 哈希值，作为唯一标识
     */
    public String hash;
    /**
     * 上一个区块哈希值
     */
    public String previousHash;
    /**
     * 交易数据
     */
    private ArrayList<TransactionData> data;
    /**
     * 时间戳
     */
    private long timeStamp;
	//工作量证明
    private int nonce;
	// 矿工挖矿控制
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
```

在构造函数里，先对基础属性赋值，通过SHA256算法对数据进行`数字摘要加密`。

```java
    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
    }
```

矿工挖矿使用到的函数

```java
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
```



## 工具类StringUtil.java

这里给出SHA256算法使用方法

```java
import java.security.MessageDigest;


public class StringUtil {
    /**
     * 加密摘要
     * Applies Sha256 to a string and returns the result.
     * @param input
     * @return
     */
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            /**
             * This will contain hash as hexidecimal
             */
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

测试代码：

```java
@org.junit.Test
public void test01() {

        Block genesisBlock = new Block(0,new ArrayList<>(), "0");
        System.out.println("Hash for block 1 : " + genesisBlock.hash);

        Block secondBlock = new Block(1,new ArrayList<>(),genesisBlock.hash);
        System.out.println("Hash for block 2 : " + secondBlock.hash);

        Block thirdBlock = new Block(2,new ArrayList<>(),secondBlock.hash);
        System.out.println("Hash for block 3 : " + thirdBlock.hash);
        
}
```

结果：

```
Hash for block 1 : 73e659c6ee17b83f0d3209097facb1fe32ba3f2f857a03329be5cac533c5b362
Hash for block 2 : b608321771936423e99eeb93c74c524937131689f2a3d437891e2ecbd14ff404
Hash for block 3 : 02dc6bd5f9e1e11058fd5622e8e1150cf7085ae32c7de879982bf3381a3ce8b7
```

## 使用Gson类输出json格式信息

引入依赖

```maven
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

我们用ArrayList 将区块串起来，组成区块链

测试代码2：

```java
@org.junit.Test
public void test02() {
    ArrayList<Block> blockchain = new ArrayList<Block>();

    //add our blocks to the blockchain ArrayList:
    blockchain.add(new Block(0,new ArrayList<>(), "0"));
    blockchain.add(new Block(1,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
    blockchain.add(new Block(2,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));

    String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
    System.out.println(blockchainJson);

  }
```

结果：

```
[
  {
    "index": 0,
    "hash": "da5b9fb425a489c460488aeb4f7a0de0c8b81d4e87d0199af57932cc3cccb005",
    "previousHash": "0",
    "data": [],
    "timeStamp": 1679820296791,
    "nonce": 0,
    "running": true
  },
  {
    "index": 1,
    "hash": "1d4c0d0b25376d660e51d4a1cd3a5380a5e76bce0827fcf204bff9f4c0a53e10",
    "previousHash": "da5b9fb425a489c460488aeb4f7a0de0c8b81d4e87d0199af57932cc3cccb005",
    "data": [],
    "timeStamp": 1679820296823,
    "nonce": 0,
    "running": true
  },
  {
    "index": 2,
    "hash": "14dac1b905439bb19d639e094ea5858b26c7b186394194f542411314d877a0ad",
    "previousHash": "1d4c0d0b25376d660e51d4a1cd3a5380a5e76bce0827fcf204bff9f4c0a53e10",
    "data": [],
    "timeStamp": 1679820296823,
    "nonce": 0,
    "running": true
  }
]

Process finished with exit code 0

```

# 2.挖矿与验证

首先遍历链条中每个区块与它上一个的区块，验证：

1. 验证当前区块是不是被修改过数据后上传，上传者可能只修改了当前的区块的数据而不会去重新生成哈希。
2. 对比先前的区块哈希与当前区块里面链接的上一个区块的哈希是否一致
3. 对比这个区块是不是达成工作量要求

```java
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
```

测试：

```java
public class Test {
    
    ArrayList<Block> blockchain = new ArrayList<Block>();
    //难度系数
    int difficulty = 3;
    
    
    @org.junit.Test
    public void test03()
    {

        
        blockchain.add(new Block(0,new ArrayList<>(), "0"));
          System.out.println("Trying to Mine block 1... ");
          blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block(1,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
          System.out.println("Trying to Mine block 2... ");
          blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block(2,new ArrayList<>(),blockchain.get(blockchain.size()-1).hash));
          System.out.println("Trying to Mine block 3... ");
          blockchain.get(2).mineBlock(difficulty);

          System.out.println("\nBlockchain is Valid: " + isChainValid());

          String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
          System.out.println("\nThe block chain: ");
          System.out.println(blockchainJson);
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
```

打印：

```
Trying to Mine block 1... 
Block Mined!!! : 000cebc823ab29477def440c42e40cc8f2d6c95e9a70acaa194118b805fd3af0
Trying to Mine block 2... 
Block Mined!!! : 000078bd7a30e9062e47040a0c2e41132e8d7f0cfbc7aca8bbc7dd402d0a7490
Trying to Mine block 3... 
Block Mined!!! : 000f8c109c985348c4ed51f78405e2ddba8d365c14caf399cdd9e62f99566830

Blockchain is Valid: true

The block chain: 
[
  {
    "index": 0,
    "hash": "000cebc823ab29477def440c42e40cc8f2d6c95e9a70acaa194118b805fd3af0",
    "previousHash": "0",
    "data": [],
    "timeStamp": 1679821998773,
    "nonce": 2695,
    "running": true
  },
  {
    "index": 1,
    "hash": "000078bd7a30e9062e47040a0c2e41132e8d7f0cfbc7aca8bbc7dd402d0a7490",
    "previousHash": "000cebc823ab29477def440c42e40cc8f2d6c95e9a70acaa194118b805fd3af0",
    "data": [],
    "timeStamp": 1679821998850,
    "nonce": 1692,
    "running": true
  },
  {
    "index": 2,
    "hash": "000f8c109c985348c4ed51f78405e2ddba8d365c14caf399cdd9e62f99566830",
    "previousHash": "000078bd7a30e9062e47040a0c2e41132e8d7f0cfbc7aca8bbc7dd402d0a7490",
    "data": [],
    "timeStamp": 1679821998871,
    "nonce": 3385,
    "running": true
  }
]

```



# 3.通信实现

在网络的设计上，这里采用传统的CS模式来模拟两个点之间的网络行为，即特定客户端发送请求服务端，服务端回复。事实上P2P每个节点即是服务器也是客户端，而且后续改进可以从这里修改。



## 1.使用WebSocket实现服务器和客户端的网络通信基础模块，引入依赖

```
        <dependency>
            <groupId>org.java-websocket</groupId>
            <artifactId>Java-WebSocket</artifactId>
            <version>1.5.1</version>
        </dependency>
```

## 2.SocketServer.java

```java
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author GiyaYon
 * @code 网络模块 服务端
 */
public class SocketServer extends WebSocketServer {

    
    private List<WebSocket> list;
    
    public SocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // This method sends a message to the new client
        conn.send("Welcome to the server!");
        
        // This method sends a message to all clients connected
        broadcast(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
        
        list.add(conn);
        
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
        list.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        broadcast(conn + ":" + message);
        System.out.println(conn + ": " + message + "，修改前。");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific
            // websocket
            broadcast(conn + "has a error! it will be quit!");
            list.remove(conn);
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
        list = new ArrayList<>();
    }

    public void MsgBroadcast(String msg)
    {
        if(msg != null && list.size() > 1)
        {
            broadcast(msg);
        }
    }

}
```

## 3.socketClient.java

```java
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author GiyaYon
 * @code 网络模块，客户端
 */
public class SocketClient extends WebSocketClient {

    private List<WebSocket> list = new ArrayList<>();

    public List<WebSocket> getList() {
        return list;
    }

    public void setList(List<WebSocket> list) {
        this.list = list;
    }

    public SocketClient(URI serverUri) {
        super(serverUri);
    }

    public SocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        list.add(this);
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        list.remove(this);
    }

    @Override
    public void onError(Exception e) {
        list.remove(this);
    }

    public void sendMessage(String msg)
    {
        if(msg != null) {
            this.send(msg);
        }
    }
}
```

测试：

```java
@org.junit.Test
public void server() {

    int port = 8888;
    try {
        SocketServer server = new SocketServer(port);
        server.start();
    } catch (UnknownHostException e) {
        throw new RuntimeException(e);
    }
    Scanner scanner = new Scanner(System.in);
    String wait = scanner.nextLine();

}
```

```java
//  ws://localhost:8888
    @org.junit.Test
    public void client()
    {
        SocketClient client = null;
        try {
            client = new SocketClient(new URI("ws://localhost:8888"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        client.connect();

    }
```

使用http://coolaf.com/tool/chattest 测试服务端是否开启成功 `ws://localhost:8888`

打印：

```
Server started!
0:0:0:0:0:0:0:1 entered the room!
127.0.0.1 entered the room!
```

```
Welcome to the server!
127.0.0.1 entered the room!
```

```
连接成功，现在你可以发送信息啦！！！
服务端回应 2023-03-26 17:30:31
Welcome to the server!
服务端回应 2023-03-26 17:30:31
0:0:0:0:0:0:0:1 entered the room!
服务端回应 2023-03-26 17:33:25
127.0.0.1 entered the room!
websocket连接已断开!!!
```



# 4.网络服务实现

## 1.消息实现

首先需要对消息进行定义处理，例如A节点对B节点发送区块链更新请求消息，或者是C节点挖到一个矿，想要对其进行广播，其他节点收到消息会对其进行处理。



定义消息模型

```java
/**
 * @author GiyaYon
 */
public class Message {
    private int type;
    private String data;

    public Message(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
```

定义消息类型

```java
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Network: BLOCK_PROTOCOL
 * @author GiyaYon
 */
public class BlockConstant {
    public final static int QUERY_LATEST_BLOCK = 1;
    public final static int QUERY_BLOCKCHAIN = 2;
    public final static int QUERY_LATEST_TRANSACTION = 3;
    public final static int UPLOAD_MINED_BLOCK = 4;
    public final static int RETURN_LATEST_BLOCK = 5;
    public final static int RETURN_BLOCKCHAIN = 6;

    public static  <T> T simpleJsonToObj(String json, Class<T> cls) {
        Gson gson = new Gson();
        if (Objects.isNull(json)) return null;
        T obj = gson.fromJson(json, cls);
        if (Objects.isNull(obj)) {
            return null;
        } else {
            return obj;
        }
    }

    public static String simpleObjToJson(Object obj) {
        if (Objects.isNull(obj)) return "";
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
```

## 2.定义接口

节点通用的方法



定义节点接口

```java
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
```

```java
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
```

```java
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
```

## 3.实现简易消息处理的服务端

首先，对SocketServer的onMessage进行重写

```java
                server = new SocketServer(port)
            {
                public void onMessage(WebSocket conn, String message)
                {

                    handleMessage(conn,message);

//                    broadcast(conn + ":" + message);
//                    System.out.println(conn + ": " + message + "，修改后。");
                }
            };
```

其次，定义消息接收后的处理方法

```java
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
```



完整代码ServiceServer.java

```java
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
```

## 4.实现客户端

客户端实现包括两个，第一是被动的网络服务，第二是用户行为主动服务

1.网络服务ConnectService.java

```java
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
```



2.用户服务UserService.java

```java
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
```

对两个服务进行整合 ServiceClient.java

```java
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
```

测试：

```java
@org.junit.Test
public void test05()
{
    ServiceServer server = new ServiceServer(8888);
    server.runServer();
}
```

```java
package com.giyaYon.Appliaction;

import com.giyaYon.Service.ServiceClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ClientProgarm {

    public static void main(String args[]) throws URISyntaxException, IOException, InterruptedException {


        String peer = "ws://localhost:8888";
        ServiceClient serviceClient = new ServiceClient(peer);
        serviceClient.runClientServer();

        Scanner scan = new Scanner(System.in);
        while (true)
        {
            System.out.println(
                    """
                            ===================================
                            please typing code what you want to do:
                            1.exit system
                            2.mine block
                            3.update_block
                            ===================================
                            """);
            if (scan.hasNext()) {
                String str1 = scan.next();
                if(str1.equals("1"))
                {
                    System.out.println("exiting...");
                    scan.close();
                    serviceClient.exitClientServer();
                    break;
                }
                if(str1.equals("2"))
                {
                    System.out.println("try to mine block...");
                    serviceClient.userService.tryToMineBlock();
                }
                if(str1.equals("3"))
                {
                    serviceClient.connection.queryChainFromOthers();
                }
            }

        }
        //serviceClient.userService.tryToMineBlock();

//        Thread.sleep(100);
//
//        serviceClient.userService.tryToMineBlock();

    }



}
```

打印：

服务端：

```
ChatServer started on port: 8888
Server started!
127.0.0.1 entered the room!
{"type":4,"data":"{\"index\":0,\"hash\":\"000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39\",\"previousHash\":\"0\",\"data\":[],\"timeStamp\":1679825452774,\"nonce\":1769903,\"running\":true}"}
{"type":4,"data":"{\"index\":1,\"hash\":\"0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8\",\"previousHash\":\"000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39\",\"data\":[],\"timeStamp\":1679825465536,\"nonce\":8809650,\"running\":true}"}
/127.0.0.1:8738: QUERY_BLOCK_CHAIN:{"index":0,"hash":"000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39","previousHash":"0","data":[],"timeStamp":1679825452774,"nonce":1769903,"running":true}|{"index":1,"hash":"0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8","previousHash":"000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39","data":[],"timeStamp":1679825465536,"nonce":8809650,"running":true}

```

客户端A：

```
===================================
please typing code what you want to do:
1.exit system
2.mine block
3.update_block
===================================

2
try to mine block...
Block Mined!!! : 000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39
��0����000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39
===================================
please typing code what you want to do:
1.exit system
2.mine block
3.update_block
===================================

i received the new block:Block{index=0, hash='000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39', previousHash='0', data=[], timeStamp=1679825452774, nonce=1769903}
2
try to mine block...
Block Mined!!! : 0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8
��1����0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8
===================================
please typing code what you want to do:
1.exit system
2.mine block
3.update_block
===================================

i received the new block:Block{index=1, hash='0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8', previousHash='000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39', data=[], timeStamp=1679825465536, nonce=8809650}
3
===================================
please typing code what you want to do:
1.exit system
2.mine block
3.update_block
===================================

Block{index=0, hash='000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39', previousHash='0', data=[], timeStamp=1679825452774, nonce=1769903}
Block{index=1, hash='0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8', previousHash='000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39', data=[], timeStamp=1679825465536, nonce=8809650}

```

客户端B：

```
===================================
please typing code what you want to do:
1.exit system
2.mine block
3.update_block
===================================

3
===================================
please typing code what you want to do:
1.exit system
2.mine block
3.update_block
===================================

Block{index=0, hash='000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39', previousHash='0', data=[], timeStamp=1679825452774, nonce=1769903}
Block{index=1, hash='0000004fd1849958d1cc5045b41f6bda486e2ab6b0fc69211a2e6de1254027c8', previousHash='000000c257847251e823c5148046dec1bd0fbdf5aa6d1ecb8d5eba22fd217d39', data=[], timeStamp=1679825465536, nonce=8809650}

```



# 5.实现Web API查询服务

1.需要引入JAVA EE Web框架

2.需要部署Tomcat服务器

3.需要检查是否引入jar包到Web-INF 的lib

4.使用运行之前需要打开ServiceServer 、ServiceClient，并且让ServiceClient挖好矿之后再进行查询测试

```java
package com.giyaYon.Network;

import com.giyaYon.Service.ServiceClient;
import org.apache.commons.text.StringEscapeUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@javax.servlet.annotation.WebServlet("/hello")
public class WebServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("hello world");
        String peer = "ws://localhost:8888";

        ServiceClient serviceClient = new ServiceClient(peer);
        serviceClient.runClientServer();
        serviceClient.connection.queryChainFromOthers();

        try {
            Thread.sleep(500);
        }catch (Exception e)
        {
        }
        String blocks = serviceClient.connection.queryChainFromLocal();
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<p>"+ blocks +"</p>");

        serviceClient.exitClientServer();

        System.out.println(blocks);
    }

}
```

测试：

在localhost:8080/CoinBlock_war_exploded/ 后面输入hello

<img src="https://raw.githubusercontent.com/GiyaYon/mypicGo/master/api.png" style="zoom:80%;" />

# 后言

后续将扩充交易信息内容模块，针对交易过程，设计Web电子商务系统，完善整个区块链系统。

参考文章：

https://www.cnblogs.com/helloworld2018/p/9011369.html java开发区块链只需150行代码 /[以太坊开发](https://home.cnblogs.com/u/helloworld2018/)/2018-07-19

https://cloud.tencent.com/developer/article/1776246 基于Java开发一套完整的区块链系统（附源码）/ [luckytuan](https://gitee.com/luckytuan?utm_source=poper_profile)/2021-01-20

