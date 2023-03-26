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
