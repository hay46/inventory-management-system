package com.injibara.iams.backend.demo.socket;

import java.io.*;
import java.net.*;

public class SocketServer {
    public static void main(String[] args) throws IOException {
        int port = 5000;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Socket server listening on port " + port);
            try (Socket client = server.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Server received: " + line);
                    out.println("Echo: " + line);
                    if ("bye".equalsIgnoreCase(line)) break;
                }
            }
        }
        System.out.println("Socket server stopped.");
    }
}
