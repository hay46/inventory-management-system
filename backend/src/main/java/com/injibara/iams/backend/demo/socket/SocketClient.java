package com.injibara.iams.backend.demo.socket;

import java.io.*;
import java.net.*;

public class SocketClient {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("Hello from IAMS client");
            System.out.println("Client received: " + in.readLine());
            out.println("bye");
            System.out.println("Client received: " + in.readLine());
        }
    }
}
