package org.jansdhillon;

import java.net.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Player {
    private int messagesReceived;
    private int messagesSent;

    Player() throws IOException {
        PlayerServer server = new PlayerServer();
        Thread serverThread = new Thread(() -> {
            try {
                server.start(6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        PlayerClient client = new PlayerClient();
        client.startConnection("127.0.0.1", 6666);
        String response = client.sendMessage("hello server");
        assertEquals("hello client", response);

        client.stopConnection();
        server.stop();
    }

    private static class PlayerServer {
        private ServerSocket serverSocket;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void start(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String greeting = in.readLine();
            if ("hello server".equals(greeting)) {
                out.println("hello client");
            } else {
                out.println("unrecognised greeting");
            }
        }

        public void stop() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }
    }

    private static class PlayerClient {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void startConnection(String ip, int port) throws IOException {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public String sendMessage(String msg) throws IOException {
            out.println(msg);
            return in.readLine();
        }

        public void stopConnection() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }
    }

    public static void main(String[] args) {
        try {
            new Player();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
