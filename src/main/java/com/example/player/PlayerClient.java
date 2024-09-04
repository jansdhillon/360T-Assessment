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
        // Add a timeout to the socket (5 seconds)
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(ip, port), 5000); // Connect with a timeout
        clientSocket.setSoTimeout(5000); // Set read timeout
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        incrementMessagesSent();
        System.out.println("Message sent: " + msg);

        try {
            // Attempt to read the response, with a timeout
            String response = in.readLine();
            if (response == null) {
                throw new SocketTimeoutException("No response from server.");
            }
            return response;
        } catch (SocketTimeoutException e) {
            System.err.println("Timeout while waiting for server response.");
            return "No response from server.";
        }
    }

    public void stopConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
    }
}