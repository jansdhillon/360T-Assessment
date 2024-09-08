package com.example.player;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerServer {
    private String name;
    private final int messageLimit;
    private final AtomicInteger messagesReceived = new AtomicInteger(0);
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public PlayerServer(String name, int messageLimit) {
        this.name = name;
        this.messageLimit = messageLimit;
    }

    public void start(int port) throws IOException {
        System.out.println(name + " server starting on port " + port);
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            System.out.println("Client connected to " + name);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (messagesReceived.get() < messageLimit) {
                try {
                    String receivedMessage = in.readLine();
                    if (receivedMessage == null) break;
                    incrementMessagesReceived();
                    String response = receivedMessage + " (message #" + messagesReceived.get() + ")";
                    out.println(response);
                } catch (IOException e) {
                    System.out.println("Stream closed or error occurred: " + e.getMessage());
                    break;
                }
            }
        } finally {
            stop();
        }
    }

    public void stop() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
        if (serverSocket != null) serverSocket.close();
    }

    public void incrementMessagesReceived() {
        messagesReceived.incrementAndGet();
    }

    public int getMessagesReceived() {
        return messagesReceived.get();
    }
}
