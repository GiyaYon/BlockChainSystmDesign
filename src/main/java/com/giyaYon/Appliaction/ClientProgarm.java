package com.giyaYon.Appliaction;

import com.giyaYon.Service.ServiceClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ClientProgarm {

    public static void main(String args[]) throws URISyntaxException, IOException, InterruptedException {


        String peer = "ws://localhost:8888";
//
//        final SocketClient client = new SocketClient(new URI(peer));
//        client.connect();
//
//


//        String peer = "ws://localhost:8888";
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
