package com.example.player;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class PlayerClient {
    private int messagesSent = 0;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public synchronized void incrementMessagesSent() {
        this.messagesSent++;
    }

    public synchronized int getMessagesSent() {
        return this.messagesSent;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(ip, port), 5000); // 5-second connection timeout
        clientSocket.setSoTimeout(5000); // 5-second read timeout
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        if (out == null) {
            throw new IOException("Client connection not initialized. Cannot send message.");
        }

        out.println(msg);
        incrementMessagesSent();

        String response = in.readLine();
        if (response == null) {
            throw new IOException("No response from server.");
        }
        return response;
    }

    public void stopConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
    }
}
